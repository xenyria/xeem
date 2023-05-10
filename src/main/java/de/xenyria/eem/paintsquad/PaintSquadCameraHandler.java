package de.xenyria.eem.paintsquad;

import de.xenyria.eem.networking.PacketListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PaintSquadCameraHandler {

    public static final String SWIM_FORM_DETECTION_NAME = "\u0001swim_form";

    private static PaintSquadCameraHandler instance;
    public static PaintSquadCameraHandler getInstance() { return instance; }
    public static void createInstance() {
        if(instance != null) return;
        instance = new PaintSquadCameraHandler();
    }

    private PaintSquadCameraHandler() {
        startTask();
    }

    private boolean wasSwimmingLastTick;
    private boolean isInSwimForm() {
        var playerEntity = MinecraftClient.getInstance().player;
        if(playerEntity == null) { return false; }
        if(playerEntity.getVehicle() == null) { return false; }
        var vehicle = playerEntity.getVehicle();
        if(vehicle.getCustomName() == null) { return false; }
        String text = vehicle.getCustomName().getString();
        return text.contains(SWIM_FORM_DETECTION_NAME);
    }

    private void startTask() {
        PacketListener.LOGGER.info("Starting PaintSquad camera update task...");
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                MinecraftClient.getInstance().send(() -> {
                    var playerEntity = MinecraftClient.getInstance().player;
                    if(playerEntity == null) { return; }

                    var clientOptions = MinecraftClient.getInstance().options;
                    boolean isSwimming = isInSwimForm();
                    if(wasSwimmingLastTick) {
                        if(!isSwimming) {
                            // Player was swimming last tick and now isn't swimming anymore
                            // We'll just switch back to first person
                            clientOptions.setPerspective(Perspective.FIRST_PERSON);
                        }
                    } else {
                        if(isSwimming) {
                            // The player wasn't swimming in the last tick and now entered the swim form
                            // In this case we'll just switch into the
                            clientOptions.setPerspective(Perspective.THIRD_PERSON_BACK);
                        }
                    }
                    wasSwimmingLastTick = isSwimming;
                });
            }
        }, 50, 50, TimeUnit.MILLISECONDS);
    }

}
