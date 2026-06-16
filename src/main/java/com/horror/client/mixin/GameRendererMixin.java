package com.horror.client.mixin;

import com.horror.client.module.ModuleManager;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** NoHurtcam: cancels the damage camera tilt transform. */
@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "tiltViewWhenHurt", at = @At("HEAD"), cancellable = true)
    private void horror$noHurtcam(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if (ModuleManager.isNoHurtcamOn()) {
            ci.cancel();
        }
    }
}
