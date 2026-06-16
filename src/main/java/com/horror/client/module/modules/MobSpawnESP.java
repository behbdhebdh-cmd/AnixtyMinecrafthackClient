package com.horror.client.module.modules;

import com.horror.client.gui.render.BlockScanner;
import com.horror.client.gui.render.Render3D;
import com.horror.client.module.Category;
import com.horror.client.module.Module;
import com.horror.client.module.setting.NumberSetting;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

/** Highlights nearby dark floor spaces where hostile mobs can spawn (throttled + cached). */
public class MobSpawnESP extends Module {
    private final NumberSetting radius = new NumberSetting("Radius", 24, 8, 64, 1);

    private final BlockScanner scanner = new BlockScanner();

    public MobSpawnESP() {
        super("MobSpawnESP", "Highlights dark spawnable floor spaces.", Category.RENDER);
        addSettings(radius);
    }

    @Override
    public void onRender3D(WorldRenderContext context) {
        if (mc.world == null || mc.player == null) {
            return;
        }
        int r = radius.getInt();
        for (BlockPos pos : scanner.scan(mc.player.getBlockPos(), r, 8, this::isSpawnSpot)) {
            Box marker = new Box(pos).contract(0.08, 0.98, 0.08).offset(0, 0.02, 0);
            Render3D.drawBox(context, marker, 1.0f, 0.12f, 0.12f, 1.0f);
        }
    }

    private boolean isSpawnSpot(BlockPos floor) {
        BlockPos feet = floor.up();
        BlockPos head = floor.up(2);
        BlockState floorState = mc.world.getBlockState(floor);
        return floorState.isSolidBlock(mc.world, floor)
                && mc.world.getBlockState(feet).isAir()
                && mc.world.getBlockState(head).isAir()
                && mc.world.getLightLevel(feet) <= 7;
    }
}
