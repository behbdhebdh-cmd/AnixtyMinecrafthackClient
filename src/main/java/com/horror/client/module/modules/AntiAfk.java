package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;
import com.horror.client.module.setting.NumberSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Hand;

/** Performs a small jump/swing every so often to keep the player active. */
public class AntiAfk extends Module {
    private final NumberSetting interval = new NumberSetting("Seconds", 30, 5, 300, 5);
    private int ticks;

    public AntiAfk() {
        super("AntiAfk", "Occasionally jumps and swings.", Category.MOVEMENT);
        addSettings(interval);
    }

    @Override
    public void onTick() {
        ClientPlayerEntity player = mc.player;
        if (player == null) {
            return;
        }
        ticks++;
        if (ticks < interval.getInt() * 20) {
            return;
        }
        ticks = 0;
        if (player.isOnGround()) {
            player.jump();
        }
        player.swingHand(Hand.MAIN_HAND);
    }

    @Override
    public void onDisable() {
        ticks = 0;
    }
}
