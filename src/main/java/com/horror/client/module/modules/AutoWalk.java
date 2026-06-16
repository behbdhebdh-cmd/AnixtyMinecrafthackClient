package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;

/** Holds the forward key for long singleplayer walks. */
public class AutoWalk extends Module {
    public AutoWalk() {
        super("AutoWalk", "Keeps walking forward.", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        if (mc.options != null) {
            mc.options.forwardKey.setPressed(true);
        }
    }

    @Override
    public void onDisable() {
        if (mc.options != null) {
            mc.options.forwardKey.setPressed(false);
        }
    }
}
