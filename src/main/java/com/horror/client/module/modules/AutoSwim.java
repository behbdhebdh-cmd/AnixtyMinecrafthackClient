package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

/** Automatically swims upward when your head is in water. */
public class AutoSwim extends Module {
    public AutoSwim() {
        super("AutoSwim", "Keeps you swimming upward in water.", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        ClientPlayerEntity player = mc.player;
        if (player == null || !player.isTouchingWater()) {
            return;
        }
        Vec3d velocity = player.getVelocity();
        if (velocity.y < 0.1) {
            player.setVelocity(velocity.x, 0.12, velocity.z);
        }
    }
}
