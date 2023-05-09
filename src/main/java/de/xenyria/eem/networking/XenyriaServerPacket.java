package de.xenyria.eem.networking;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

public class XenyriaServerPacket {

    /** Packets are separated into different types/categories **/
    public enum EPacketType {
        RP // Rich-Presence (Discord Integration)
    }

    private final EPacketType packetType;
    public EPacketType getPacketType() { return packetType; }

    private final JSONObject data;
    public JSONObject getData() { return data; }

    public XenyriaServerPacket(EPacketType parsedPacketType, JSONObject data) {
        this.packetType = parsedPacketType;
        this.data = data;
    }

    /**
     * Parses raw JSON into packet objects
     */
    public static XenyriaServerPacket parsePacket(String rawJson) {

        JSONObject jsonData = null;
        try {
            jsonData = new JSONObject(rawJson);
        } catch (JSONException exception) {
            PacketListener.LOGGER.severe("Unable to parse packet from raw JSON: " + rawJson);
            return null;
        }

        if(!jsonData.has("type")) {
            PacketListener.LOGGER.severe("Malformed packet, missing type field.");
            return null;
        }
        EPacketType parsedPacketType = null;
        try {
            parsedPacketType = EPacketType.valueOf(jsonData.getString("type").toUpperCase());
        } catch (IllegalArgumentException exception) {
            PacketListener.LOGGER.severe("Malformed packet, unknown type value: " + jsonData.getString("type"));
            return null;
        }

        if(!jsonData.has("data")) {
            PacketListener.LOGGER.severe("Malformed packet, missing data field.");
            return null;
        }

        return new XenyriaServerPacket(parsedPacketType, jsonData.getJSONObject("data"));

    }

}
