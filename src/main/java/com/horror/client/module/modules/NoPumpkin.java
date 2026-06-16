package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;

/** Removes the carved pumpkin blur overlay. */
public class NoPumpkin extends Module {
    public NoPumpkin() {
        super("NoPumpkin", "Hides the pumpkin blur overlay.", Category.RENDER);
    }
}
