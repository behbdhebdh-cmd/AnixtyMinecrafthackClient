package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;
import com.horror.client.module.setting.NumberSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

/**
 * Breaks the nearest breakable block within range each tick.
 */
public class Nuker extends Module {
    private final NumberSetting range = new NumberSetting("Range", 4.0, 1.0, 6.0, 0.5);

    public Nuker() {
        super("Nuker", "Breaks blocks around you automatically.", Category.WORLD);
        addSettings(range);
    }

    @Override
    public void onTick() {
        ClientPlayerEntity player = mc.player;
        if (player == null || mc.world == null || mc.interactionManager == null) {
            return;
        }

        int r = range.getInt();
        BlockPos origin = player.getBlockPos();
        BlockPos best = null;
        double bestDistance = Double.MAX_VALUE;

        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -r; dy <= r; dy++) {
                for (int dz = -r; dz <= r; dz++) {
                    BlockPos pos = origin.add(dx, dy, dz);
                    if (mc.world.getBlockState(pos).isAir()) {
                        continue;
                    }
                    if (mc.world.getBlockState(pos).getHardness(mc.world, pos) < 0) {
                        continue; // unbreakable (e.g. bedrock)
                    }
                    double distance = pos.getSquaredDistance(player.getPos());
                    if (distance <= (double) r * r && distance < bestDistance) {
                        bestDistance = distance;
                        best = pos;
                    }
                }
            }
        }

        if (best != null) {
            mc.interactionManager.updateBlockBreakingProgress(best, Direction.UP);
            player.swingHand(Hand.MAIN_HAND);
        }
    }
}
