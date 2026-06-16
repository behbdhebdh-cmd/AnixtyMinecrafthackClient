package com.horror.client.mixin;

import com.horror.client.module.ModuleManager;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/** NoSlowdown: cancels the 0.2x movement multiplier applied while using items. */
@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @ModifyConstant(method = "tickMovement", constant = @Constant(floatValue = 0.2f))
    private float horror$noSlowdown(float original) {
        return ModuleManager.isNoSlowdownOn() ? 1.0f : original;
    }
}
