package com.horror.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.horror.client.module.ModuleManager;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * NoFall: makes outgoing movement packets always report the player as standing
 * on the ground, so the (integrated) server never registers a fall.
 *
 * <p>Modifying the {@code isOnGround()} getter is far more robust than patching
 * the packet constructor's arguments.
 */
@Mixin(PlayerMoveC2SPacket.class)
public class PlayerMoveC2SPacketMixin {
    @ModifyReturnValue(method = "isOnGround", at = @At("RETURN"))
    private boolean horror$noFall(boolean original) {
        return original || ModuleManager.isNoFallOn();
    }
}
