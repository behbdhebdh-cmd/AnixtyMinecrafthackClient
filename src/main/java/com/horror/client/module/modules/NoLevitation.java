package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;
import net.minecraft.entity.effect.StatusEffects;

/** Removes the levitation status effect in singleplayer. */
public class NoLevitation extends Module {
    public NoLevitation() {
        super("NoLevitation", "Removes levitation.", Category.RENDER);
    }

    @Override
    public void onTick() {
        if (mc.player != null) {
            mc.player.removeStatusEffect(StatusEffects.LEVITATION);
        }
    }
}
