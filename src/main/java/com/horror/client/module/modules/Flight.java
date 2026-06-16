package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;
import com.horror.client.module.setting.NumberSetting;
import net.minecraft.client.network.ClientPlayerEntity;

/**
 * Velocity-based creative-style flight controlled with the movement keys,
 * jump (up) and sneak (down).
 */
public class Flight extends Module {
    private final NumberSetting speed = new NumberSetting("Speed", 1.0, 0.1, 5.0, 0.1);

    public Flight() {
        super("Flight", "Lets you fly freely through the world.", Category.MOVEMENT);
        addSettings(speed);
    }

    @Override
    public void onDisable() {
        if (mc.player != null) {
            mc.player.setVelocity(0, 0, 0);
        }
    }

    @Override
    public void onTick() {
        ClientPlayerEntity player = mc.player;
        if (player == null) {
            return;
        }

        double s = speed.get();
        double vy = 0;
        if (player.input.jumping) {
            vy += s;
        }
        if (player.input.sneaking) {
            vy -= s;
        }

        double forward = player.input.movementForward;
        double sideways = player.input.movementSideways;
        double vx = 0;
        double vz = 0;
        if (forward != 0 || sideways != 0) {
            double rad = Math.toRadians(player.getYaw());
            double sin = Math.sin(rad);
            double cos = Math.cos(rad);
            vx = (forward * -sin + sideways * cos) * s;
            vz = (forward * cos + sideways * sin) * s;
        }

        player.setVelocity(vx, vy, vz);
        player.fallDistance = 0;
    }
}
