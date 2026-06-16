package com.horror.client.mixin;

import com.horror.client.module.ModuleManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Render overlay toggles inspired by Wurst, implemented as small local gates. */
@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(method = "renderVignetteOverlay", at = @At("HEAD"), cancellable = true)
    private void horror$noVignette(DrawContext context, Entity entity, CallbackInfo ci) {
        if (ModuleManager.isNoVignetteOn()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderPortalOverlay", at = @At("HEAD"), cancellable = true)
    private void horror$noPortalOverlay(DrawContext context, float nauseaStrength, CallbackInfo ci) {
        if (ModuleManager.isNoPortalOverlayOn()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderOverlay", at = @At("HEAD"), cancellable = true)
    private void horror$noPumpkin(DrawContext context, Identifier texture, float opacity, CallbackInfo ci) {
        if (ModuleManager.isNoPumpkinOn() && texture.getPath().contains("pumpkinblur")) {
            ci.cancel();
        }
    }
}
