package com.horror.client.gui.render;

/**
 * A frame-rate independent eased value that glides toward a target.
 * Used for hover/toggle/expand transitions in the ClickGUI.
 */
public class Animation {
    private double value;
    private long last = System.nanoTime();

    public Animation(double initial) {
        this.value = initial;
    }

    public double get() {
        return value;
    }

    /** Eases toward {@code target}. Higher {@code speed} = snappier. */
    public double to(double target, double speed) {
        long now = System.nanoTime();
        double dt = Math.min(0.1, (now - last) / 1.0e9);
        last = now;
        double t = 1.0 - Math.exp(-speed * dt);
        value += (target - value) * t;
        if (Math.abs(target - value) < 0.0005) {
            value = target;
        }
        return value;
    }
}
