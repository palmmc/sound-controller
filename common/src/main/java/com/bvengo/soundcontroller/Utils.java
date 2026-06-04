package com.bvengo.soundcontroller;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;

public class Utils {
	public static void updateExistingSounds() {
        Minecraft.getInstance().getSoundManager().refreshCategoryVolume(SoundSource.AMBIENT);
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
