package com.horror.client.gui;

import com.horror.client.HorrorClient;
import com.horror.client.gui.render.Animation;
import com.horror.client.gui.render.Render2D;
import com.horror.client.module.Module;
import com.horror.client.module.ModuleManager;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

/**
 * Wurst-style search navigator: a single search box over a live-filtered list of
 * every feature. Type to filter, Up/Down to select, Enter or click to toggle.
 */
public class NavigatorScreen extends Screen {
    private static final int BOX_W = 260;
    private static final int ROW_H = 16;
    private static final int MAX_ROWS = 9;

    private String search = "";
    private int selected;
    private int scroll;
    private final Animation openAnim = new Animation(0);

    public NavigatorScreen() {
        super(Text.literal("Anixty Navigator"));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private List<Module> results() {
        String q = search.trim().toLowerCase();
        List<Module> list = new ArrayList<>();
        for (Module m : ModuleManager.getModules()) {
            if (q.isEmpty()
                    || m.getName().toLowerCase().contains(q)
                    || m.getCategory().title.toLowerCase().contains(q)
                    || m.getDescription().toLowerCase().contains(q)) {
                list.add(m);
            }
        }
        return list;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        Render2D.setAlpha((float) openAnim.to(1.0, 12));
        try {
            draw(ctx, mouseX, mouseY);
        } catch (Exception ignored) {
            // never crash on a render glitch
        } finally {
            Render2D.resetAlpha();
        }
        super.render(ctx, mouseX, mouseY, delta);
    }

    private void draw(DrawContext ctx, int mouseX, int mouseY) {
        Render2D.roundedRect(ctx, 0, 0, this.width, this.height, 0, Theme.BACKDROP);
        TextRenderer font = this.textRenderer;

        int x = (this.width - BOX_W) / 2;
        int top = this.height / 5;

        // Search bar.
        Render2D.roundedRect(ctx, x, top, BOX_W, 22, 4, Theme.HEADER);
        Render2D.rect(ctx, x, top, 3, 22, Theme.ACCENT);
        String shown = search.isEmpty() ? "Search " + HorrorClient.NAME + "..." : search;
        Render2D.text(ctx, font, shown, x + 10, top + 7f, 1.0f, search.isEmpty() ? Theme.TEXT_DIM : Theme.TEXT);

        List<Module> results = results();
        clampSelection(results.size());

        int listTop = top + 26;
        int rows = Math.min(MAX_ROWS, results.size());
        int listH = Math.max(ROW_H, rows * ROW_H) + 4;
        Render2D.roundedRect(ctx, x, listTop, BOX_W, listH, 4, Theme.PANEL);

        for (int i = 0; i < rows; i++) {
            int idx = scroll + i;
            Module m = results.get(idx);
            int ry = listTop + 2 + i * ROW_H;
            boolean hover = mouseX >= x && mouseX <= x + BOX_W && mouseY >= ry && mouseY <= ry + ROW_H;
            if (idx == selected || hover) {
                Render2D.rect(ctx, x + 2, ry, BOX_W - 4, ROW_H, Theme.HEADER);
            }
            if (m.isEnabled()) {
                Render2D.rect(ctx, x + 2, ry, 2, ROW_H, Theme.ACCENT);
            }
            Render2D.text(ctx, font, m.getName(), x + 10, ry + 4f, 0.9f,
                    m.isEnabled() ? Theme.TEXT : Theme.TEXT_DIM);
            String cat = m.getCategory().title;
            Render2D.text(ctx, font, cat, x + BOX_W - Render2D.textWidth(font, cat, 0.75f) - 8, ry + 4.5f,
                    0.75f, Theme.TEXT_DIM);
        }
        if (results.isEmpty()) {
            Render2D.text(ctx, font, "No matches", x + 10, listTop + 6f, 0.9f, Theme.TEXT_DIM);
        }

        Render2D.text(ctx, font, "Enter: toggle    Up/Down: select    Esc: close",
                x, listTop + listH + 6, 0.75f, Theme.TEXT_DIM);
    }

    private void clampSelection(int size) {
        if (selected >= size) {
            selected = Math.max(0, size - 1);
        }
        if (selected < scroll) {
            scroll = selected;
        }
        if (selected >= scroll + MAX_ROWS) {
            scroll = selected - MAX_ROWS + 1;
        }
        int maxScroll = Math.max(0, size - MAX_ROWS);
        scroll = Math.max(0, Math.min(scroll, maxScroll));
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (!Character.isISOControl(chr)) {
            search += chr;
            selected = 0;
            scroll = 0;
            return true;
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        List<Module> results = results();
        return switch (keyCode) {
            case GLFW.GLFW_KEY_ESCAPE -> {
                this.close();
                yield true;
            }
            case GLFW.GLFW_KEY_BACKSPACE -> {
                if (!search.isEmpty()) {
                    search = search.substring(0, search.length() - 1);
                }
                selected = 0;
                scroll = 0;
                yield true;
            }
            case GLFW.GLFW_KEY_DOWN -> {
                if (!results.isEmpty()) {
                    selected = Math.min(results.size() - 1, selected + 1);
                }
                yield true;
            }
            case GLFW.GLFW_KEY_UP -> {
                if (!results.isEmpty()) {
                    selected = Math.max(0, selected - 1);
                }
                yield true;
            }
            case GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER -> {
                if (selected >= 0 && selected < results.size()) {
                    results.get(selected).toggle();
                }
                yield true;
            }
            default -> super.keyPressed(keyCode, scanCode, modifiers);
        };
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (this.width - BOX_W) / 2;
        int top = this.height / 5;
        int listTop = top + 26;
        List<Module> results = results();
        int rows = Math.min(MAX_ROWS, results.size());
        for (int i = 0; i < rows; i++) {
            int ry = listTop + 2 + i * ROW_H;
            if (mouseX >= x && mouseX <= x + BOX_W && mouseY >= ry && mouseY <= ry + ROW_H) {
                if (button == 0) {
                    results.get(scroll + i).toggle();
                }
                selected = scroll + i;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        scroll -= (int) Math.signum(amount);
        scroll = Math.max(0, scroll);
        return true;
    }
}
