package de.xenyria.eem.paintsquad;

import de.xenyria.eem.networking.PacketListener;
import de.xenyria.eem.networking.XenyriaServerPacket;
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
                boolean hasChanged = false;
                boolean isPressingRightMouseButton =
                        MinecraftClient.getInstance().options.useKey.isPressed();

                if(isShooting != isPressingRightMouseButton) {
                    hasChanged = true;
                    isShooting = isPressingRightMouseButton;
                }

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
