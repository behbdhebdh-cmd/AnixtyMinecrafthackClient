package com.horror.client.module.modules;

import com.horror.client.gui.render.BlockScanner;
import com.horror.client.gui.render.Render3D;
import com.horror.client.module.Category;
import com.horror.client.module.Module;
import com.horror.client.module.setting.ModeSetting;
import com.horror.client.module.setting.NumberSetting;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.Set;

/** Wurst-style block search for common singleplayer targets (throttled + cached). */
public class SearchESP extends Module {
    private final ModeSetting target = new ModeSetting("Target", "Diamond",
            "Diamond", "AncientDebris", "Emerald", "Gold", "Redstone", "Spawner", "Amethyst");
    private final NumberSetting radius = new NumberSetting("Radius", 32, 8, 96, 1);

    private final BlockScanner scanner = new BlockScanner();

    public SearchESP() {
        super("SearchESP", "Highlights a selected block type nearby.", Category.RENDER);
        addSettings(target, radius);
    }

    @Override
    public void onRender3D(WorldRenderContext context) {
        if (mc.world == null || mc.player == null) {
            return;
        }
        Set<Block> targets = blocks();
        int r = radius.getInt();
        for (BlockPos pos : scanner.scan(mc.player.getBlockPos(), r, r,
                p -> targets.contains(mc.world.getBlockState(p).getBlock()))) {
            Render3D.drawBox(context, new Box(pos), 0.0f, 0.9f, 0.15f, 1.0f);
        }
    }

    private Set<Block> blocks() {
        return switch (target.get()) {
            case "AncientDebris" -> Set.of(Blocks.ANCIENT_DEBRIS);
            case "Emerald" -> Set.of(Blocks.EMERALD_ORE, Blocks.DEEPSLATE_EMERALD_ORE);
            case "Gold" -> Set.of(Blocks.GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE, Blocks.NETHER_GOLD_ORE);
            case "Redstone" -> Set.of(Blocks.REDSTONE_ORE, Blocks.DEEPSLATE_REDSTONE_ORE);
            case "Spawner" -> Set.of(Blocks.SPAWNER);
            case "Amethyst" -> Set.of(Blocks.AMETHYST_BLOCK, Blocks.BUDDING_AMETHYST);
            default -> Set.of(Blocks.DIAMOND_ORE, Blocks.DEEPSLATE_DIAMOND_ORE);
        };
    }
}
