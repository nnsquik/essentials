package com.monkey.monkey;

import com.monkey.monkey.chat.ChatMod;
import com.monkey.monkey.commands.*;
import com.monkey.monkey.gui.TabListMod;
import com.monkey.monkey.keybinds.SprintHandler;
import com.monkey.monkey.keybinds.KeyHandler;
import com.monkey.monkey.mm.MurderMystery;
import com.monkey.monkey.modules.BossBarHider;
import com.monkey.monkey.modules.FullBrightEventHandler;
import com.monkey.monkey.modules.ZoomSensitivity;
import com.monkey.monkey.utils.ServerChecker;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "monkey", name = "monkey", version = "1.0")
public final class App {
    private SprintHandler sprintHandler;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        sprintHandler = new SprintHandler();

        MinecraftForge.EVENT_BUS.register(sprintHandler);
        MinecraftForge.EVENT_BUS.register(new KeyHandler());
        KeyHandler.register();
        MinecraftForge.EVENT_BUS.register(new ServerChecker());
        MinecraftForge.EVENT_BUS.register(new MurderMystery());
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        registerEvents();
        registerCommands();
    }

    private void registerCommands() {
        ClientCommandHandler.instance.registerCommand(new SprintCommand(sprintHandler));
        ClientCommandHandler.instance.registerCommand(new CopyCommand());
        ClientCommandHandler.instance.registerCommand(new ClearCommand());
        ClientCommandHandler.instance.registerCommand(new FovCommand());
        ClientCommandHandler.instance.registerCommand(new MonkeyCommand());
        ClientCommandHandler.instance.registerCommand(new TranslateCommand());
        ClientCommandHandler.instance.registerCommand(new TranslateMessageCommand());
    }

    private void registerEvents() {
        MinecraftForge.EVENT_BUS.register(new BossBarHider());
        MinecraftForge.EVENT_BUS.register(new ZoomSensitivity());
        MinecraftForge.EVENT_BUS.register(new FullBrightEventHandler());
        MinecraftForge.EVENT_BUS.register(new TabListMod());
        MinecraftForge.EVENT_BUS.register(new ChatMod());
    }
}

