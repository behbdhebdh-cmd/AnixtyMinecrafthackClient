package com.horror.client.mixin;

import com.horror.client.module.ModuleManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** NoFireOverlay: cancels only the fire overlay, not actual fire behavior. */
@Mixin(InGameOverlayRenderer.class)
public class InGameOverlayRendererMixin {
    @Inject(method = "renderFireOverlay", at = @At("HEAD"), cancellable = true)
    private static void horror$noFireOverlay(MinecraftClient client, MatrixStack matrices, CallbackInfo ci) {
        if (ModuleManager.isNoFireOverlayOn()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderUnderwaterOverlay", at = @At("HEAD"), cancellable = true)
    private static void horror$noWaterOverlay(MinecraftClient client, MatrixStack matrices, CallbackInfo ci) {
        if (ModuleManager.isNoWaterOverlayOn()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderInWallOverlay", at = @At("HEAD"), cancellable = true)
    private static void horror$noWallOverlay(Sprite sprite, MatrixStack matrices, CallbackInfo ci) {
        if (ModuleManager.isNoWallOverlayOn()) {
            ci.cancel();
        }
    }
}
