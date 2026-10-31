package com.monkey.monkey.commands;

import com.monkey.monkey.utils.Translator;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

public class TranslateCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "tr";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/tr <сообщение>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "use: /tr <message>"));
            return;
        }

        String text = String.join(" ", args);

        Translator.translate(text, "ru", "en",
                result -> {
                    String chatText = Translator.toChatStyle(result);

                    ChatComponentText message = new ChatComponentText(
                            EnumChatFormatting.AQUA + "[tr] " + EnumChatFormatting.WHITE + chatText);
                    message.setChatStyle(new ChatStyle()
                            .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/copy " + chatText)));
                    Translator.printToClient(message);
                },
                error -> Translator.printToClient(new ChatComponentText(
                        EnumChatFormatting.RED + "[tr] translate error: " + error)));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}