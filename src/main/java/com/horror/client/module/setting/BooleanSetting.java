package com.horror.client.module.setting;

/** An on/off toggle setting rendered as a checkbox. */
public class BooleanSetting extends Setting {
    private boolean value;

    public BooleanSetting(String name, boolean defaultValue) {
        super(name);
        this.value = defaultValue;
    }

    public boolean get() {
        return value;
    }

    public void set(boolean value) {
        this.value = value;
    }

    public void toggle() {
        this.value = !this.value;
    }
}
