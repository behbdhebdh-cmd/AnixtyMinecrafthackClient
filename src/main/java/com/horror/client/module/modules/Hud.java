package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;

/**
 * Master switch for the in-game overlay (watermark + module list).
 * {@code HudRenderer} only draws when this module is enabled.
 */
public class Hud extends Module {
    public Hud() {
        super("Hud", "Toggles the on-screen overlay.", Category.MISC);
    }
}
