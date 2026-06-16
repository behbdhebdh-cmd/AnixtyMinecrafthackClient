package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;
import com.horror.client.module.setting.NumberSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/** Stops horizontal movement before walking off block edges. */
public class SafeWalk extends Module {
    private final NumberSetting lookAhead = new NumberSetting("LookAhead", 0.42, 0.2, 1.0, 0.02);

    public SafeWalk() {
        super("SafeWalk", "Stops at block edges while walking.", Category.MOVEMENT);
        addSettings(lookAhead);
    }

    @Override
    public void onTick() {
        ClientPlayerEntity player = mc.player;
        if (player == null || mc.world == null || !player.isOnGround() || player.isSneaking()) {
            return;
        }

        double forward = player.input.movementForward;
        double sideways = player.input.movementSideways;
        if (forward == 0 && sideways == 0) {
            return;
        }

        double yaw = Math.toRadians(player.getYaw());
        double sin = Math.sin(yaw);
        double cos = Math.cos(yaw);
        double nextX = player.getX() + (forward * -sin + sideways * cos) * lookAhead.get();
        double nextZ = player.getZ() + (forward * cos + sideways * sin) * lookAhead.get();
        BlockPos belowNext = BlockPos.ofFloored(nextX, player.getY() - 0.55, nextZ);

        if (mc.world.getBlockState(belowNext).isAir()) {
            Vec3d velocity = player.getVelocity();
            player.setVelocity(0, velocity.y, 0);
        }
    }
}
