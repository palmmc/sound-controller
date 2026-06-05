package com.bvengo.soundcontroller;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;
import com.bvengo.soundcontroller.mixin.SoundManagerAccessor;
import com.bvengo.soundcontroller.mixin.SoundEngineAccessor;

public class Utils {
	public static void updateExistingSounds() {
        SoundManagerAccessor managerAccessor = (SoundManagerAccessor) Minecraft.getInstance().getSoundManager();
        SoundEngineAccessor engineAccessor = (SoundEngineAccessor) managerAccessor.getSoundEngine();
        engineAccessor.invokeUpdateCategoryVolume(SoundSource.AMBIENT, 1.0f);
	}

    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) return "";
        String[] words = str.split("_");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) continue;
            sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
        }
        return sb.toString().trim();
    }
}
