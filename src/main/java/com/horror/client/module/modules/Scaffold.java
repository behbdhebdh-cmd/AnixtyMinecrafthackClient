package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

/**
 * Places a block beneath the player while moving, bridging over gaps.
 */
public class Scaffold extends Module {
    public Scaffold() {
        super("Scaffold", "Places blocks beneath you while moving.", Category.WORLD);
    }

    @Override
    public void onTick() {
        ClientPlayerEntity player = mc.player;
        if (player == null || mc.world == null || mc.interactionManager == null) {
            return;
        }
        if (!(player.getMainHandStack().getItem() instanceof BlockItem)) {
            return;
        }

        BlockPos below = player.getBlockPos().down();
        if (!mc.world.getBlockState(below).isAir()) {
            return;
        }

        // Need an existing block to place against (the block below 'below').
        BlockPos support = below.down();
        if (mc.world.getBlockState(support).isAir()) {
            return;
        }

        Vec3d hit = Vec3d.ofCenter(support).add(0, 0.5, 0);
        BlockHitResult hitResult = new BlockHitResult(hit, Direction.UP, support, false);
        mc.interactionManager.interactBlock(player, Hand.MAIN_HAND, hitResult);
        player.swingHand(Hand.MAIN_HAND);
    }
}
