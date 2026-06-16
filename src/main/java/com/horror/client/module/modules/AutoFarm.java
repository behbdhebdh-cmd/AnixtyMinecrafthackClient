package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;
import com.horror.client.module.setting.NumberSetting;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CocoaBlock;
import net.minecraft.block.CropBlock;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

/** Breaks mature crops around the player. */
public class AutoFarm extends Module {
    private final NumberSetting radius = new NumberSetting("Radius", 4, 1, 6, 1);
    private final NumberSetting delay = new NumberSetting("Delay", 4, 1, 20, 1);

    private int cooldown;

    public AutoFarm() {
        super("AutoFarm", "Harvests mature crops nearby.", Category.WORLD);
        addSettings(radius, delay);
    }

    @Override
    public void onTick() {
        ClientPlayerEntity player = mc.player;
        if (player == null || mc.world == null || mc.interactionManager == null) {
            return;
        }
        if (cooldown > 0) {
            cooldown--;
            return;
        }

        int r = radius.getInt();
        BlockPos center = player.getBlockPos();
        for (BlockPos pos : BlockPos.iterate(center.add(-r, -1, -r), center.add(r, 2, r))) {
            BlockState state = mc.world.getBlockState(pos);
            if (!isMature(state)) {
                continue;
            }

            mc.interactionManager.attackBlock(pos, Direction.UP);
            player.swingHand(Hand.MAIN_HAND);
            cooldown = delay.getInt();
            return;
        }
    }

    private boolean isMature(BlockState state) {
        Block block = state.getBlock();
        if (block instanceof CropBlock crop) {
            return crop.isMature(state);
        }
        if (block instanceof NetherWartBlock) {
            return state.get(NetherWartBlock.AGE) >= 3;
        }
        if (block instanceof CocoaBlock) {
            return state.get(CocoaBlock.AGE) >= CocoaBlock.MAX_AGE;
        }
        return false;
    }
}
