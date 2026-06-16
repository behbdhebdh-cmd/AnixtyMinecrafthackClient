package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;
import com.horror.client.module.setting.BooleanSetting;
import com.horror.client.module.setting.NumberSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

/** Recasts and reels in a fishing rod when the bobber dips under water. */
public class AutoFish extends Module {
    private final NumberSetting recastDelay = new NumberSetting("RecastDelay", 12, 0, 60, 1);
    private final BooleanSetting autoCast = new BooleanSetting("AutoCast", true);

    private int cooldown;

    public AutoFish() {
        super("AutoFish", "Automatically catches fish with a fishing rod.", Category.PLAYER);
        addSettings(recastDelay, autoCast);
    }

    @Override
    public void onTick() {
        ClientPlayerEntity player = mc.player;
        if (player == null || mc.interactionManager == null || !player.getMainHandStack().isOf(Items.FISHING_ROD)) {
            cooldown = 0;
            return;
        }

        if (cooldown > 0) {
            cooldown--;
            return;
        }

        FishingBobberEntity bobber = player.fishHook;
        if (bobber == null) {
            if (autoCast.get()) {
                useRod(player);
            }
            return;
        }

        if (bobber.age > 20 && bobber.getVelocity().y < -0.08) {
            useRod(player);
            cooldown = recastDelay.getInt();
        }
    }

    private void useRod(ClientPlayerEntity player) {
        mc.interactionManager.interactItem(player, Hand.MAIN_HAND);
        player.swingHand(Hand.MAIN_HAND);
    }
}
