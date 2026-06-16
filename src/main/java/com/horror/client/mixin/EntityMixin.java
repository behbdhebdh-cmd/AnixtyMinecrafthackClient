package com.horror.client.mixin;

import com.horror.client.module.ModuleManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Cancels selected local movement slowdowns. */
@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "slowMovement", at = @At("HEAD"), cancellable = true)
    private void horror$noWeb(BlockState state, Vec3d multiplier, CallbackInfo ci) {
        if (state.isOf(Blocks.COBWEB) && ModuleManager.isNoWebOn()) {
            ci.cancel();
            return;
        }
        if (state.isOf(Blocks.POWDER_SNOW) && ModuleManager.isSnowShoeOn()) {
            ci.cancel();
        }
    }
}
