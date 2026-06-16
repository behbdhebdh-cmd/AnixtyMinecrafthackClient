package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;

/** Pushes client-side fog far away. */
public class NoFog extends Module {
    public NoFog() {
        super("NoFog", "Reduces client-side fog.", Category.RENDER);
    }
}
