package com.bvengo.soundcontroller;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;

public class SoundControllerFabric implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		// Initialize common logic
		SoundController.init();

		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Override
			public ResourceLocation getFabricId() {
				return SoundReloadListener.ID;
			}

			@Override
			public void onResourceManagerReload(ResourceManager manager) {
				new SoundReloadListener().onResourceManagerReload(manager);
			}
		});
	}
}
