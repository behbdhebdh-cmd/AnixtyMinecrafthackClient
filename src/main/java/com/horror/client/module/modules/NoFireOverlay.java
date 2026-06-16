package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;

/** Hides the burning overlay while you are on fire. */
public class NoFireOverlay extends Module {
    public NoFireOverlay() {
        super("NoFireOverlay", "Hides the fire overlay.", Category.RENDER);
    }
}
