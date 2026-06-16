package com.horror.client.module;

import com.horror.client.module.setting.Setting;
import com.horror.client.config.ConfigManager;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Base type for every trainer feature.
 *
 * <p>Subclasses override the lifecycle hooks they need:
 * <ul>
 *   <li>{@link #onEnable()} / {@link #onDisable()} - one-shot state changes</li>
 *   <li>{@link #onTick()} - runs every client tick while enabled</li>
 *   <li>{@link #onRender3D(WorldRenderContext)} - world-space rendering</li>
 * </ul>
 */
public abstract class Module {
    protected static final MinecraftClient mc = MinecraftClient.getInstance();

    private final String name;
    private final String description;
    private final Category category;
    private final List<Setting> settings = new ArrayList<>();

    private int key = -1;
    private boolean enabled;
    private boolean favorite;

    /** UI state: whether the settings sub-panel is open in the ClickGUI. */
    private boolean expanded;

    protected Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    protected void addSettings(Setting... toAdd) {
        for (Setting setting : toAdd) {
            settings.add(setting);
        }
    }

    public final void toggle() {
        setEnabled(!enabled);
    }

    public final void setEnabled(boolean state) {
        if (state == enabled) {
            return;
        }
        enabled = state;
        ModuleManager.onModuleStateChanged(this, enabled);
        if (enabled) {
            onEnable();
        } else {
            onDisable();
        }
        ConfigManager.save();
    }

    // ---- Lifecycle hooks (override as needed) ----
    public void onEnable() {}

    public void onDisable() {}

    public void onTick() {}

    public void onRender3D(WorldRenderContext context) {}

    // ---- Accessors ----
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }

    public List<Setting> getSettings() {
        return settings;
    }

    public boolean hasSettings() {
        return !settings.isEmpty();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        int oldKey = this.key;
        this.key = key;
        ModuleManager.onModuleKeyChanged(this, oldKey, key);
        ConfigManager.save();
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
        ConfigManager.save();
    }

    public void toggleFavorite() {
        setFavorite(!favorite);
    }
}
