package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

/**
 * Continuously breaks the block the player is looking at.
 */
public class AutoMine extends Module {
    public AutoMine() {
        super("AutoMine", "Mines the block you are looking at.", Category.WORLD);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.interactionManager == null) {
            return;
        }
        if (mc.crosshairTarget instanceof BlockHitResult hit && hit.getType() == HitResult.Type.BLOCK) {
            mc.interactionManager.updateBlockBreakingProgress(hit.getBlockPos(), hit.getSide());
            mc.player.swingHand(Hand.MAIN_HAND);
        }
    }
}
