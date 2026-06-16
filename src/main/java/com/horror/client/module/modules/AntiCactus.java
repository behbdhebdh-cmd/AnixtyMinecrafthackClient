package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;

/** Gives cactus blocks a full collision box so you do not brush into them. */
public class AntiCactus extends Module {
    public AntiCactus() {
        super("AntiCactus", "Prevents walking into cactus damage.", Category.PLAYER);
    }
}
