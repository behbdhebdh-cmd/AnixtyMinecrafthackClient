package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;

/** Hides the suffocation/in-wall overlay. */
public class NoWallOverlay extends Module {
    public NoWallOverlay() {
        super("NoWallOverlay", "Hides the in-wall overlay.", Category.RENDER);
    }
}
