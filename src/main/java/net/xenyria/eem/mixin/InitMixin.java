package net.xenyria.eem.mixin;

import net.xenyria.eem.networking.PacketListener;
import net.xenyria.eem.config.screen.XenyriaConfigManager;
import net.xenyria.eem.networking.PacketListener;
import net.xenyria.eem.paintsquad.PaintSquadInputManager;
import net.minecraft.client.gui.screen.TitleScreen;
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
        // Load the configuration file
        try {
            XenyriaConfigManager.loadConfig();
        } catch (Exception exception) {
            XenyriaConfigManager.LOGGER.error("Failed to load XEEM's configuration file on start-up: "
                    + exception.getMessage());
        }
        // Register improved shooting detection for weapons in PaintSquad
        PaintSquadInputManager.createInstance();
    }
}

