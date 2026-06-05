package com.bvengo.soundcontroller;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import com.bvengo.soundcontroller.gui.AllSoundOptionsScreen;
import com.bvengo.soundcontroller.client.NeoForgeClientSetup;
import net.minecraft.client.Minecraft;

@Mod(SoundController.MOD_ID)
public class SoundControllerNeo {

    public SoundControllerNeo(IEventBus eventBus, ModContainer container) {
        // Initialize common logic
        SoundController.init();

        if (FMLEnvironment.dist == Dist.CLIENT) {
            // Register neoforge config screen
            container.registerExtensionPoint(IConfigScreenFactory.class, (modContainer, parentScreen) -> {
                return new AllSoundOptionsScreen(parentScreen, Minecraft.getInstance().options);
            });

            eventBus.register(NeoForgeClientSetup.class);
        }
    }
}
