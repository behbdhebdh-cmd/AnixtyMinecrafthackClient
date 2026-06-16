package com.horror.client.module.modules;

import com.horror.client.mixin.MinecraftClientAccessor;
import com.horror.client.module.Category;
import com.horror.client.module.Module;

/**
 * Removes the vanilla block/item placement cooldown.
 */
public class FastPlace extends Module {
    public FastPlace() {
        super("FastPlace", "Removes the block placement delay.", Category.PLAYER);
    }

    @Override
    public void onTick() {
        ((MinecraftClientAccessor) mc).setItemUseCooldown(0);
    }
}
