package net.xenyria.eem;

import net.xenyria.eem.discord.DiscordRichPresenceIntegration;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.xenyria.eem.utils.Keybinds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XenyriaExperienceEnhancementMod implements ClientModInitializer {

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("Xenyria/EEM");

	@Override
	public void onInitializeClient() {
		LOGGER.info("Xenyria Mod has been initialized.");
		try {
			LOGGER.info("Attempting to initialize Discord Integration...");
			DiscordRichPresenceIntegration.createInstance();
			LOGGER.info("Discord Integration has been successfully initialized.");

			LOGGER.info("Registering keybinds...");
			Keybinds.register();
			LOGGER.info("Keybinds have been successfully registered.");
		} catch (IllegalStateException exception) {
			LOGGER.error("Discord integration could not be initialized: " + exception.getMessage());
		}
	}
}
