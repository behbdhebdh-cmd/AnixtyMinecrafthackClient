package com.horror.client.module.modules;

import com.horror.client.mixin.EntityAccessor;
import com.horror.client.module.Category;
import com.horror.client.module.Module;
import com.horror.client.module.setting.NumberSetting;

/**
 * Raises the player's step height so they walk up full blocks like stairs.
 */
public class Step extends Module {
    private static final float VANILLA_STEP = 0.6f;

    private final NumberSetting height = new NumberSetting("Height", 1.0, 0.6, 3.0, 0.1);

    public Step() {
        super("Step", "Walk up full blocks like stairs.", Category.MOVEMENT);
        addSettings(height);
    }

    @Override
    public void onTick() {
        if (mc.player != null) {
            ((EntityAccessor) mc.player).setStepHeight((float) height.get());
        }
    }

    @Override
    public void onDisable() {
        if (mc.player != null) {
            ((EntityAccessor) mc.player).setStepHeight(VANILLA_STEP);
        }
    }
}
