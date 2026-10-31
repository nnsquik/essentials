package com.monkey.monkey.chat;

import net.minecraft.event.ClickEvent;
import net.minecraft.util.*;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChatMod {
    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        if (event.type == 2) {
            return;
        }

        String plain = event.message.getUnformattedText();
        if (plain.trim().isEmpty()) {
            return;
        }

        addTranslateButton(event.message, plain);
        applyCopy(event.message, plain);
    }

    private void addTranslateButton(IChatComponent message, String plain) {
        int id = MessageStore.put(plain);

        ChatComponentText button = new ChatComponentText(" [T]");
        button.setChatStyle(new ChatStyle()
                .setColor(EnumChatFormatting.AQUA)
                .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/trmsg " + id)));
        message.appendSibling(button);
    }

    private void applyCopy(IChatComponent component, String text) {
        ChatStyle style = component.getChatStyle();

        if (style.getChatClickEvent() == null) {
            style.setChatClickEvent(new ClickEvent(
                    ClickEvent.Action.RUN_COMMAND, "/copy " + text));
        }

        for (Object sibling : component.getSiblings()) {
            applyCopy((IChatComponent) sibling, text);
        }
    }
}
