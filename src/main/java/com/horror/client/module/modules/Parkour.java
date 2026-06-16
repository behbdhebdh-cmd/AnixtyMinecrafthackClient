package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;

/** Jumps automatically when you walk off a block edge. */
public class Parkour extends Module {
    public Parkour() {
        super("Parkour", "Automatically jumps at block edges.", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        ClientPlayerEntity player = mc.player;
        if (player == null || mc.world == null || !player.isOnGround()
                || player.input.movementForward <= 0 || player.isSneaking()) {
            return;
        }

        double yaw = Math.toRadians(player.getYaw());
        double nextX = player.getX() - Math.sin(yaw) * 0.65;
        double nextZ = player.getZ() + Math.cos(yaw) * 0.65;
        BlockPos belowNext = BlockPos.ofFloored(nextX, player.getY() - 0.5, nextZ);
        BlockState state = mc.world.getBlockState(belowNext);
        if (state.isAir()) {
            player.jump();
        }
    }
}
