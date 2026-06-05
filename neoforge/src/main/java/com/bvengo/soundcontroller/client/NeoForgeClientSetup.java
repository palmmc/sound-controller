package com.bvengo.soundcontroller.client;

import com.bvengo.soundcontroller.SoundReloadListener;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

public class NeoForgeClientSetup {

    @SubscribeEvent
    public static void onRegisterReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new SoundReloadListener());
    }
}
