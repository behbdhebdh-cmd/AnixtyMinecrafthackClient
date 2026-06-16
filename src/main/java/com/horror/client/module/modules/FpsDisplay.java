package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;

/**
 * Shows the current frame rate on the HUD. Read by {@code HudRenderer}.
 */
public class FpsDisplay extends Module {
    public FpsDisplay() {
        super("FpsDisplay", "Shows your current frame rate.", Category.MISC);
    }
}
