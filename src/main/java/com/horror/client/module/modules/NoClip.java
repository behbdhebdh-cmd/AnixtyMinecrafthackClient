package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;
import com.horror.client.module.setting.NumberSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

/** Lets the local player move freely through blocks in singleplayer. */
public class NoClip extends Module {
    private final NumberSetting speed = new NumberSetting("Speed", 0.6, 0.1, 2.0, 0.1);

    public NoClip() {
        super("NoClip", "Move through blocks in singleplayer.", Category.MOVEMENT);
        addSettings(speed);
    }

    @Override
    public void onTick() {
        ClientPlayerEntity player = mc.player;
        if (player == null) {
            return;
        }

        player.noClip = true;
        player.fallDistance = 0;

        double vertical = 0;
        if (mc.options.jumpKey.isPressed()) {
            vertical += speed.get();
        }
        if (mc.options.sneakKey.isPressed()) {
            vertical -= speed.get();
        }

        double forward = player.input.movementForward;
        double sideways = player.input.movementSideways;
        double vx = 0;
        double vz = 0;
        if (forward != 0 || sideways != 0) {
            double rad = Math.toRadians(player.getYaw());
            double sin = Math.sin(rad);
            double cos = Math.cos(rad);
            vx = (forward * -sin + sideways * cos) * speed.get();
            vz = (forward * cos + sideways * sin) * speed.get();
        }
        player.setVelocity(new Vec3d(vx, vertical, vz));
    }

    @Override
    public void onDisable() {
        if (mc.player != null) {
            mc.player.noClip = false;
        }
    }
}
