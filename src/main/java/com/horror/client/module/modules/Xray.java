package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

import java.util.HashSet;
import java.util.Set;

/**
 * Reveals ores by hiding common blocks. The block-culling logic lives in
 * {@code BlockMixin#horror$xray}; this class just holds the visible-ore set and
 * triggers a chunk re-render on toggle.
 */
public class Xray extends Module {
    public static final Set<Block> ORES = new HashSet<>();

    /** Fast gate read on the chunk-meshing hot path (avoids a map lookup per block face). */
    public static volatile boolean enabled;

    static {
        ORES.add(Blocks.COAL_ORE);
        ORES.add(Blocks.DEEPSLATE_COAL_ORE);
        ORES.add(Blocks.IRON_ORE);
        ORES.add(Blocks.DEEPSLATE_IRON_ORE);
        ORES.add(Blocks.COPPER_ORE);
        ORES.add(Blocks.DEEPSLATE_COPPER_ORE);
        ORES.add(Blocks.GOLD_ORE);
        ORES.add(Blocks.DEEPSLATE_GOLD_ORE);
        ORES.add(Blocks.REDSTONE_ORE);
        ORES.add(Blocks.DEEPSLATE_REDSTONE_ORE);
        ORES.add(Blocks.LAPIS_ORE);
        ORES.add(Blocks.DEEPSLATE_LAPIS_ORE);
        ORES.add(Blocks.DIAMOND_ORE);
        ORES.add(Blocks.DEEPSLATE_DIAMOND_ORE);
        ORES.add(Blocks.EMERALD_ORE);
        ORES.add(Blocks.DEEPSLATE_EMERALD_ORE);
        ORES.add(Blocks.NETHER_GOLD_ORE);
        ORES.add(Blocks.NETHER_QUARTZ_ORE);
        ORES.add(Blocks.ANCIENT_DEBRIS);
    }

    public Xray() {
        super("Xray", "Reveals ores by hiding common blocks.", Category.RENDER);
    }

    @Override
    public void onEnable() {
        enabled = true;
        reload();
    }

    @Override
    public void onDisable() {
        enabled = false;
        reload();
    }

    private void reload() {
        if (mc.worldRenderer != null) {
            mc.worldRenderer.reload();
        }
    }
}
