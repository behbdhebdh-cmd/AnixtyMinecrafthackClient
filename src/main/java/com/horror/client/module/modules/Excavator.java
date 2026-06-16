package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;
import com.horror.client.module.setting.NumberSetting;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

/** Mines connected soft blocks like dirt, sand, gravel and clay. */
public class Excavator extends Module {
    private final NumberSetting limit = new NumberSetting("Limit", 32, 2, 128, 1);
    private final NumberSetting perTick = new NumberSetting("PerTick", 3, 1, 10, 1);

    private final Queue<BlockPos> queue = new ArrayDeque<>();
    private Block sourceBlock;

    public Excavator() {
        super("Excavator", "Mines connected soft blocks.", Category.WORLD);
        addSettings(limit, perTick);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null || mc.interactionManager == null
                || !(mc.crosshairTarget instanceof BlockHitResult hit)
                || hit.getType() != HitResult.Type.BLOCK || !mc.options.attackKey.isPressed()) {
            queue.clear();
            sourceBlock = null;
            return;
        }

        BlockState state = mc.world.getBlockState(hit.getBlockPos());
        if (!isSoftBlock(state.getBlock())) {
            queue.clear();
            sourceBlock = null;
            return;
        }

        if (queue.isEmpty() || sourceBlock != state.getBlock()) {
            sourceBlock = state.getBlock();
            buildQueue(hit.getBlockPos(), sourceBlock);
        }

        int count = perTick.getInt();
        while (count-- > 0 && !queue.isEmpty()) {
            BlockPos pos = queue.poll();
            if (!mc.world.getBlockState(pos).isOf(sourceBlock)) {
                continue;
            }
            mc.interactionManager.attackBlock(pos, Direction.UP);
            mc.player.swingHand(Hand.MAIN_HAND);
        }
    }

    @Override
    public void onDisable() {
        queue.clear();
        sourceBlock = null;
    }

    private void buildQueue(BlockPos start, Block block) {
        queue.clear();
        Set<BlockPos> visited = new HashSet<>();
        ArrayDeque<BlockPos> open = new ArrayDeque<>();
        open.add(start);
        visited.add(start);

        while (!open.isEmpty() && queue.size() < limit.getInt()) {
            BlockPos pos = open.poll();
            if (!mc.world.getBlockState(pos).isOf(block)) {
                continue;
            }
            queue.add(pos);
            for (Direction direction : Direction.values()) {
                BlockPos next = pos.offset(direction);
                if (visited.add(next)) {
                    open.add(next);
                }
            }
        }
    }

    private boolean isSoftBlock(Block block) {
        return block == Blocks.DIRT || block == Blocks.GRASS_BLOCK || block == Blocks.COARSE_DIRT
                || block == Blocks.ROOTED_DIRT || block == Blocks.SAND || block == Blocks.RED_SAND
                || block == Blocks.GRAVEL || block == Blocks.CLAY || block == Blocks.MUD
                || block == Blocks.SOUL_SAND || block == Blocks.SOUL_SOIL || block == Blocks.MOSS_BLOCK;
    }
}
