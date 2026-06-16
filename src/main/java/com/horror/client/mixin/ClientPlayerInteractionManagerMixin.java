package com.horror.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.horror.client.module.ModuleManager;
import com.horror.client.module.modules.Reach;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Reach (extends interaction distance) and Criticals (packet hop before attacks). */
@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    @ModifyReturnValue(method = "getReachDistance", at = @At("RETURN"))
    private float horror$reach(float original) {
        if (ModuleManager.isReachOn()) {
            Reach reach = ModuleManager.get(Reach.class);
            return Math.max(original, (float) reach.getRange());
        }
        return original;
    }

    @Inject(method = "attackEntity", at = @At("HEAD"))
    private void horror$criticals(PlayerEntity attacker, Entity target, CallbackInfo ci) {
        if (!ModuleManager.isCriticalsOn()) {
            return;
        }
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null || attacker != player) {
            return;
        }
        if (!player.isOnGround() || player.isTouchingWater() || player.isInLava()
                || player.hasVehicle() || player.isClimbing()) {
            return;
        }

        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();
        // Tiny hop sent as movement packets so the server registers a critical hit.
        player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y + 0.0625, z, false));
        player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, false));
        player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y + 1.0E-4, z, false));
        player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, false));
    }
}
