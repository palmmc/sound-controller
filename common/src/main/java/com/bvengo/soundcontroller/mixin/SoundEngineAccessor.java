package com.bvengo.soundcontroller.mixin;

import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SoundEngine.class)
public interface SoundEngineAccessor {
    @Invoker("updateCategoryVolume")
    void invokeUpdateCategoryVolume(SoundSource category, float volume);
}
