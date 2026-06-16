package com.horror.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.horror.client.module.ModuleManager;
import com.horror.client.module.modules.FastBreak;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** FastBreak: multiplies the local player's block breaking speed. */
@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @ModifyReturnValue(method = "getBlockBreakingSpeed", at = @At("RETURN"))
    private float horror$fastBreak(float original) {
        if (ModuleManager.isFastBreakOn() && (Object) this == MinecraftClient.getInstance().player) {
            FastBreak fastBreak = ModuleManager.get(FastBreak.class);
            return original * fastBreak.getMultiplier();
        }
        return original;
    }
}
