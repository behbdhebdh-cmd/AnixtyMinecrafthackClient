package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;
import com.horror.client.module.setting.NumberSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

/** Increases the height of normal jumps. */
public class HighJump extends Module {
    private final NumberSetting height = new NumberSetting("Height", 0.8, 0.42, 2.5, 0.05);

    public HighJump() {
        super("HighJump", "Makes normal jumps higher.", Category.MOVEMENT);
        addSettings(height);
    }

    @Override
    public void onTick() {
        ClientPlayerEntity player = mc.player;
        if (player == null || !mc.options.jumpKey.isPressed() || !player.isOnGround()) {
            return;
        }
        Vec3d velocity = player.getVelocity();
        player.setVelocity(velocity.x, height.get(), velocity.z);
    }
}
