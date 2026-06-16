package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;
import com.horror.client.module.setting.NumberSetting;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

/** Uses bonemeal on the block you are looking at. */
public class AutoBonemeal extends Module {
    private final NumberSetting delay = new NumberSetting("Delay", 6, 1, 40, 1);
    private int cooldown;

    public AutoBonemeal() {
        super("AutoBonemeal", "Uses bonemeal on growable blocks.", Category.WORLD);
        addSettings(delay);
    }

    @Override
    public void onTick() {
        ClientPlayerEntity player = mc.player;
        if (player == null || mc.world == null || mc.interactionManager == null
                || !(mc.crosshairTarget instanceof BlockHitResult hit)
                || hit.getType() != HitResult.Type.BLOCK) {
            return;
        }
        if (cooldown > 0) {
            cooldown--;
            return;
        }

        BlockState state = mc.world.getBlockState(hit.getBlockPos());
        if (!isGrowableTarget(state)) {
            return;
        }

        int slot = findBonemeal(player);
        if (slot == -1) {
            return;
        }

        int previous = player.getInventory().selectedSlot;
        player.getInventory().selectedSlot = slot;
        mc.interactionManager.interactBlock(player, Hand.MAIN_HAND, hit);
        player.swingHand(Hand.MAIN_HAND);
        player.getInventory().selectedSlot = previous;
        cooldown = delay.getInt();
    }

    private int findBonemeal(ClientPlayerEntity player) {
        for (int i = 0; i < 9; i++) {
            if (player.getInventory().getStack(i).isOf(Items.BONE_MEAL)) {
                return i;
            }
        }
        return -1;
    }

    private boolean isGrowableTarget(BlockState state) {
        return state.isOf(Blocks.WHEAT) || state.isOf(Blocks.CARROTS) || state.isOf(Blocks.POTATOES)
                || state.isOf(Blocks.BEETROOTS) || state.isOf(Blocks.OAK_SAPLING)
                || state.isOf(Blocks.SPRUCE_SAPLING) || state.isOf(Blocks.BIRCH_SAPLING)
                || state.isOf(Blocks.JUNGLE_SAPLING) || state.isOf(Blocks.ACACIA_SAPLING)
                || state.isOf(Blocks.DARK_OAK_SAPLING) || state.isOf(Blocks.MANGROVE_PROPAGULE)
                || state.isOf(Blocks.MELON_STEM) || state.isOf(Blocks.PUMPKIN_STEM)
                || state.isOf(Blocks.COCOA) || state.isOf(Blocks.BAMBOO) || state.isOf(Blocks.SWEET_BERRY_BUSH);
    }
}
