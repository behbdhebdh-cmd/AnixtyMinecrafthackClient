package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;

/** Extinguishes the local player in singleplayer. */
public class AntiFire extends Module {
    public AntiFire() {
        super("AntiFire", "Extinguishes you when burning.", Category.PLAYER);
    }

    @Override
    public void onTick() {
        if (mc.player != null && mc.player.isOnFire()) {
            mc.player.extinguish();
        }
    }
}
