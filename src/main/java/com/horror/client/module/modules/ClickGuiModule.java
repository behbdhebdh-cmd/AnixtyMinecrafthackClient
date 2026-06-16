package com.horror.client.module.modules;

import com.horror.client.gui.ClickGui;
import com.horror.client.module.Category;
import com.horror.client.module.Module;

/**
 * Opens the ClickGUI when toggled on. Immediately turns itself back off so it
 * behaves like a button.
 */
public class ClickGuiModule extends Module {
    public ClickGuiModule() {
        super("ClickGUI", "Opens this interface (Right Shift).", Category.MISC);
    }

    @Override
    public void onEnable() {
        mc.setScreen(new ClickGui());
        setEnabled(false);
    }
}
