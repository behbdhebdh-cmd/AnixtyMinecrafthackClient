package com.horror.client.gui;

import com.horror.client.HorrorClient;
import com.horror.client.gui.render.Render2D;
import com.horror.client.module.Module;
import com.horror.client.module.ModuleManager;
import com.horror.client.module.modules.FpsDisplay;
import com.horror.client.module.modules.Hud;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;

import java.util.List;

/**
 * Modern in-game overlay: a rounded watermark chip, optional FPS, and a clean
 * module list with accent edges. Presentation only.
 */
public final class HudRenderer {
    private HudRenderer() {}

    public static void register() {
        HudRenderCallback.EVENT.register(HudRenderer::onHudRender);
    }

    private static void onHudRender(DrawContext ctx, float tickDelta) {
        try {
            draw(ctx);
        } catch (Exception ignored) {
            // A HUD rendering glitch must never crash the game.
        }
    }

    private static void draw(DrawContext ctx) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.options.hudHidden || mc.currentScreen instanceof ChatScreen
                || mc.currentScreen instanceof ClickGui) {
            return;
        }
        if (!ModuleManager.isOn(Hud.class)) {
            return;
        }

        TextRenderer font = mc.textRenderer;
        float scale = 0.9f;

        // Watermark chip.
        String watermark = HorrorClient.NAME;
        float ww = Render2D.textWidth(font, watermark, 1.0f);
        Render2D.roundedRect(ctx, 5, 5, ww + 16, 15, 5, Theme.TOOLTIP_BG);
        Render2D.roundedRect(ctx, 8, 8, 3, 9, 1.5f, Theme.ACCENT);
        Render2D.text(ctx, font, watermark, 15, 8.5f, 1.0f, Theme.TEXT);

        if (ModuleManager.isOn(FpsDisplay.class)) {
            Render2D.text(ctx, font, mc.getCurrentFps() + " FPS", 7, 23f, scale, Theme.TEXT_DIM);
        }

        // Module list (top-right), widest first.
        List<Module> enabled = ModuleManager.getEnabledForHud();
        if (enabled.isEmpty()) {
            return;
        }
        enabled.sort((a, b) -> font.getWidth(b.getName()) - font.getWidth(a.getName()));

        int screenWidth = ctx.getScaledWindowWidth();
        float y = 5;
        for (Module module : enabled) {
            String name = module.getName();
            float w = Render2D.textWidth(font, name, scale);
            float x = screenWidth - w - 12;
            Render2D.roundedRect(ctx, x - 6, y, w + 12, 13, 3, Theme.TOOLTIP_BG);
            Render2D.roundedRect(ctx, screenWidth - 2.5f, y, 2.5f, 13, 1.2f, Theme.ACCENT);
            Render2D.text(ctx, font, name, x, y + 3f, scale, Theme.TEXT);
            y += 15;
        }
    }
}
