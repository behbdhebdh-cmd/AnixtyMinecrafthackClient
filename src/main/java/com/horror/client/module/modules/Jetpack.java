package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;
import com.horror.client.module.setting.NumberSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

/** Adds upward thrust while holding jump. */
public class Jetpack extends Module {
    private final NumberSetting thrust = new NumberSetting("Thrust", 0.35, 0.05, 1.5, 0.05);

    public Jetpack() {
        super("Jetpack", "Hold jump to fly upward.", Category.MOVEMENT);
        addSettings(thrust);
    }

    @Override
    public void onTick() {
        ClientPlayerEntity player = mc.player;
        if (player == null || !mc.options.jumpKey.isPressed()) {
            return;
        }
        Vec3d velocity = player.getVelocity();
        player.setVelocity(velocity.x, thrust.get(), velocity.z);
        player.fallDistance = 0;
    }
}
