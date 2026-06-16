package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.network.ClientPlayerEntity;

/** Respawns immediately after death and closes the death screen. */
public class AutoRespawn extends Module {
    public AutoRespawn() {
        super("AutoRespawn", "Automatically respawns after death.", Category.PLAYER);
    }

    @Override
    public void onTick() {
        ClientPlayerEntity player = mc.player;
        if (player == null || player.isAlive()) {
            return;
        }
        player.requestRespawn();
        if (mc.currentScreen instanceof DeathScreen) {
            mc.setScreen(null);
        }
    }
}
