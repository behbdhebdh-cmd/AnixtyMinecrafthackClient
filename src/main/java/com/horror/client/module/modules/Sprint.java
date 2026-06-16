package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;
import net.minecraft.client.network.ClientPlayerEntity;

/**
 * Keeps the player sprinting whenever they move forward.
 */
public class Sprint extends Module {
    public Sprint() {
        super("Sprint", "Always sprint while moving.", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        ClientPlayerEntity player = mc.player;
        if (player == null) {
            return;
        }
        if (player.input.movementForward > 0 && !player.isSneaking() && !player.horizontalCollision) {
            player.setSprinting(true);
        }
    }
}
