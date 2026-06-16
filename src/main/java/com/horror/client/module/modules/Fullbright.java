package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;

/**
 * Maximises the gamma so the world appears fully lit. Restores the previous
 * gamma when disabled.
 */
public class Fullbright extends Module {
    private double previousGamma = 0.5;

    public Fullbright() {
        super("Fullbright", "Lights up the world as if it were day.", Category.RENDER);
    }

    @Override
    public void onEnable() {
        if (mc.options != null) {
            previousGamma = mc.options.getGamma().getValue();
        }
    }

    @Override
    public void onTick() {
        if (mc.options != null) {
            mc.options.getGamma().setValue(1.0);
        }
    }

    @Override
    public void onDisable() {
        if (mc.options != null) {
            mc.options.getGamma().setValue(previousGamma);
        }
    }
}
