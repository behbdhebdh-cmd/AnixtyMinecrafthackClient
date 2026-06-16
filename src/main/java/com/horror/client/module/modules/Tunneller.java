package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;
import com.horror.client.module.setting.NumberSetting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

/** Digs a simple two-block-high tunnel in front of the player. */
public class Tunneller extends Module {
    private final NumberSetting reach = new NumberSetting("Reach", 4, 1, 6, 1);
    private final NumberSetting perTick = new NumberSetting("PerTick", 2, 1, 6, 1);

    public Tunneller() {
        super("Tunneller", "Digs a 1x2 tunnel in front of you.", Category.WORLD);
        addSettings(reach, perTick);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null || mc.interactionManager == null || !mc.options.attackKey.isPressed()) {
            return;
        }

        Direction facing = mc.player.getHorizontalFacing();
        BlockPos base = mc.player.getBlockPos();
        int broken = 0;

        for (int i = 1; i <= reach.getInt() && broken < perTick.getInt(); i++) {
            BlockPos lower = base.offset(facing, i);
            broken += tryBreak(lower) ? 1 : 0;
            if (broken >= perTick.getInt()) {
                break;
            }
            broken += tryBreak(lower.up()) ? 1 : 0;
        }
    }

    private boolean tryBreak(BlockPos pos) {
        if (mc.world.getBlockState(pos).isAir() || mc.world.getBlockState(pos).getHardness(mc.world, pos) < 0) {
            return false;
        }
        mc.interactionManager.updateBlockBreakingProgress(pos, Direction.UP);
        mc.player.swingHand(Hand.MAIN_HAND);
        return true;
    }
}
