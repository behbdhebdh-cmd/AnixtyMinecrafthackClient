package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;
import com.horror.client.module.setting.NumberSetting;
import net.minecraft.client.network.ClientPlayerEntity;

/**
 * Detaches the camera from the player's body. The camera position lives in the
 * static fields below and is applied by {@code CameraMixin}; this module moves
 * that position with the movement keys while keeping the body still.
 */
public class Freecam extends Module {
    public static boolean active;
    public static double x;
    public static double y;
    public static double z;
    // Previous-tick position, used to interpolate the camera per frame for smooth motion.
    public static double prevX;
    public static double prevY;
    public static double prevZ;

    private final NumberSetting speed = new NumberSetting("Speed", 1.0, 0.2, 3.0, 0.1);

    public static double renderX(float tickDelta) {
        return prevX + (x - prevX) * tickDelta;
    }

    public static double renderY(float tickDelta) {
        return prevY + (y - prevY) * tickDelta;
    }

    public static double renderZ(float tickDelta) {
        return prevZ + (z - prevZ) * tickDelta;
    }

    public Freecam() {
        super("Freecam", "Detach the camera from your body.", Category.RENDER);
        addSettings(speed);
    }

    @Override
    public void onEnable() {
        ClientPlayerEntity player = mc.player;
        if (player == null) {
            setEnabled(false);
            return;
        }
        x = player.getX();
        y = player.getY() + player.getStandingEyeHeight();
        z = player.getZ();
        prevX = x;
        prevY = y;
        prevZ = z;
        active = true;
    }

    @Override
    public void onDisable() {
        active = false;
    }

    @Override
    public void onTick() {
        ClientPlayerEntity player = mc.player;
        if (player == null || !active) {
            return;
        }

        // Keep the body still.
        player.setVelocity(0, 0, 0);

        // Remember last position so the camera can interpolate between ticks.
        prevX = x;
        prevY = y;
        prevZ = z;

        double s = speed.get();
        if (player.input.jumping) {
            y += s;
        }
        if (player.input.sneaking) {
            y -= s;
        }

        double forward = player.input.movementForward;
        double sideways = player.input.movementSideways;
        if (forward != 0 || sideways != 0) {
            double rad = Math.toRadians(player.getYaw());
            double sin = Math.sin(rad);
            double cos = Math.cos(rad);
            x += (forward * -sin + sideways * cos) * s;
            z += (forward * cos + sideways * sin) * s;
        }
    }
}
