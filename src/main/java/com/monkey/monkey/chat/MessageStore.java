package com.monkey.monkey.chat;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public final class MessageStore {
    private static final int MAX_MESSAGES = 300;
    private static final AtomicInteger COUNTER = new AtomicInteger();

    private static final Map<Integer, String> MESSAGES = Collections.synchronizedMap(
            new LinkedHashMap<Integer, String>() {
                @Override
                protected boolean removeEldestEntry(Map.Entry<Integer, String> eldest) {
                    return size() > MAX_MESSAGES;
                }
            });

    private MessageStore() {
    }

    public static int put(String text) {
        int id = COUNTER.incrementAndGet();
        MESSAGES.put(id, text);
        return id;
    }

    public static String get(int id) {
        return MESSAGES.get(id);
    }
}
