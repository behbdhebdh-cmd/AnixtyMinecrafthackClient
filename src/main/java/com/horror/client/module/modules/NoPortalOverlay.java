package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;

/** Removes the Nether portal screen wobble overlay. */
public class NoPortalOverlay extends Module {
    public NoPortalOverlay() {
        super("NoPortalOverlay", "Hides the portal overlay.", Category.RENDER);
    }
}
