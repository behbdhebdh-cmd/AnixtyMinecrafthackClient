package com.horror.client.module.modules;

import com.horror.client.gui.render.Render3D;
import com.horror.client.module.Category;
import com.horror.client.module.Module;
import com.horror.client.module.setting.BooleanSetting;
import com.horror.client.module.setting.NumberSetting;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.List;

/**
 * Draws a line from the crosshair to each living entity.
 */
public class Tracers extends Module {
    private final BooleanSetting players = new BooleanSetting("Players", true);
    private final BooleanSetting mobs = new BooleanSetting("Mobs", true);
    private final BooleanSetting filterInvisible = new BooleanSetting("FilterInvisible", false);
    private final BooleanSetting filterSleeping = new BooleanSetting("FilterSleeping", false);
    private final BooleanSetting filterArmorStands = new BooleanSetting("FilterArmorStands", true);
    private final NumberSetting range = new NumberSetting("Range", 128, 16, 256, 4);
    private final NumberSetting maxTargets = new NumberSetting("MaxTargets", 128, 16, 512, 8);
    private final NumberSetting updateMs = new NumberSetting("UpdateMs", 150, 50, 1000, 50);

    private final List<LivingEntity> targets = new ArrayList<>();
    private long lastUpdate;

    public Tracers() {
        super("Tracers", "Draws lines to nearby entities.", Category.RENDER);
        addSettings(players, mobs, filterInvisible, filterSleeping, filterArmorStands,
                range, maxTargets, updateMs);
    }

    @Override
    public void onRender3D(WorldRenderContext context) {
        if (mc.world == null || mc.player == null) {
            return;
        }
        updateTargets();
        float tickDelta = context.tickDelta();
        for (LivingEntity entity : targets) {
            if (entity.isRemoved() || !entity.isAlive()) {
                continue;
            }
            Box box = Render3D.lerpedBox(entity, tickDelta, 0);
            float[] color = distanceColor(entity);
            Render3D.drawTracer(context, box.getCenter(), color[0], color[1], color[2], 0.72f);
        }
    }

    private void updateTargets() {
        long now = System.currentTimeMillis();
        if (now - lastUpdate < updateMs.getInt()) {
            return;
        }
        lastUpdate = now;
        targets.clear();

        double maxDistanceSq = range.get() * range.get();
        int limit = maxTargets.getInt();
        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof LivingEntity) || entity == mc.player) {
                continue;
            }
            if (entity instanceof PlayerEntity) {
                if (!players.get()) {
                    continue;
                }
                if (filterSleeping.get() && ((PlayerEntity) entity).isSleeping()) {
                    continue;
                }
            } else {
                if (!mobs.get()) {
                    continue;
                }
                if (filterArmorStands.get() && entity instanceof ArmorStandEntity) {
                    continue;
                }
            }
            if (filterInvisible.get() && entity.isInvisible()) {
                continue;
            }
            if (entity.squaredDistanceTo(mc.player) > maxDistanceSq) {
                continue;
            }
            targets.add((LivingEntity) entity);
            if (targets.size() >= limit) {
                break;
            }
        }
    }

    private float[] distanceColor(Entity entity) {
        float f = mc.player.distanceTo(entity) / 20.0f;
        float red = Math.max(0.0f, Math.min(1.0f, 2.0f - f));
        float green = Math.max(0.0f, Math.min(1.0f, f));
        return new float[]{red, green, 0.0f};
    }

    @Override
    public void onDisable() {
        targets.clear();
        lastUpdate = 0;
    }
}
