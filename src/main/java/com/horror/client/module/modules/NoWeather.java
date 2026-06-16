package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;

/** Hides rain and thunder on the client side. */
public class NoWeather extends Module {
    public NoWeather() {
        super("NoWeather", "Hides rain and thunder.", Category.RENDER);
    }

    @Override
    public void onTick() {
        if (mc.world != null) {
            mc.world.setRainGradient(0);
            mc.world.setThunderGradient(0);
        }
    }
}
