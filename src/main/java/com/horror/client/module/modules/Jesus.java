package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

/** Keeps you floating near the water surface. */
public class Jesus extends Module {
    public Jesus() {
        super("Jesus", "Float on water surfaces.", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        ClientPlayerEntity player = mc.player;
        if (player == null || !player.isTouchingWater() || player.isSneaking()) {
            return;
        }
        Vec3d velocity = player.getVelocity();
        if (velocity.y < 0.06) {
            player.setVelocity(velocity.x, 0.06, velocity.z);
        }
        player.fallDistance = 0;
    }
}
