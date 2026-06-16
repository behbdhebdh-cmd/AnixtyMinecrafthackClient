package com.horror.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.horror.client.module.modules.Xray;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Xray: only draw ore block faces, cull everything else.
 *
 * <p>{@code shouldDrawSide} runs for every block face during chunk meshing, so
 * this must be allocation-free. Using {@link ModifyReturnValue} (instead of a
 * cancellable {@code @Inject}) avoids the per-call {@code CallbackInfoReturnable}
 * allocation that otherwise causes heavy GC churn and stutter while chunks build.
 * A plain {@code volatile boolean} gate keeps the disabled path a single read.
 */
@Mixin(Block.class)
public class BlockMixin {
    @ModifyReturnValue(method = "shouldDrawSide", at = @At("RETURN"))
    private static boolean horror$xray(boolean original, BlockState state, BlockView world,
                                       BlockPos pos, Direction side, BlockPos otherPos) {
        if (!Xray.enabled) {
            return original;
        }
        return Xray.ORES.contains(state.getBlock());
    }
}
