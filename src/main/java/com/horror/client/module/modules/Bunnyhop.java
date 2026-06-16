package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;
import net.minecraft.client.network.ClientPlayerEntity;

/**
 * Wurst-style Bunnyhop: automatically jumps whenever you move along the ground.
 */
public class Bunnyhop extends Module {
    public Bunnyhop() {
        super("Bunnyhop", "Automatically jumps while you move.", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        ClientPlayerEntity player = mc.player;
        if (player == null) {
            return;
        }
        boolean moving = player.input.movementForward != 0 || player.input.movementSideways != 0;
        if (player.isOnGround() && moving && !player.isSneaking()) {
            player.setJumping(true);
        }
    }
}
