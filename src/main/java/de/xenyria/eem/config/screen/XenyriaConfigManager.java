package de.xenyria.eem.config.screen;

import de.xenyria.eem.XenyriaExperienceEnhancementMod;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class XenyriaConfigManager {

    public static final Logger LOGGER = LoggerFactory.getLogger("EEM/Settings");

    private static final File configFolder = new File("config/xenyria");
    private static final File configFile = new File(configFolder, "xenyria_eem.config.json");

    private static XenyriaConfig config;
    public static XenyriaConfig getConfig() { return config; }

    public static void loadConfig() throws IOException, JSONException {
        if(!configFile.exists()) {
            // Use default config
            config = new XenyriaConfig();
        }

        try(Scanner scanner = new Scanner(configFile)) {
            StringBuilder jsonStringBuilder = new StringBuilder();
            while (scanner.hasNextLine()) {
                jsonStringBuilder.append(scanner.nextLine());
            }
            config = XenyriaConfig.load(new JSONObject(jsonStringBuilder.toString()));
        }
    }

    public static void saveConfig() throws IOException, IllegalStateException{
        if (!configFolder.exists()) {
            configFolder.mkdirs();
        }

        if(!configFile.exists()) {
            try {
                if(!configFile.createNewFile()) { throw new IOException("Config file couldn't be created."); }
            } catch (IOException exception) {
                throw new IOException("Couldn't save the config file for Xenyria EEM.");
            }
        }

        String configData = config.toJSON().toString(4);
        try(FileWriter writer = new FileWriter(configFile)) {
            writer.write(configData);
        }

        if(!configFolder.isDirectory()) {
            throw new IllegalStateException("Config folder has to be a folder, not a file.");
        }
    }

    public static ConfigBuilder getConfigurationBuilder() {

        ConfigBuilder configBuilder = ConfigBuilder.create()
                .setTitle(Text.translatable("text.xenyria.settings.title"))
                .setEditable(true)
                .setTransparentBackground(true)
                .setSavingRunnable(() -> {
                    try {
                        saveConfig();
                    } catch (Exception e) {
                        LOGGER.error("Couldn't save config: " + e.getMessage());
                    }
                });

        ConfigCategory discordCategory
                = configBuilder.getOrCreateCategory(Text.translatable("text.xenyria.settings.discord.title"));
        discordCategory.addEntry(
                configBuilder.entryBuilder()
                        .startBooleanToggle(
                                Text.translatable("text.xenyria.settings.discord.rich_presence"),
                                config.enableDiscordRichPresence
                        )
                        .setDefaultValue(true)
                        .setTooltip(Text.translatable("text.xenyria.settings.discord.rich_presence.tooltip"))
                        .setSaveConsumer((newState) -> config.enableDiscordRichPresence = newState)
                        .build()
        );
        discordCategory.addEntry(
                configBuilder.entryBuilder()
                        .startBooleanToggle(
                                Text.translatable("text.xenyria.settings.discord.share_server_activity"),
                                config.enableDiscordRichPresence
                        )
                        .setDefaultValue(true)
                        .setTooltip(Text.translatable("text.xenyria.settings.discord.share_server_activity.tooltip"))
                        .setSaveConsumer((newState) -> config.shareServerActivity = newState)
                        .build()
        );

        ConfigCategory paintSquadCategory
                = configBuilder.getOrCreateCategory(Text.translatable("text.xenyria.settings.paintsquad.title"));
        paintSquadCategory.addEntry(
                configBuilder.entryBuilder()
                        .startBooleanToggle(
                                Text.translatable("text.xenyria.settings.paintsquad.swim_cam"),
                                config.enableDiscordRichPresence
                        )
                        .setDefaultValue(true)
                        .setTooltip(Text.translatable("text.xenyria.settings.paintsquad.swim_cam.tooltip"))
                        .setSaveConsumer((newState) -> config.swimFormCameraForPaintSquad = newState)
                        .build()
        );
        paintSquadCategory.addEntry(
                configBuilder.entryBuilder()
                        .startBooleanToggle(
                                Text.translatable("text.xenyria.settings.paintsquad.shooting"),
                                config.enableDiscordRichPresence
                        )
                        .setDefaultValue(true)
                        .setTooltip(Text.translatable("text.xenyria.settings.paintsquad.shooting.tooltip"))
                        .setSaveConsumer((newState) -> config.improvedShootingDetectionForPaintSquad = newState)
                        .build()
        );
        return configBuilder;
    }

}
