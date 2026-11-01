package com.lilsort.easyauthextension;

import net.fabricmc.api.ModInitializer;

import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import com.lilsort.easyauthextension.commands.ExtensionCommands;
import com.lilsort.easyauthextension.config.ExtensionConfig;

public class EasyAuthExtension implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final String MOD_ID = "easyauthextension";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		ExtensionConfig.load();
		CommandRegistrationCallback.EVENT.register(ExtensionCommands::register);

		LOGGER.info("EasyAuthExtension initialized!");
	}

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}
}
