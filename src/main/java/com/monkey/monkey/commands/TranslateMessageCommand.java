package com.monkey.monkey.commands;

import com.monkey.monkey.chat.MessageStore;
import com.monkey.monkey.utils.Translator;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class TranslateMessageCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "trmsg";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/trmsg <id>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length != 1) {
            return;
        }

        String original;
        try {
            original = MessageStore.get(Integer.parseInt(args[0]));
        } catch (NumberFormatException e) {
            return;
        }

        if (original == null) {
            sender.addChatMessage(new ChatComponentText(
                    EnumChatFormatting.RED + "[T] message has already been removed from the cache"));
            return;
        }

        Translator.translate(original, "auto", "ru",
                result -> Translator.printToClient(new ChatComponentText(
                        EnumChatFormatting.AQUA + "[T] " + EnumChatFormatting.WHITE + result)),
                error -> Translator.printToClient(new ChatComponentText(
                        EnumChatFormatting.RED + "[T] translate error: " + error)));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}