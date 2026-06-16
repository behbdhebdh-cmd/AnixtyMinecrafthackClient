package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;
import com.horror.client.module.setting.NumberSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.Hand;

/**
 * Breaks nearby end crystals automatically.
 */
public class AutoCrystal extends Module {
    private final NumberSetting range = new NumberSetting("Range", 4.0, 1.0, 6.0, 0.1);

    public AutoCrystal() {
        super("AutoCrystal", "Breaks nearby end crystals automatically.", Category.COMBAT);
        addSettings(range);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) {
            return;
        }

        EndCrystalEntity closest = null;
        double best = range.get();
        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof EndCrystalEntity crystal) {
                double distance = mc.player.distanceTo(crystal);
                if (distance <= best) {
                    best = distance;
                    closest = crystal;
                }
            }
        }

        if (closest != null) {
            mc.interactionManager.attackEntity(mc.player, closest);
            mc.player.swingHand(Hand.MAIN_HAND);
        }
    }
}
