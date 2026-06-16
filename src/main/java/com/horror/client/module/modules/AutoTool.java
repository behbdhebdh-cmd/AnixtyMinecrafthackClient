package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;
import com.horror.client.module.setting.BooleanSetting;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

/** Selects the fastest hotbar tool for the block currently being mined. */
public class AutoTool extends Module {
    private final BooleanSetting restoreSlot = new BooleanSetting("Restore", true);

    private int previousSlot = -1;
    private boolean switched;

    public AutoTool() {
        super("AutoTool", "Switches to the best tool while mining.", Category.PLAYER);
        addSettings(restoreSlot);
    }

    @Override
    public void onTick() {
        ClientPlayerEntity player = mc.player;
        if (player == null || mc.world == null || !(mc.crosshairTarget instanceof BlockHitResult hit)
                || hit.getType() != HitResult.Type.BLOCK || !mc.options.attackKey.isPressed()) {
            restoreIfNeeded();
            return;
        }

        BlockState state = mc.world.getBlockState(hit.getBlockPos());
        int bestSlot = findBestTool(player, state);
        if (bestSlot == -1 || bestSlot == player.getInventory().selectedSlot) {
            return;
        }

        if (!switched) {
            previousSlot = player.getInventory().selectedSlot;
            switched = true;
        }
        player.getInventory().selectedSlot = bestSlot;
    }

    @Override
    public void onDisable() {
        restoreIfNeeded();
    }

    private int findBestTool(ClientPlayerEntity player, BlockState state) {
        int bestSlot = -1;
        float bestSpeed = 1.0f;
        for (int slot = 0; slot < 9; slot++) {
            ItemStack stack = player.getInventory().getStack(slot);
            if (stack.isEmpty()) {
                continue;
            }
            float speed = stack.getMiningSpeedMultiplier(state);
            if (speed > bestSpeed) {
                bestSpeed = speed;
                bestSlot = slot;
            }
        }
        return bestSlot;
    }

    private void restoreIfNeeded() {
        if (!switched) {
            return;
        }
        if (restoreSlot.get() && previousSlot >= 0 && mc.player != null) {
            mc.player.getInventory().selectedSlot = previousSlot;
        }
        previousSlot = -1;
        switched = false;
    }
}
