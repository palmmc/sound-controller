package com.bvengo.soundcontroller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.bvengo.soundcontroller.config.VolumeConfig;

public class SoundController {
	public static final String MOD_ID = "soundcontroller";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static VolumeConfig CONFIG;

	public static void init() {
		LOGGER.info("{} loaded.", LOGGER.getName());
	}
}
