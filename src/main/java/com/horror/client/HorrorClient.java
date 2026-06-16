package com.horror.client;

import com.horror.client.gui.ClickGui;
import com.horror.client.gui.HudRenderer;
import com.horror.client.gui.NavigatorScreen;
import com.horror.client.module.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/**
 * Client entry point for the Horror singleplayer trainer.
 */
public class HorrorClient implements ClientModInitializer {
    public static final String MOD_ID = "horror";
    public static final String NAME = "Anixty";
    public static final String VERSION = "1.0.0";

    private static KeyBinding openGuiKey;
    private static KeyBinding openSearchKey;

    @Override
    public void onInitializeClient() {
        ModuleManager.init();

        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.horror.openGui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.horror.main"
        ));

        openSearchKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.horror.openSearch",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_N,
                "category.horror.main"
        ));

        // Per-tick module logic.
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGuiKey.wasPressed()) {
                if (client.world != null) {
                    client.setScreen(new ClickGui());
                }
            }
            while (openSearchKey.wasPressed()) {
                if (client.world != null) {
                    client.setScreen(new NavigatorScreen());
                }
            }
            if (client.world != null) {
                ModuleManager.onKeybinds();
                ModuleManager.onTick();
            }
        });

        // World-space rendering (ESP / Tracers).
        WorldRenderEvents.AFTER_ENTITIES.register(ModuleManager::onRender3D);

        HudRenderer.register();
    }
}
