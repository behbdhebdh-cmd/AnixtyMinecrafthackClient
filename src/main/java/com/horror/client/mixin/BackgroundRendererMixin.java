package com.horror.client.mixin;

import com.horror.client.module.ModuleManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** NoFog: after vanilla calculates fog, push the end distance far out. */
@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {
    @Inject(method = "applyFog", at = @At("TAIL"))
    private static void horror$noFog(Camera camera, BackgroundRenderer.FogType fogType,
                                     float viewDistance, boolean thickFog, float tickDelta,
                                     CallbackInfo ci) {
        if (ModuleManager.isNoFogOn()) {
            RenderSystem.setShaderFogStart(viewDistance * 16.0f);
            RenderSystem.setShaderFogEnd(viewDistance * 32.0f);
        }
    }
}
