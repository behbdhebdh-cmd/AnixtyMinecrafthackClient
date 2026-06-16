package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;

/**
 * Forces critical hits on every melee attack.
 *
 * <p>The actual packet logic lives in {@code ClientPlayerInteractionManagerMixin},
 * which checks whether this module is enabled before each attack.
 */
public class Criticals extends Module {
    public Criticals() {
        super("Criticals", "Forces critical hits on every attack.", Category.COMBAT);
    }
}
