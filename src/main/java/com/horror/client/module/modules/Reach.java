package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;
import com.horror.client.module.setting.NumberSetting;

/**
 * Extends interaction range. The value is consumed by
 * {@code ClientPlayerInteractionManagerMixin#horror$reach}.
 */
public class Reach extends Module {
    private final NumberSetting range = new NumberSetting("Range", 5.0, 3.0, 6.0, 0.1);

    public Reach() {
        super("Reach", "Extends your interaction range.", Category.COMBAT);
        addSettings(range);
    }

    public double getRange() {
        return range.get();
    }
}
