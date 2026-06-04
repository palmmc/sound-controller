package com.bvengo.soundcontroller;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.server.packs.PackType;

public class SoundControllerFabric implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		// Initialize common logic
		SoundController.init();

		ResourceLoader.get(PackType.CLIENT_RESOURCES)
			.registerReloadListener(SoundReloadListener.ID, new SoundReloadListener());
	}
}
