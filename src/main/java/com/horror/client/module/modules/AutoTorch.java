package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;
import com.horror.client.module.setting.NumberSetting;
import net.minecraft.block.BlockState;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

/** Places a torch at your feet when the local light level gets low. */
public class AutoTorch extends Module {
    private final NumberSetting light = new NumberSetting("Light", 7, 0, 15, 1);
    private final NumberSetting delay = new NumberSetting("Delay", 10, 2, 40, 1);

    private int cooldown;

    public AutoTorch() {
        super("AutoTorch", "Places torches when the light level is low.", Category.WORLD);
        addSettings(light, delay);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) {
            return;
        }
        if (cooldown > 0) {
            cooldown--;
            return;
        }

        BlockPos feet = mc.player.getBlockPos();
        BlockPos placePos = feet.down();
        if (mc.world.getLightLevel(feet) > light.getInt() || !canPlaceAt(feet) || !isSolid(placePos)) {
            return;
        }

        int torchSlot = findTorch();
        if (torchSlot == -1) {
            return;
        }

        int previousSlot = mc.player.getInventory().selectedSlot;
        mc.player.getInventory().selectedSlot = torchSlot;
        BlockHitResult hit = new BlockHitResult(Vec3d.ofCenter(placePos), Direction.UP, placePos, false);
        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hit);
        mc.player.swingHand(Hand.MAIN_HAND);
        mc.player.getInventory().selectedSlot = previousSlot;
        cooldown = delay.getInt();
    }

    private boolean canPlaceAt(BlockPos pos) {
        BlockState state = mc.world.getBlockState(pos);
        return state.isAir() || state.getCollisionShape(mc.world, pos).isEmpty();
    }

    private boolean isSolid(BlockPos pos) {
        return !mc.world.getBlockState(pos).getCollisionShape(mc.world, pos).isEmpty();
    }

    private int findTorch() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).isOf(Items.TORCH)) {
                return i;
            }
        }
        return -1;
    }
}
