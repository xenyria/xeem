package net.xenyria.eem.paintsquad;

import net.xenyria.eem.config.screen.XenyriaConfigManager;
import net.xenyria.eem.networking.PacketListener;
import net.xenyria.eem.networking.XenyriaServerPacket;
import net.minecraft.client.MinecraftClient;
import org.json.JSONObject;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PaintSquadInputManager {

    private static PaintSquadInputManager instance;
    public static PaintSquadInputManager getInstance() { return instance; }
    public static void createInstance() {
        if(instance != null) return;
        instance = new PaintSquadInputManager();
    }

    private PaintSquadInputManager() {
        startTask();
    }

    private void startTask() {
        PacketListener.LOGGER.info("Starting PaintSquad input polling task...");
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(new Runnable() {
            private boolean isShooting;
            @Override
            public void run() {
                // Check if this feature is enabled...
                if(!XenyriaConfigManager.getConfig().improvedShootingDetectionForPaintSquad) {
                    return;
                }
                // Check if the player is holding the right mouse button
                boolean hasChanged = false;
                boolean isPressingRightMouseButton =
                        MinecraftClient.getInstance().options.useKey.isPressed();

                // We only send state changes to the server (e.g. when the state switches from firing to not firing)
                // So in this case we just check if the state has changed compared to the last check
                if(isShooting != isPressingRightMouseButton) {
                    hasChanged = true;
                    isShooting = isPressingRightMouseButton;
                }

                // If a change has been detected we send a mod packet to the server
                if(hasChanged) {
                    JSONObject payload = new JSONObject();
                    payload.put("shooting", isShooting);
                    XenyriaServerPacket packet = new XenyriaServerPacket(
                            XenyriaServerPacket.EPacketType.PS_SHOOTING_STATE, payload);
                    packet.sendToServer();
                }
            }
        }, 10, 10, TimeUnit.MILLISECONDS);
    }

}
