package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;
import com.horror.client.module.setting.NumberSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

/** Lets you climb walls when walking into them. */
public class Spider extends Module {
    private final NumberSetting speed = new NumberSetting("Speed", 0.25, 0.1, 1.0, 0.05);

    public Spider() {
        super("Spider", "Climb walls by walking into them.", Category.MOVEMENT);
        addSettings(speed);
    }

    @Override
    public void onTick() {
        ClientPlayerEntity player = mc.player;
        if (player == null || !player.horizontalCollision || player.isClimbing()) {
            return;
        }
        Vec3d velocity = player.getVelocity();
        player.setVelocity(velocity.x, speed.get(), velocity.z);
        player.fallDistance = 0;
    }
}
