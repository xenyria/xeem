package net.xenyria.eem.networking;

import org.json.JSONException;
import org.json.JSONObject;

public class XenyriaServerPacket {

    /** Packets are separated into different types/categories **/
    public enum EPacketType {
        RP, // Rich-Presence (Discord Integration)
        PS_SHOOTING_STATE, // PaintSquad
        HANDSHAKE_INIT, // Server Switch / Server Info Change (Sent by the server)
        HANDSHAKE_RESPONSE, // Sent by the client, informs the server about the mod being active
        DEBUG, // Debug Operation (printing client-side variables into the chat)
        SETTINGS_CHANGED // Sent by the client when settings are changed
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

        JSONObject jsonData;
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
        EPacketType parsedPacketType;
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


    /** Attempt to send this packet to the currently connected server as a plugin message **/
    public void sendToServer() {
        var networkHandler = MinecraftClient.getInstance().getNetworkHandler();
        if(networkHandler == null)
            return; // Not connected

        JSONObject assembledPacket = new JSONObject();
        assembledPacket.put("type", this.packetType.name());
        assembledPacket.put("data", this.data);

        ByteArrayOutputStream rawPacketBytes = new ByteArrayOutputStream();
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(rawPacketBytes);
            byte[] jsonContent = assembledPacket.toString().getBytes(StandardCharsets.UTF_8);
            // 4 bytes are used to store the length of the JSON object that follows
            dataOutputStream.writeInt(jsonContent.length);
            dataOutputStream.write(jsonContent, 0, jsonContent.length);
        } catch (IOException e) {
            LOGGER.severe("Couldn't convert packet into bytes: " + e.getMessage());
        }

        // Send data to the server
        CustomPayloadC2SPacket packet = new CustomPayloadC2SPacket(
                PacketListener.ID, new PacketByteBuf(
                        Unpooled.wrappedBuffer(rawPacketBytes.toByteArray()))
        );
        networkHandler.sendPacket(packet);
    }

}
