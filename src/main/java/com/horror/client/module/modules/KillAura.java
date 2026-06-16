package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;
import com.horror.client.module.setting.BooleanSetting;
import com.horror.client.module.setting.ModeSetting;
import com.horror.client.module.setting.NumberSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

/**
 * Wurst-style KillAura: picks the best living target within range and field of
 * view by a configurable priority, optionally faces it, and attacks at a set CPS.
 */
public class KillAura extends Module {
    private final NumberSetting range = new NumberSetting("Range", 4.0, 2.0, 6.0, 0.1);
    private final NumberSetting cps = new NumberSetting("CPS", 8, 1, 20, 1);
    private final NumberSetting fov = new NumberSetting("FOV", 180, 30, 180, 5);
    private final ModeSetting priority = new ModeSetting("Priority", "Distance", "Distance", "Health", "Angle");
    private final BooleanSetting rotate = new BooleanSetting("Rotate", true);
    private final BooleanSetting players = new BooleanSetting("Players", true);
    private final BooleanSetting mobs = new BooleanSetting("Mobs", true);

    private long lastAttack;

    public KillAura() {
        super("KillAura", "Automatically attacks the best nearby target.", Category.COMBAT);
        addSettings(range, cps, fov, priority, rotate, players, mobs);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - lastAttack < 1000.0 / cps.get()) {
            return;
        }

        LivingEntity best = null;
        double bestScore = Double.MAX_VALUE;
        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof LivingEntity living) || entity == mc.player || !entity.isAlive()) {
                continue;
            }
            if (entity instanceof PlayerEntity) {
                if (!players.get()) {
                    continue;
                }
            } else if (!mobs.get()) {
                continue;
            }

            double distance = mc.player.distanceTo(entity);
            if (distance > range.get()) {
                continue;
            }
            double angle = angleTo(entity);
            if (angle > fov.get() / 2.0) {
                continue;
            }

            double score = switch (priority.get()) {
                case "Health" -> living.getHealth();
                case "Angle" -> angle;
                default -> distance;
            };
            if (score < bestScore) {
                bestScore = score;
                best = living;
            }
        }

        if (best == null) {
            return;
        }
        if (rotate.get()) {
            faceEntity(best);
        }
        mc.interactionManager.attackEntity(mc.player, best);
        mc.player.swingHand(Hand.MAIN_HAND);
        lastAttack = now;
    }

    private double angleTo(Entity entity) {
        Vec3d look = mc.player.getRotationVec(1.0f);
        Vec3d toTarget = entity.getPos().add(0, entity.getStandingEyeHeight(), 0)
                .subtract(mc.player.getEyePos()).normalize();
        double dot = Math.max(-1.0, Math.min(1.0, look.dotProduct(toTarget)));
        return Math.toDegrees(Math.acos(dot));
    }

    private void faceEntity(Entity entity) {
        double dx = entity.getX() - mc.player.getX();
        double dz = entity.getZ() - mc.player.getZ();
        double dy = (entity.getY() + entity.getStandingEyeHeight())
                - (mc.player.getY() + mc.player.getStandingEyeHeight());
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
        float pitch = (float) (-Math.toDegrees(Math.atan2(dy, horizontal)));
        mc.player.setYaw(yaw);
        mc.player.setPitch(pitch);
    }
}
