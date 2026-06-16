package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;

/** Hides the underwater screen tint overlay. */
public class NoWaterOverlay extends Module {
    public NoWaterOverlay() {
        super("NoWaterOverlay", "Hides the underwater overlay.", Category.RENDER);
    }
}
