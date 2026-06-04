package com.bvengo.soundcontroller.platform;

import com.bvengo.soundcontroller.Utils;
import com.bvengo.soundcontroller.platform.services.IPlatformHelper;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import java.nio.file.Path;

public class NeoForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLEnvironment.isProduction();
    }

    @Override
    public Path getConfigFolder() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public String getModName(String modId) {
        if ("minecraft".equals(modId)) return "Minecraft";
        return ModList.get().getModContainerById(modId)
                .map(container -> container.getModInfo().getDisplayName())
                .orElse(Utils.capitalize(modId));
    }
}
