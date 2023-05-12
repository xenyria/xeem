package net.xenyria.eem.networking;

import net.xenyria.eem.discord.DiscordRichPresenceIntegration;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;
import net.xenyria.eem.EXenyriaServerType;
import net.xenyria.eem.PlayingSessionInformation;
import net.xenyria.eem.config.screen.XenyriaConfigManager;
import net.xenyria.eem.discord.DiscordRichPresenceIntegration;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class PacketListener {

    public static Identifier ID;
    public static Logger LOGGER = Logger.getLogger("Xenyria/PacketListener");

    public static void initialize() {
        ID = Identifier.of("xenyria", "mod_communication");
        if(ID == null) throw new IllegalStateException("Identifier could not be initialized.");

        LOGGER.info("Registering packet receiver for plugin messages...");
        ClientPlayNetworking.registerGlobalReceiver(
                ID, (client, handler, buf, responseSender) -> {
                    /*
                     * Packets sent by the server consist of a 4-byte Integer
                     * and a byte array that represents UTF-8 string data.
                     */
                    int bytesToRead = buf.readInt();
                    // Allocate enough memory for reading the raw JSON string in the next step
                    byte[] buffer = new byte[bytesToRead];
                    buf.readBytes(buffer);
                    // Create a UTF-8 string from the received data
                    String rawJsonText = new String(buffer, StandardCharsets.UTF_8);

                    // Try to parse the packet
                    XenyriaServerPacket packet = XenyriaServerPacket.parsePacket(rawJsonText);
                    if(packet == null) {
                        LOGGER.warning("Unable to parse Xenyria packet with " + bytesToRead + " length.");
                        return;
                    }

                    // Do something with the received data
                    if (packet.getPacketType() == XenyriaServerPacket.EPacketType.RP) {
                        // If rich presence is disabled in the settings we ignore this packet
                        if (!XenyriaConfigManager.getConfig().enableDiscordRichPresence) {
                            return;
                        }
                        DiscordRichPresenceIntegration.setLastReceivedRichPresence(packet.getData());
                    } else if (packet.getPacketType() == XenyriaServerPacket.EPacketType.HANDSHAKE_INIT) {
                        // Orion sends one mod handshake packet on login
                        // This packet contains the current server ID
                        String instanceId = packet.getData().getString("server_id");
                        PlayingSessionInformation.setServerInstanceId(instanceId);
                        PlayingSessionInformation.setOnNetwork(true);
                        PlayingSessionInformation.setCurrentServerType(
                                EXenyriaServerType.determineServerType(instanceId)
                        );

                        // We respond back so that the server knows we're using XEEM
                        XenyriaServerPacket responsePacket = new XenyriaServerPacket(
                                XenyriaServerPacket.EPacketType.HANDSHAKE_RESPONSE,
                                new JSONObject()
                        );
                        responsePacket.sendToServer();
                        LOGGER.info("Successfully completed handshake with Orion");
                    } else if (packet.getPacketType() == XenyriaServerPacket.EPacketType.DEBUG) {
                        LOGGER.info("Current network state: " + PlayingSessionInformation.isOnNetwork());
                        LOGGER.info("Current server type: " + PlayingSessionInformation.getCurrentServerType());
                        LOGGER.info("Current server id: " + PlayingSessionInformation.getServerInstanceId());
                    }
                }
        );
    }

}
