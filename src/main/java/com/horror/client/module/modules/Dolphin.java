package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;
import com.horror.client.module.setting.NumberSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

/** Gives extra forward speed while swimming. */
public class Dolphin extends Module {
    private final NumberSetting speed = new NumberSetting("Speed", 0.08, 0.02, 0.4, 0.02);

    public Dolphin() {
        super("Dolphin", "Swim faster in water.", Category.MOVEMENT);
        addSettings(speed);
    }

    @Override
    public void onTick() {
        ClientPlayerEntity player = mc.player;
        if (player == null || !player.isTouchingWater() || player.input.movementForward <= 0) {
            return;
        }
        double yaw = Math.toRadians(player.getYaw());
        Vec3d velocity = player.getVelocity();
        player.setVelocity(velocity.x - Math.sin(yaw) * speed.get(), velocity.y,
                velocity.z + Math.cos(yaw) * speed.get());
    }
}
