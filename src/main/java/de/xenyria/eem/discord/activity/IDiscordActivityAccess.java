package de.xenyria.eem.discord.activity;

public interface IDiscordActivityAccess {

    void initialize() throws IllegalStateException;
    void start(long applicationId);
    void stop();
    void updateRichPresence(
            long applicationId,
            String details,
            String state,
            long activityStart,
            long activityEnd,
            String smallImageId,
            String smallImageText,
            String largeImageId,
            String largeImageText
    );
    void runCallbacks();


}
