package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;
import com.horror.client.module.setting.BooleanSetting;
import com.horror.client.module.setting.NumberSetting;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

/** Breaks connected ores/logs of the same type while mining. */
public class VeinMiner extends Module {
    private final BooleanSetting ores = new BooleanSetting("Ores", true);
    private final BooleanSetting logs = new BooleanSetting("Logs", true);
    private final NumberSetting limit = new NumberSetting("Limit", 24, 2, 96, 1);
    private final NumberSetting perTick = new NumberSetting("PerTick", 2, 1, 8, 1);

    private final Queue<BlockPos> queue = new ArrayDeque<>();
    private Block sourceBlock;

    public VeinMiner() {
        super("VeinMiner", "Mines connected ore or log veins.", Category.WORLD);
        addSettings(ores, logs, limit, perTick);
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

        BlockPos start = hit.getBlockPos();
        BlockState state = mc.world.getBlockState(start);
        if (!isAllowed(state)) {
            queue.clear();
            sourceBlock = null;
            return;
        }

        if (queue.isEmpty() || sourceBlock != state.getBlock()) {
            sourceBlock = state.getBlock();
            buildQueue(start, sourceBlock);
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

    private boolean isAllowed(BlockState state) {
        Block block = state.getBlock();
        if (ores.get() && (block == Blocks.ANCIENT_DEBRIS || block == Blocks.NETHER_QUARTZ_ORE
                || block == Blocks.NETHER_GOLD_ORE || block == Blocks.COAL_ORE
                || block == Blocks.DEEPSLATE_COAL_ORE || block == Blocks.IRON_ORE
                || block == Blocks.DEEPSLATE_IRON_ORE || block == Blocks.COPPER_ORE
                || block == Blocks.DEEPSLATE_COPPER_ORE || block == Blocks.GOLD_ORE
                || block == Blocks.DEEPSLATE_GOLD_ORE || block == Blocks.REDSTONE_ORE
                || block == Blocks.DEEPSLATE_REDSTONE_ORE || block == Blocks.LAPIS_ORE
                || block == Blocks.DEEPSLATE_LAPIS_ORE || block == Blocks.DIAMOND_ORE
                || block == Blocks.DEEPSLATE_DIAMOND_ORE || block == Blocks.EMERALD_ORE
                || block == Blocks.DEEPSLATE_EMERALD_ORE)) {
            return true;
        }
        return logs.get() && state.isIn(BlockTags.LOGS);
    }
}
