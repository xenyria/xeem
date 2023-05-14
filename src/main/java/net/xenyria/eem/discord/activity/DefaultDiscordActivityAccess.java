package net.xenyria.eem.discord.activity;

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.activity.Activity;
import de.jcm.discordgamesdk.activity.ActivityType;
import net.xenyria.eem.discord.DiscordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;

public class DefaultDiscordActivityAccess implements IDiscordActivityAccess {

    public static Logger LOGGER = LoggerFactory.getLogger("Xenyria/DiscordActivity");
    public static final File TEMP_DIRECTORY = new File("xenyria" + File.separator + "temp");

    @Override
    public void initialize() throws IllegalStateException {
        LOGGER.info("Initializing Discord Game SDK for Rich Presence integration");

        // We're storing a copy of Discord's Game SDK in a temporary folder
        // This folder has to exist in order for initialization to succeed
        if(!TEMP_DIRECTORY.exists() && !TEMP_DIRECTORY.mkdirs()) {
            throw new IllegalStateException("Couldn't create temporary directory");
        }

        // Determine the name of the native lib we have to load based on the user's OS
        String sdkFileName = DiscordUtil.getNativeSDKLibraryFileName();

        File nativeLibraryFile = new File(TEMP_DIRECTORY, sdkFileName);
        if(!nativeLibraryFile.exists()) {
            try {
                LOGGER.info("Downloading Discord's Game SDK... (Version " + DiscordUtil.SDK_VERSION + ")");
                DiscordUtil.downloadGameSDK(nativeLibraryFile);
                if(!nativeLibraryFile.exists()) {
                    throw new FileNotFoundException("SDK has been downloaded but the required file couldn't be found.");
                }
            } catch (IOException exception) {
                LOGGER.error("Downloading the Discord Game SDK has failed: " + exception.getMessage());
            }
        }
        Core.init(nativeLibraryFile);
    }

    private Core coreInstance;
    private CreateParams params;

    @Override
    public synchronized void start(long applicationId) {
        // Destroy the existing core
        if(coreInstance != null) stop();

        // Initialize the core
        params = new CreateParams();
        try {
            params.setClientID(applicationId);
            params.setFlags(CreateParams.getDefaultFlags());
            coreInstance = new Core(params);
        } catch (Exception e) {
            try {
                if(params != null) {
                    params.close();
                    params = null;
                }
            } catch (Exception e1) {
                LOGGER.error("Couldn't close create params, this is really bad and probably an issue with the underlying library: "
                        + e1.getMessage());
            }
            try {
                if(coreInstance != null) {
                    coreInstance.close();
                    coreInstance = null;
                }
            } catch (Exception e1) {
                LOGGER.error("Couldn't close core instance after initialization failed"
                        + e1.getMessage());
            }
        }
    }

    @Override
    public synchronized void stop() {
        // Clean up
        if(coreInstance == null) return;
        try {
             coreInstance.close();
        } catch (Exception e) {
            LOGGER.error("Couldn't close core instance: " + e.getMessage());
        } finally {
            coreInstance = null;
        }

        try {
            params.close();
        } catch (Exception e) {
            LOGGER.error("Couldn't close create params: " + e.getMessage());
        } finally {
            params = null;
        }
    }

    @Override
    public synchronized void updateRichPresence(long applicationId,
                                                String details,
                                                String state,
                                                long activityStart,
                                                long activityEnd,
                                                String smallImageId,
                                                String smallImageText,
                                                String largeImageId,
                                                String largeImageText) {
        if(coreInstance == null) return;
        try(var activity = new Activity()) {
            activity.setDetails(details);
            activity.setState(state);
            activity.timestamps().setStart(Instant.ofEpochMilli(activityStart));
            activity.timestamps().setEnd(Instant.ofEpochMilli(activityEnd));
            activity.assets().setSmallImage(smallImageId);
            activity.assets().setSmallText(smallImageText);
            activity.assets().setLargeImage(largeImageId);
            activity.assets().setLargeText(largeImageText);
            activity.setType(ActivityType.PLAYING);
            coreInstance.activityManager().updateActivity(activity);
        } catch (Exception e) {
            LOGGER.error("Couldn't update activity: " + e.getMessage());
        }
    }

    @Override
    public synchronized void runCallbacks() {
        if(coreInstance == null) return;
        try {
            coreInstance.runCallbacks();
        } catch (Exception e) {
            LOGGER.error("Couldn't run callbacks: " + e.getMessage());
        }
    }
}
