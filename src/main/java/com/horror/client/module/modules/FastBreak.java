package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;
import com.horror.client.module.setting.NumberSetting;

/**
 * Speeds up block breaking. The multiplier is applied in
 * {@code PlayerEntityMixin#horror$fastBreak}.
 */
public class FastBreak extends Module {
    private final NumberSetting multiplier = new NumberSetting("Multiplier", 2.0, 1.0, 5.0, 0.1);

    public FastBreak() {
        super("FastBreak", "Speeds up block breaking.", Category.PLAYER);
        addSettings(multiplier);
    }

    public float getMultiplier() {
        return (float) multiplier.get();
    }
}
