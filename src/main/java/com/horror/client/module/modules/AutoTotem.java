package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

/**
 * Keeps a totem of undying in the off-hand by swapping one in from the inventory.
 */
public class AutoTotem extends Module {
    public AutoTotem() {
        super("AutoTotem", "Keeps a totem of undying in your off-hand.", Category.COMBAT);
    }

    @Override
    public void onTick() {
        ClientPlayerEntity player = mc.player;
        if (player == null || mc.interactionManager == null) {
            return;
        }
        if (player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING)) {
            return;
        }

        int inventorySlot = -1;
        for (int i = 0; i < player.getInventory().size(); i++) {
            if (player.getInventory().getStack(i).isOf(Items.TOTEM_OF_UNDYING)) {
                inventorySlot = i;
                break;
            }
        }
        if (inventorySlot == -1) {
            return;
        }

        // Map inventory index -> player screen handler slot id, then swap with off-hand (button 40).
        int screenSlot = inventorySlot < 9 ? inventorySlot + 36 : inventorySlot;
        mc.interactionManager.clickSlot(
                player.playerScreenHandler.syncId, screenSlot, 40, SlotActionType.SWAP, player);
    }
}
