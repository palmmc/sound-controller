package com.bvengo.soundcontroller.platform.services;

import java.nio.file.Path;

public interface IPlatformHelper {

    String getPlatformName();

    boolean isModLoaded(String modId);

    boolean isDevelopmentEnvironment();

    Path getConfigFolder();

    String getModName(String modId);
}
