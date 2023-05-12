package net.xenyria.eem.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.xenyria.eem.PlayingSessionInformation;
import net.xenyria.eem.config.screen.XenyriaConfigManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class PaintSquadSwimFormMixin {

    private static final String SWIM_FORM_DETECTION_NAME = "\u0001swim_form";

    @Inject(at = @At("HEAD"), method = "tick()V")
    private void tick(CallbackInfo info) {
        // Check if this setting is enabled in the first place...
        if(!PlayingSessionInformation.isOnNetwork()
        || !XenyriaConfigManager.getConfig().swimFormCameraForPaintSquad) {
            return;
        }
        // A player entity has to be present for this to work
        var playerEntity = MinecraftClient.getInstance().player;
        if(playerEntity == null) { return; }
        // Grab the player's client options since those include the current camera perspective
        var clientOptions = MinecraftClient.getInstance().options;
        // Check if the player is currently in PaintSquad's swim form
        boolean isSwimming = isInSwimForm();
        if(wasSwimmingLastTick && !isSwimming) {
            // Player was swimming last tick and now isn't swimming anymore
            // We'll just switch back to first person
            clientOptions.setPerspective(Perspective.FIRST_PERSON);
        } else if(!wasSwimmingLastTick && isSwimming) {
            // The player wasn't swimming in the last tick and now entered the swim form
            // In this case we'll just switch into the
            clientOptions.setPerspective(Perspective.THIRD_PERSON_BACK);
        }
        // Store the last swimming state for the next check
        wasSwimmingLastTick = isSwimming;
    }

    private boolean wasSwimmingLastTick;
    private boolean isInSwimForm() {
        // A player entity has to be present for this to work
        var playerEntity = MinecraftClient.getInstance().player;
        if(playerEntity == null) { return false; }
        if(playerEntity.getVehicle() == null) { return false; }

        // Check the player's vehicle entity
        var vehicle = playerEntity.getVehicle();
        if(vehicle.getCustomName() == null) { return false; }

        // Check for a specific string to be present for the check to trigger
        String text = vehicle.getCustomName().getString();
        return text.contains(SWIM_FORM_DETECTION_NAME);
    }

}
