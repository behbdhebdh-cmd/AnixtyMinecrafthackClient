package com.horror.client.module.modules;

import com.horror.client.gui.render.BlockScanner;
import com.horror.client.gui.render.Render3D;
import com.horror.client.module.Category;
import com.horror.client.module.Module;
import com.horror.client.module.setting.NumberSetting;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

/** Highlights invisible barrier blocks nearby (throttled + cached). */
public class BarrierESP extends Module {
    private final NumberSetting radius = new NumberSetting("Radius", 24, 4, 64, 1);

    private final BlockScanner scanner = new BlockScanner();

    public BarrierESP() {
        super("BarrierESP", "Highlights nearby barrier blocks.", Category.RENDER);
        addSettings(radius);
    }

    @Override
    public void onRender3D(WorldRenderContext context) {
        if (mc.world == null || mc.player == null) {
            return;
        }
        int r = radius.getInt();
        for (BlockPos pos : scanner.scan(mc.player.getBlockPos(), r, r, this::matches)) {
            Render3D.drawBox(context, new Box(pos), 1.0f, 0.1f, 0.1f, 1.0f);
        }
    }

    private boolean matches(BlockPos pos) {
        BlockState state = mc.world.getBlockState(pos);
        return state.isOf(Blocks.BARRIER) || state.isOf(Blocks.STRUCTURE_VOID);
    }
}
