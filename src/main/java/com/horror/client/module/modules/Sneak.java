package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;

/** Holds sneak without keeping the key pressed manually. */
public class Sneak extends Module {
    public Sneak() {
        super("Sneak", "Keeps you sneaking.", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        if (mc.options != null) {
            mc.options.sneakKey.setPressed(true);
        }
    }

    @Override
    public void onDisable() {
        if (mc.options != null) {
            mc.options.sneakKey.setPressed(false);
        }
    }
}
