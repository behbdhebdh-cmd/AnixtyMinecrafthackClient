package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;
import com.horror.client.module.setting.NumberSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

/** Climbs ladders and vines faster. */
public class FastLadder extends Module {
    private final NumberSetting speed = new NumberSetting("Speed", 0.35, 0.2, 1.0, 0.05);

    public FastLadder() {
        super("FastLadder", "Climb ladders faster.", Category.MOVEMENT);
        addSettings(speed);
    }

    @Override
    public void onTick() {
        ClientPlayerEntity player = mc.player;
        if (player == null || !player.isClimbing()) {
            return;
        }
        if (mc.options.forwardKey.isPressed() || mc.options.jumpKey.isPressed()) {
            Vec3d velocity = player.getVelocity();
            player.setVelocity(velocity.x, speed.get(), velocity.z);
        }
    }
}
