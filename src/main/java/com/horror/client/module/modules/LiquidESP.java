package com.horror.client.module.modules;

import com.horror.client.gui.render.BlockScanner;
import com.horror.client.gui.render.Render3D;
import com.horror.client.module.Category;
import com.horror.client.module.Module;
import com.horror.client.module.setting.BooleanSetting;
import com.horror.client.module.setting.NumberSetting;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

/** Highlights water and lava blocks around the player (throttled + cached). */
public class LiquidESP extends Module {
    private final BooleanSetting water = new BooleanSetting("Water", true);
    private final BooleanSetting lava = new BooleanSetting("Lava", true);
    private final NumberSetting radius = new NumberSetting("Radius", 24, 8, 64, 1);

    private final BlockScanner scanner = new BlockScanner();

    public LiquidESP() {
        super("LiquidESP", "Highlights nearby water and lava.", Category.RENDER);
        addSettings(water, lava, radius);
    }

    @Override
    public void onRender3D(WorldRenderContext context) {
        if (mc.world == null || mc.player == null) {
            return;
        }
        int r = radius.getInt();
        for (BlockPos pos : scanner.scan(mc.player.getBlockPos(), r, r, this::matches)) {
            BlockState state = mc.world.getBlockState(pos);
            if (state.isOf(Blocks.WATER)) {
                Render3D.drawBox(context, new Box(pos), 0.15f, 0.45f, 1.0f, 1.0f);
            } else if (state.isOf(Blocks.LAVA)) {
                Render3D.drawBox(context, new Box(pos), 1.0f, 0.28f, 0.05f, 1.0f);
            }
        }
    }

    private boolean matches(BlockPos pos) {
        BlockState state = mc.world.getBlockState(pos);
        return (water.get() && state.isOf(Blocks.WATER)) || (lava.get() && state.isOf(Blocks.LAVA));
    }
}
