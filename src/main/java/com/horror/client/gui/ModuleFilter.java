package com.horror.client.gui;

/** Top-level module views for the ClickGUI. */
public enum ModuleFilter {
    ALL("All"),
    ENABLED("On"),
    FAVORITES("Fav");

    public final String label;

    ModuleFilter(String label) {
        this.label = label;
    }
}
