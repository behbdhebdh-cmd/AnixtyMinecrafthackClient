package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;
import com.horror.client.module.setting.NumberSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

/**
 * Multiplies horizontal velocity while moving on the ground.
 */
public class Speed extends Module {
    private final NumberSetting multiplier = new NumberSetting("Multiplier", 1.5, 1.1, 3.0, 0.1);

    public Speed() {
        super("Speed", "Increases your movement speed.", Category.MOVEMENT);
        addSettings(multiplier);
    }

    @Override
    public void onTick() {
        ClientPlayerEntity player = mc.player;
        if (player == null) {
            return;
        }
        boolean moving = player.input.movementForward != 0 || player.input.movementSideways != 0;
        if (moving && !player.isSneaking()) {
            Vec3d velocity = player.getVelocity();
            player.setVelocity(velocity.x * multiplier.get(), velocity.y, velocity.z * multiplier.get());
        }
    }
}
