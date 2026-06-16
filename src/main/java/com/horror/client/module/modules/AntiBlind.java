package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;
import net.minecraft.entity.effect.StatusEffects;

/** Removes blindness and darkness effects in singleplayer. */
public class AntiBlind extends Module {
    public AntiBlind() {
        super("AntiBlind", "Removes blindness and darkness.", Category.RENDER);
    }

    @Override
    public void onTick() {
        if (mc.player != null) {
            mc.player.removeStatusEffect(StatusEffects.BLINDNESS);
            mc.player.removeStatusEffect(StatusEffects.DARKNESS);
        }
    }
}
