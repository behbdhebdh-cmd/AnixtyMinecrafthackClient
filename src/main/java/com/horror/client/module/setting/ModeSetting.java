package com.horror.client.module.setting;

import java.util.Arrays;
import java.util.List;

/** A multiple-choice setting cycled by clicking. */
public class ModeSetting extends Setting {
    private final List<String> modes;
    private int index;

    public ModeSetting(String name, String defaultMode, String... modes) {
        super(name);
        this.modes = Arrays.asList(modes);
        int i = this.modes.indexOf(defaultMode);
        this.index = i < 0 ? 0 : i;
    }

    public String get() {
        return modes.get(index);
    }

    public boolean is(String mode) {
        return get().equals(mode);
    }

    public void set(String mode) {
        int i = modes.indexOf(mode);
        if (i >= 0) {
            index = i;
        }
    }

    public void cycle() {
        index = (index + 1) % modes.size();
    }

    public List<String> getModes() {
        return modes;
    }
}
