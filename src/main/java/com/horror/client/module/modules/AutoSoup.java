package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;
import com.horror.client.module.setting.NumberSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

/** Eats stew from the hotbar when health gets low. */
public class AutoSoup extends Module {
    private final NumberSetting health = new NumberSetting("Health", 10, 2, 19, 1);

    private int previousSlot = -1;
    private boolean eating;

    public AutoSoup() {
        super("AutoSoup", "Uses stew when your health is low.", Category.PLAYER);
        addSettings(health);
    }

    @Override
    public void onTick() {
        ClientPlayerEntity player = mc.player;
        if (player == null || mc.interactionManager == null) {
            return;
        }

        if (!eating && player.getHealth() > health.get()) {
            return;
        }
        if (player.getHealth() >= player.getMaxHealth()) {
            stopEating();
            return;
        }

        int soupSlot = findSoup(player);
        if (soupSlot == -1) {
            stopEating();
            return;
        }

        if (!eating) {
            previousSlot = player.getInventory().selectedSlot;
            eating = true;
        }
        player.getInventory().selectedSlot = soupSlot;
        mc.interactionManager.interactItem(player, Hand.MAIN_HAND);
        mc.options.useKey.setPressed(true);
    }

    @Override
    public void onDisable() {
        stopEating();
    }

    private void stopEating() {
        if (!eating) {
            return;
        }
        if (previousSlot >= 0 && mc.player != null) {
            mc.player.getInventory().selectedSlot = previousSlot;
        }
        if (mc.options != null) {
            mc.options.useKey.setPressed(false);
        }
        previousSlot = -1;
        eating = false;
    }

    private int findSoup(ClientPlayerEntity player) {
        for (int i = 0; i < 9; i++) {
            Item item = player.getInventory().getStack(i).getItem();
            if (item == Items.MUSHROOM_STEW || item == Items.SUSPICIOUS_STEW || item == Items.RABBIT_STEW) {
                return i;
            }
        }
        return -1;
    }
}
