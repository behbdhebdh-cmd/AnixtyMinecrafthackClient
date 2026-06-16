package com.horror.client.module.setting;

/**
 * Base type for a module setting shown in the ClickGUI.
 */
public abstract class Setting {
    private final String name;

    protected Setting(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
