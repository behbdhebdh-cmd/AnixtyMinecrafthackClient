package com.horror.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.horror.client.HorrorClient;
import com.horror.client.module.Category;
import com.horror.client.module.Module;
import com.horror.client.module.ModuleManager;
import com.horror.client.module.setting.BooleanSetting;
import com.horror.client.module.setting.ModeSetting;
import com.horror.client.module.setting.NumberSetting;
import com.horror.client.module.setting.Setting;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;

/** Saves the parts of the client that should survive a restart. */
public final class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir()
            .resolve(HorrorClient.MOD_ID).resolve("client.json");
    private static final Map<Category, PanelState> PANEL_STATES = new EnumMap<>(Category.class);

    private static boolean loading;
    private static boolean initialized;

    private ConfigManager() {}

    public static void load() {
        loading = true;
        try {
            if (!Files.exists(FILE)) {
                initialized = true;
                return;
            }

            try (Reader reader = Files.newBufferedReader(FILE)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
                loadModules(root.getAsJsonObject("modules"));
                loadPanels(root.getAsJsonObject("panels"));
            }
        } catch (Exception ignored) {
            // Broken configs should not prevent the trainer from loading.
        } finally {
            initialized = true;
            loading = false;
        }
    }

    public static void save() {
        if (loading || !initialized) {
            return;
        }

        try {
            Files.createDirectories(FILE.getParent());
            JsonObject root = new JsonObject();
            root.add("modules", writeModules());
            root.add("panels", writePanels());

            try (Writer writer = Files.newBufferedWriter(FILE)) {
                GSON.toJson(root, writer);
            }
        } catch (IOException ignored) {
        }
    }

    public static PanelState getPanelState(Category category) {
        return PANEL_STATES.get(category);
    }

    public static void setPanelState(Category category, int x, int y, boolean expanded, boolean pinned) {
        PANEL_STATES.put(category, new PanelState(x, y, expanded, pinned));
        save();
    }

    public static boolean isLoading() {
        return loading;
    }

    private static void loadModules(JsonObject modules) {
        if (modules == null) {
            return;
        }

        for (Module module : ModuleManager.getModules()) {
            JsonObject json = getObject(modules, module.getName());
            if (json == null) {
                continue;
            }

            if (json.has("enabled")) {
                module.setEnabled(json.get("enabled").getAsBoolean());
            }
            if (json.has("key")) {
                module.setKey(json.get("key").getAsInt());
            }
            if (json.has("favorite")) {
                module.setFavorite(json.get("favorite").getAsBoolean());
            }

            JsonObject settings = getObject(json, "settings");
            if (settings == null) {
                continue;
            }
            for (Setting setting : module.getSettings()) {
                JsonElement value = settings.get(setting.getName());
                if (value == null) {
                    continue;
                }
                if (setting instanceof BooleanSetting bool) {
                    bool.set(value.getAsBoolean());
                } else if (setting instanceof NumberSetting number) {
                    number.set(value.getAsDouble());
                } else if (setting instanceof ModeSetting mode) {
                    mode.set(value.getAsString());
                }
            }
        }
    }

    private static JsonObject writeModules() {
        JsonObject modules = new JsonObject();
        for (Module module : ModuleManager.getModules()) {
            JsonObject json = new JsonObject();
            json.addProperty("enabled", module.isEnabled());
            json.addProperty("key", module.getKey());
            json.addProperty("favorite", module.isFavorite());

            JsonObject settings = new JsonObject();
            for (Setting setting : module.getSettings()) {
                if (setting instanceof BooleanSetting bool) {
                    settings.addProperty(setting.getName(), bool.get());
                } else if (setting instanceof NumberSetting number) {
                    settings.addProperty(setting.getName(), number.get());
                } else if (setting instanceof ModeSetting mode) {
                    settings.addProperty(setting.getName(), mode.get());
                }
            }
            json.add("settings", settings);
            modules.add(module.getName(), json);
        }
        return modules;
    }

    private static void loadPanels(JsonObject panels) {
        if (panels == null) {
            return;
        }
        for (Category category : Category.values()) {
            JsonObject json = getObject(panels, category.name());
            if (json == null) {
                continue;
            }
            int x = getInt(json, "x", 12);
            int y = getInt(json, "y", 30);
            boolean expanded = getBoolean(json, "expanded", true);
            boolean pinned = getBoolean(json, "pinned", false);
            PANEL_STATES.put(category, new PanelState(x, y, expanded, pinned));
        }
    }

    private static JsonObject writePanels() {
        JsonObject panels = new JsonObject();
        for (Map.Entry<Category, PanelState> entry : PANEL_STATES.entrySet()) {
            PanelState state = entry.getValue();
            JsonObject json = new JsonObject();
            json.addProperty("x", state.x());
            json.addProperty("y", state.y());
            json.addProperty("expanded", state.expanded());
            json.addProperty("pinned", state.pinned());
            panels.add(entry.getKey().name(), json);
        }
        return panels;
    }

    private static JsonObject getObject(JsonObject parent, String key) {
        JsonElement value = parent.get(key);
        return value instanceof JsonObject object ? object : null;
    }

    private static int getInt(JsonObject object, String key, int fallback) {
        JsonElement value = object.get(key);
        return value == null ? fallback : value.getAsInt();
    }

    private static boolean getBoolean(JsonObject object, String key, boolean fallback) {
        JsonElement value = object.get(key);
        return value == null ? fallback : value.getAsBoolean();
    }

    public record PanelState(int x, int y, boolean expanded, boolean pinned) {}
}
