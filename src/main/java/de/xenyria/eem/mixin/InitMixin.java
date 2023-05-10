package de.xenyria.eem.mixin;

import de.xenyria.eem.networking.PacketListener;
import de.xenyria.eem.paintsquad.PaintSquadCameraHandler;
import de.xenyria.eem.paintsquad.PaintSquadInputManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class InitMixin {

    @Inject(at = @At("HEAD"), method = "init()V")
    private void init(CallbackInfo info) {
        // Register the packet listener here for client-server communication
        PacketListener.initialize();
        PaintSquadInputManager.createInstance();
        PaintSquadCameraHandler.createInstance();
    }
}

