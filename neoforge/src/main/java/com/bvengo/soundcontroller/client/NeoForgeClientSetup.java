package com.bvengo.soundcontroller.client;

import com.bvengo.soundcontroller.SoundReloadListener;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;

public class NeoForgeClientSetup {

    @SubscribeEvent
    public static void onRegisterReloadListeners(AddClientReloadListenersEvent event) {
        event.addListener(SoundReloadListener.ID, new SoundReloadListener());
    }
}
