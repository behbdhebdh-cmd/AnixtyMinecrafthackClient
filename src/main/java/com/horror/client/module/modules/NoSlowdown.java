package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;

/**
 * Removes the movement slowdown applied while using items (eating, drawing a bow,
 * etc.). The constant is patched in {@code ClientPlayerEntityMixin}.
 */
public class NoSlowdown extends Module {
    public NoSlowdown() {
        super("NoSlowdown", "Move at full speed while using items.", Category.PLAYER);
    }
}
