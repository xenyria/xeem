package net.xenyria.eem.config.screen;

import org.json.JSONObject;

public class XenyriaConfig {

    public boolean enableDiscordRichPresence = true;
    public boolean shareServerActivity = true;
    public boolean improvedShootingDetectionForPaintSquad = true;
    public boolean swimFormCameraForPaintSquad = true;

    public static XenyriaConfig load(JSONObject object) {
        XenyriaConfig config = new XenyriaConfig();
        if(object.has("discord")) {
            JSONObject settingsObject = object.getJSONObject("discord");
            config.enableDiscordRichPresence = settingsObject.getBoolean("rich_presence");
            config.shareServerActivity = settingsObject.getBoolean("share_server_activity");
        }
        if(object.has("paintsquad")) {
            JSONObject settingsObject = object.getJSONObject("paintsquad");
            config.swimFormCameraForPaintSquad = settingsObject.getBoolean("swim_camera");
            config.improvedShootingDetectionForPaintSquad = settingsObject.getBoolean("improved_shooting");
        }
        return config;
    }

    public JSONObject toJSON() {
        JSONObject configObject = new JSONObject();

        JSONObject discordObject = new JSONObject();
        discordObject.put("rich_presence", enableDiscordRichPresence);
        discordObject.put("share_server_activity", shareServerActivity);
        configObject.put("discord", discordObject);

        JSONObject paintSquadObject = new JSONObject();
        paintSquadObject.put("improved_shooting", improvedShootingDetectionForPaintSquad);
        paintSquadObject.put("swim_camera", swimFormCameraForPaintSquad);
        configObject.put("paintsquad", paintSquadObject);

        return configObject;
    }

}
