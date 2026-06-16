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

/** Highlights nearby Nether and End portal blocks (throttled + cached). */
public class PortalESP extends Module {
    private final NumberSetting radius = new NumberSetting("Radius", 32, 8, 96, 1);

    private final BlockScanner scanner = new BlockScanner();

    public PortalESP() {
        super("PortalESP", "Highlights nearby portal blocks.", Category.RENDER);
        addSettings(radius);
    }

    @Override
    public void onRender3D(WorldRenderContext context) {
        if (mc.world == null || mc.player == null) {
            return;
        }
        int r = radius.getInt();
        for (BlockPos pos : scanner.scan(mc.player.getBlockPos(), r, r, this::matches)) {
            Render3D.drawBox(context, new Box(pos), 0.8f, 0.25f, 1.0f, 1.0f);
        }
    }

    private boolean matches(BlockPos pos) {
        BlockState state = mc.world.getBlockState(pos);
        return state.isOf(Blocks.NETHER_PORTAL) || state.isOf(Blocks.END_PORTAL)
                || state.isOf(Blocks.END_PORTAL_FRAME);
    }
}
