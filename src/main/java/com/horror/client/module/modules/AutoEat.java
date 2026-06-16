package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;
import com.horror.client.module.setting.NumberSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Hand;

/**
 * Switches to food and eats it when the hunger level drops below a threshold,
 * restoring the previously held slot afterwards.
 */
public class AutoEat extends Module {
    private final NumberSetting threshold = new NumberSetting("Hunger", 6, 1, 19, 1);

    private int previousSlot = -1;
    private boolean eating;

    public AutoEat() {
        super("AutoEat", "Eats food when your hunger is low.", Category.PLAYER);
        addSettings(threshold);
    }

    @Override
    public void onTick() {
        ClientPlayerEntity player = mc.player;
        if (player == null || mc.interactionManager == null) {
            return;
        }

        int hunger = player.getHungerManager().getFoodLevel();
        if (!eating && hunger > threshold.getInt()) {
            return;
        }
        if (hunger >= 20) {
            stopEating();
            return;
        }

        int foodSlot = findFood(player);
        if (foodSlot == -1) {
            stopEating();
            return;
        }

        if (!eating) {
            previousSlot = player.getInventory().selectedSlot;
            eating = true;
        }
        player.getInventory().selectedSlot = foodSlot;
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
        if (previousSlot != -1 && mc.player != null) {
            mc.player.getInventory().selectedSlot = previousSlot;
        }
        if (mc.options != null) {
            mc.options.useKey.setPressed(false);
        }
        eating = false;
    }

    private int findFood(ClientPlayerEntity player) {
        for (int i = 0; i < 9; i++) {
            if (player.getInventory().getStack(i).isFood()) {
                return i;
            }
        }
        return -1;
    }
}
