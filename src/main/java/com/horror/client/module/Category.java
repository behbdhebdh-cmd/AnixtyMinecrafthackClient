package com.horror.client.module;

/**
 * Module categories shown as separate panels in the ClickGUI.
 */
public enum Category {
    COMBAT("Combat"),
    MOVEMENT("Movement"),
    PLAYER("Player"),
    RENDER("Render"),
    WORLD("World"),
    MISC("Misc");

    public final String title;

    Category(String title) {
        this.title = title;
    }
}
