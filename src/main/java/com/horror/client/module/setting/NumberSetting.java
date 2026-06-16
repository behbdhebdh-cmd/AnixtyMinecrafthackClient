package com.horror.client.module.setting;

/** A numeric setting rendered as a slider. */
public class NumberSetting extends Setting {
    private double value;
    private final double min;
    private final double max;
    private final double step;

    public NumberSetting(String name, double defaultValue, double min, double max, double step) {
        super(name);
        this.min = min;
        this.max = max;
        this.step = step;
        set(defaultValue);
    }

    public double get() {
        return value;
    }

    public int getInt() {
        return (int) Math.round(value);
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public void set(double newValue) {
        double clamped = Math.max(min, Math.min(max, newValue));
        // Snap to the configured step.
        this.value = Math.round(clamped / step) * step;
    }

    /** Sets the value from a normalised slider fraction (0..1). */
    public void setFromFraction(double fraction) {
        set(min + (max - min) * Math.max(0.0, Math.min(1.0, fraction)));
    }

    /** Returns the value as a normalised slider fraction (0..1). */
    public double getFraction() {
        return (value - min) / (max - min);
    }
}
