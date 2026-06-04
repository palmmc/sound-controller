package com.bvengo.soundcontroller.platform;

import com.bvengo.soundcontroller.Utils;
import com.bvengo.soundcontroller.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;
import java.nio.file.Path;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public Path getConfigFolder() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public String getModName(String modId) {
        if ("minecraft".equals(modId)) return "Minecraft";
        return FabricLoader.getInstance().getModContainer(modId)
                .map(container -> container.getMetadata().getName())
                .orElse(Utils.capitalize(modId));
    }
}
