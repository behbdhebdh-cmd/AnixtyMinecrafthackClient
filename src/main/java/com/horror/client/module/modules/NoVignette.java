package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;

/** Removes the dark screen-edge vignette. */
public class NoVignette extends Module {
    public NoVignette() {
        super("NoVignette", "Removes the dark edge vignette.", Category.RENDER);
    }
}
