package com.horror.client.mixin;

import com.horror.client.module.ModuleManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.CactusBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.block.ShapeContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** AntiCactus: expands cactus collision to a full block. */
@Mixin(CactusBlock.class)
public class CactusBlockMixin {
    @Inject(method = "getCollisionShape", at = @At("HEAD"), cancellable = true)
    private void horror$antiCactus(BlockState state, BlockView world, BlockPos pos,
                                   ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (ModuleManager.isAntiCactusOn()) {
            cir.setReturnValue(VoxelShapes.fullCube());
        }
    }
}
