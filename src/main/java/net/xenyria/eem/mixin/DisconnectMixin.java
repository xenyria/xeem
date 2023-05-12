package net.xenyria.eem.mixin;

import net.minecraft.client.MinecraftClient;
import net.xenyria.eem.EXenyriaServerType;
import net.xenyria.eem.PlayingSessionInformation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class DisconnectMixin {

    @Inject(at = @At("HEAD"), method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V")
    public void handleDisconnect(CallbackInfo info) {
        PlayingSessionInformation.setOnNetwork(false);
        PlayingSessionInformation.setCurrentServerType(EXenyriaServerType.UNKNOWN);
        PlayingSessionInformation.setServerInstanceId("");
    }
}
