package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;

/**
 * Wurst-style AutoArmor: equips the best armor piece found in the main inventory
 * for each armor slot, one swap at a time. Skips work while a screen is open so
 * it never fights the player's own inventory interactions.
 */
public class AutoArmor extends Module {
    private static final EquipmentSlot[] SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };
    // PlayerScreenHandler armor slot ids for head/chest/legs/feet.
    private static final int[] ARMOR_SLOT_IDS = {5, 6, 7, 8};

    private int delay;

    public AutoArmor() {
        super("AutoArmor", "Equips the best armor from your inventory.", Category.COMBAT);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.interactionManager == null) {
            return;
        }
        // Don't interfere with an open container or a held (cursor) stack.
        if (mc.currentScreen != null || !mc.player.playerScreenHandler.getCursorStack().isEmpty()) {
            return;
        }
        if (delay > 0) {
            delay--;
            return;
        }

        for (int s = 0; s < SLOTS.length; s++) {
            EquipmentSlot slot = SLOTS[s];
            int currentProtection = protection(mc.player.getEquippedStack(slot), slot);

            int bestInvSlot = -1;
            int bestProtection = currentProtection;
            for (int i = 0; i < 36; i++) { // hotbar + main only (not the armor/offhand slots)
                ItemStack stack = mc.player.getInventory().getStack(i);
                if (stack.getItem() instanceof ArmorItem armor && armor.getSlotType() == slot
                        && armor.getProtection() > bestProtection) {
                    bestProtection = armor.getProtection();
                    bestInvSlot = i;
                }
            }

            if (bestInvSlot != -1) {
                equip(bestInvSlot, ARMOR_SLOT_IDS[s]);
                delay = 4; // space the swaps out over a few ticks
                return;
            }
        }
    }

    private int protection(ItemStack stack, EquipmentSlot slot) {
        if (stack.getItem() instanceof ArmorItem armor && armor.getSlotType() == slot) {
            return armor.getProtection();
        }
        return -1; // empty slot is worse than any armor
    }

    private void equip(int invSlot, int armorSlotId) {
        int syncId = mc.player.playerScreenHandler.syncId;
        int source = invSlot < 9 ? invSlot + 36 : invSlot; // hotbar 0-8 -> 36-44, main 9-35 unchanged
        mc.interactionManager.clickSlot(syncId, source, 0, SlotActionType.PICKUP, mc.player);     // pick up new armor
        mc.interactionManager.clickSlot(syncId, armorSlotId, 0, SlotActionType.PICKUP, mc.player); // place into armor slot
        mc.interactionManager.clickSlot(syncId, source, 0, SlotActionType.PICKUP, mc.player);     // return old piece
    }
}
