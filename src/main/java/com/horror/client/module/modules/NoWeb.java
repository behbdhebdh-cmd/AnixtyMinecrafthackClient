package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;

/** Removes the movement slowdown from cobwebs. */
public class NoWeb extends Module {
    public NoWeb() {
        super("NoWeb", "Removes cobweb slowdown.", Category.MOVEMENT);
    }
}
