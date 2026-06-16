package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/** Reduces powder snow sinking and slowdown. */
public class SnowShoe extends Module {
    public SnowShoe() {
        super("SnowShoe", "Move over powder snow more easily.", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) {
            return;
        }
        BlockPos pos = mc.player.getBlockPos();
        if (!mc.world.getBlockState(pos).isOf(Blocks.POWDER_SNOW)) {
            return;
        }
        Vec3d velocity = mc.player.getVelocity();
        if (velocity.y < 0) {
            mc.player.setVelocity(velocity.x, 0, velocity.z);
        }
    }
}
