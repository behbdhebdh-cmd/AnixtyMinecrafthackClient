package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.hit.EntityHitResult;

/**
 * Wurst-style AutoSword: when the crosshair is on a living entity, switches the
 * held hotbar slot to the strongest melee weapon available.
 */
public class AutoSword extends Module {
    public AutoSword() {
        super("AutoSword", "Switches to your best melee weapon when aiming at a mob.", Category.COMBAT);
    }

    @Override
    public void onTick() {
        if (mc.player == null) {
            return;
        }
        if (!(mc.crosshairTarget instanceof EntityHitResult hit) || !(hit.getEntity() instanceof LivingEntity)) {
            return;
        }

        int bestSlot = -1;
        double bestDamage = -1;
        for (int i = 0; i < 9; i++) {
            double damage = meleeDamage(mc.player.getInventory().getStack(i));
            if (damage > bestDamage) {
                bestDamage = damage;
                bestSlot = i;
            }
        }
        if (bestSlot != -1 && bestDamage > 1.0) {
            mc.player.getInventory().selectedSlot = bestSlot;
        }
    }

    private double meleeDamage(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        double damage = 1.0;
        for (EntityAttributeModifier modifier : stack.getAttributeModifiers(EquipmentSlot.MAINHAND)
                .get(EntityAttributes.GENERIC_ATTACK_DAMAGE)) {
            damage += modifier.getValue();
        }
        if (stack.getItem() instanceof SwordItem) {
            damage += 0.6; // prefer swords over axes at equal damage (faster, no cooldown penalty)
        } else if (stack.getItem() instanceof AxeItem) {
            damage += 0.3;
        }
        return damage;
    }
}
