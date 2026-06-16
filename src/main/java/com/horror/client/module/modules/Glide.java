package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;
import com.horror.client.module.setting.NumberSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

/** Slows down falling while still allowing normal movement. */
public class Glide extends Module {
    private final NumberSetting fallSpeed = new NumberSetting("FallSpeed", 0.08, 0.01, 0.3, 0.01);

    public Glide() {
        super("Glide", "Fall slowly instead of dropping fast.", Category.MOVEMENT);
        addSettings(fallSpeed);
    }

    @Override
    public void onTick() {
        ClientPlayerEntity player = mc.player;
        if (player == null || player.isOnGround() || player.getVelocity().y >= 0 || player.isFallFlying()) {
            return;
        }
        Vec3d velocity = player.getVelocity();
        player.setVelocity(velocity.x, -fallSpeed.get(), velocity.z);
        player.fallDistance = 0;
    }
}
