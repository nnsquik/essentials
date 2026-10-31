package com.monkey.monkey.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IChatComponent;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public final class Translator {
    // Google Cloud Translation API
    private static final String OFFICIAL_API_KEY = "";

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(2, r -> {
        Thread t = new Thread(r, "monkey-translator");
        t.setDaemon(true);
        return t;
    });

    private static final Map<String, String> CACHE = Collections.synchronizedMap(
            new LinkedHashMap<String, String>() {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
                    return size() > 200;
                }
            });

    private Translator() {
    }

    /** Асинхронный перевод. Колбэки вызываются в основном потоке игры. */
    public static void translate(String text, String from, String to,
                                 Consumer<String> onSuccess, Consumer<String> onError) {
        String cacheKey = from + "|" + to + "|" + text;
        String cached = CACHE.get(cacheKey);
        if (cached != null) {
            onSuccess.accept(cached);
            return;
        }

        EXECUTOR.submit(() -> {
            try {
                String result = request(text, from, to);
                CACHE.put(cacheKey, result);
                runOnMainThread(() -> onSuccess.accept(result));
            } catch (Exception e) {
                runOnMainThread(() -> onError.accept(e.getMessage() != null
                        ? e.getMessage() : e.getClass().getSimpleName()));
            }
        });
    }

    /** Пробует сервисы по очереди, пока один не ответит. */
    private static String request(String text, String from, String to) throws Exception {
        StringBuilder errors = new StringBuilder();

        if (!OFFICIAL_API_KEY.isEmpty()) {
            try {
                return requestOfficial(text, from, to);
            } catch (Exception e) {
                errors.append("official: ").append(e.getMessage()).append("; ");
            }
        }

        String[][] gtxEndpoints = {
                {"googleapis", "https://translate.googleapis.com/translate_a/single?client=gtx&dt=t"},
                {"google.com", "https://translate.google.com/translate_a/single?client=gtx&dt=t"}
        };
        for (String[] endpoint : gtxEndpoints) {
            try {
                return requestGtx(endpoint[1], text, from, to);
            } catch (Exception e) {
                errors.append(endpoint[0]).append(": ").append(e.getMessage()).append("; ");
            }
        }

        try {
            return requestClients5(text, from, to);
        } catch (Exception e) {
            errors.append("clients5: ").append(e.getMessage()).append("; ");
        }

        try {
            return requestMyMemory(text, from, to);
        } catch (Exception e) {
            errors.append("mymemory: ").append(e.getMessage()).append("; ");
        }

        throw new IllegalStateException(errors.toString().trim());
    }

    private static String requestGtx(String endpoint, String text, String from, String to)
            throws Exception {
        String url = endpoint + "&sl=" + from + "&tl=" + to
                + "&q=" + URLEncoder.encode(text, "UTF-8");

        JsonElement root = fetchJson(url);
        JsonArray segments = root.getAsJsonArray().get(0).getAsJsonArray();

        StringBuilder sb = new StringBuilder();
        for (JsonElement segment : segments) {
            if (!segment.isJsonArray()) {
                continue;
            }
            JsonElement part = segment.getAsJsonArray().get(0);
            if (part != null && !part.isJsonNull()) {
                sb.append(part.getAsString());
            }
        }
        return sb.toString();
    }

    private static String requestClients5(String text, String from, String to) throws Exception {
        String url = "https://clients5.google.com/translate_a/t?client=dict-chrome-ex"
                + "&sl=" + from + "&tl=" + to
                + "&q=" + URLEncoder.encode(text, "UTF-8");

        JsonArray root = fetchJson(url).getAsJsonArray();

        StringBuilder sb = new StringBuilder();
        for (JsonElement item : root) {
            if (item.isJsonArray()) {
                sb.append(item.getAsJsonArray().get(0).getAsString());
            } else if (item.isJsonPrimitive()) {
                sb.append(item.getAsString());
            }
        }
        return sb.toString();
    }

    /** MyMemory: без ключа, ~5000 символов в день. */
    private static String requestMyMemory(String text, String from, String to) throws Exception {
        String source = "auto".equals(from) ? "autodetect" : from;
        String url = "https://api.mymemory.translated.net/get"
                + "?q=" + URLEncoder.encode(text, "UTF-8")
                + "&langpair=" + source + "|" + to;

        JsonObject root = fetchJson(url).getAsJsonObject();

        int status = root.get("responseStatus").getAsInt();
        if (status != 200) {
            throw new IllegalStateException("HTTP " + status);
        }

        String translated = root.getAsJsonObject("responseData").get("translatedText").getAsString();
        return translated.replace("&#39;", "'").replace("&quot;", "\"")
                .replace("&amp;", "&").replace("&lt;", "<").replace("&gt;", ">");
    }

    private static String requestOfficial(String text, String from, String to) throws Exception {
        String url = "https://translation.googleapis.com/language/translate/v2"
                + "?key=" + OFFICIAL_API_KEY
                + "&target=" + to
                + ("auto".equals(from) ? "" : "&source=" + from)
                + "&format=text"
                + "&q=" + URLEncoder.encode(text, "UTF-8");

        return fetchJson(url).getAsJsonObject()
                .getAsJsonObject("data").getAsJsonArray("translations")
                .get(0).getAsJsonObject().get("translatedText").getAsString();
    }

    private static JsonElement fetchJson(String url) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
                        + "(KHTML, like Gecko) Chrome/120.0 Safari/537.36");
        try {
            int code = connection.getResponseCode();
            if (code != 200) {
                throw new IllegalStateException("HTTP " + code);
            }
            try (InputStreamReader reader = new InputStreamReader(connection.getInputStream(), "UTF-8")) {
                return new JsonParser().parse(reader);
            }
        } finally {
            connection.disconnect();
        }
    }

    public static String toChatStyle(String text) {
        String s = text.trim().replace("'", "").replace("’", "");
        if (s.endsWith(".")) {
            s = s.substring(0, s.length() - 1);
        }
        if (!s.isEmpty()) {
            s = Character.toLowerCase(s.charAt(0)) + s.substring(1);
        }
        return s;
    }

    public static void printToClient(IChatComponent component) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null) {
            mc.thePlayer.addChatMessage(component);
        }
    }

    private static void runOnMainThread(Runnable runnable) {
        Minecraft.getMinecraft().addScheduledTask(runnable);
    }
}