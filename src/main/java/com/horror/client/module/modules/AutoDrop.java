package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;
import com.horror.client.module.setting.BooleanSetting;
import com.horror.client.module.setting.NumberSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

/** Drops selected junk items from the hotbar. */
public class AutoDrop extends Module {
    private final BooleanSetting dirt = new BooleanSetting("Dirt", true);
    private final BooleanSetting cobble = new BooleanSetting("Cobble", false);
    private final BooleanSetting rottenFlesh = new BooleanSetting("RottenFlesh", true);
    private final NumberSetting delay = new NumberSetting("Delay", 8, 2, 40, 1);

    private int cooldown;

    public AutoDrop() {
        super("AutoDrop", "Drops configured junk from your hotbar.", Category.PLAYER);
        addSettings(dirt, cobble, rottenFlesh, delay);
    }

    @Override
    public void onTick() {
        ClientPlayerEntity player = mc.player;
        if (player == null) {
            return;
        }
        if (cooldown > 0) {
            cooldown--;
            return;
        }

        int slot = findDropSlot(player);
        if (slot == -1) {
            return;
        }

        int previousSlot = player.getInventory().selectedSlot;
        player.getInventory().selectedSlot = slot;
        player.dropSelectedItem(true);
        player.getInventory().selectedSlot = previousSlot;
        cooldown = delay.getInt();
    }

    private int findDropSlot(ClientPlayerEntity player) {
        for (int i = 0; i < 9; i++) {
            Item item = player.getInventory().getStack(i).getItem();
            if (shouldDrop(item)) {
                return i;
            }
        }
        return -1;
    }

    private boolean shouldDrop(Item item) {
        if (dirt.get() && (item == Items.DIRT || item == Items.COARSE_DIRT || item == Items.ROOTED_DIRT)) {
            return true;
        }
        if (cobble.get() && (item == Items.COBBLESTONE || item == Items.COBBLED_DEEPSLATE)) {
            return true;
        }
        return rottenFlesh.get() && item == Items.ROTTEN_FLESH;
    }
}
