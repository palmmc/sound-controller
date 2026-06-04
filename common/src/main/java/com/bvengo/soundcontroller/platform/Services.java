package com.bvengo.soundcontroller.platform;

import com.bvengo.soundcontroller.SoundController;
import com.bvengo.soundcontroller.platform.services.IPlatformHelper;
import java.util.ServiceLoader;

public class Services {

    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);

    public static <T> T load(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz, Services.class.getClassLoader())
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        return loadedService;
    }
}
