package com.horror.client.gui.widget;

import com.horror.client.gui.Theme;
import com.horror.client.gui.render.Animation;
import com.horror.client.gui.render.Render2D;
import com.horror.client.module.Module;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

/**
 * One module row: animated hover highlight, a sliding on/off pill, and a small
 * dot hinting at available settings.
 */
public class ModuleButton {
    public static final int HEIGHT = 14;
    private static final float TEXT_SCALE = 0.78f;

    private final Module module;
    private final Animation hover = new Animation(0);
    private final Animation toggle;
    private final Animation expand = new Animation(0);

    public ModuleButton(Module module) {
        this.module = module;
        this.toggle = new Animation(module.isEnabled() ? 1 : 0);
    }

    public Module getModule() {
        return module;
    }

    public boolean isHovered(int x, int y, int width, int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + HEIGHT;
    }

    /** Eased 0..1 progress of the settings sub-panel; advanced once per frame. */
    public double expandProgress() {
        return expand.to(module.isExpanded() ? 1 : 0, 13);
    }

    public void render(DrawContext ctx, TextRenderer font, int x, int y, int width,
                       int mouseX, int mouseY) {
        double h = hover.to(isHovered(x, y, width, mouseX, mouseY) ? 1 : 0, 16);
        double t = toggle.to(module.isEnabled() ? 1 : 0, 14);

        int right = module.hasSettings() ? width - 14 : width - 2;
        int base = Render2D.lerpColor(Theme.PANEL_INNER, Theme.ACCENT_SOFT, (float) t);
        if (h > 0.001) {
            base = Render2D.lerpColor(base, Theme.HEADER, (float) (h * 0.45));
        }

        Render2D.rect(ctx, x + 2, y + 1, right - 2, HEIGHT - 2, base);
        Render2D.rect(ctx, x + 2, y + HEIGHT - 1, width - 4, 1, Theme.BORDER);
        if (module.isEnabled()) {
            Render2D.rect(ctx, x + 2, y + 1, 2, HEIGHT - 2, Theme.ACCENT);
        }
        if (module.isFavorite()) {
            Render2D.rect(ctx, x + 5, y + 4, 2, 2, Theme.ACCENT);
            Render2D.rect(ctx, x + 4, y + 6, 4, 1, Theme.ACCENT);
            Render2D.rect(ctx, x + 5, y + 7, 2, 2, Theme.ACCENT);
        }

        int nameColor = Render2D.lerpColor(Theme.TEXT_DIM, Theme.TEXT, (float) t);
        Render2D.text(ctx, font, module.getName(), x + (module.isFavorite() ? 11 : 8), y + 4f,
                TEXT_SCALE, nameColor);

        if (module.getKey() >= 0) {
            String key = keyName(module.getKey());
            float keyWidth = Render2D.textWidth(font, key, 0.68f);
            int keyRight = module.hasSettings() ? x + width - 17 : x + width - 6;
            Render2D.text(ctx, font, key, keyRight - keyWidth, y + 4.5f, 0.68f, Theme.TEXT_DIM);
        }

        if (module.hasSettings()) {
            int sx = x + width - 14;
            boolean settingsHover = mouseX >= sx && mouseX <= x + width - 2 && mouseY >= y && mouseY <= y + HEIGHT;
            Render2D.rect(ctx, sx, y + 1, 12, HEIGHT - 2, settingsHover ? Theme.HEADER : Theme.PANEL_INNER);
            int color = module.isExpanded() ? Theme.ACCENT_2 : Theme.ACCENT;
            Render2D.rect(ctx, sx + 4, y + 5, 5, 1, color);
            Render2D.rect(ctx, sx + 6, y + 3, 1, 5, color);
        }
    }

    public static String keyName(int key) {
        String name = GLFW.glfwGetKeyName(key, 0);
        if (name != null) {
            return name.toUpperCase();
        }
        return switch (key) {
            case GLFW.GLFW_KEY_LEFT_SHIFT, GLFW.GLFW_KEY_RIGHT_SHIFT -> "SHIFT";
            case GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_RIGHT_CONTROL -> "CTRL";
            case GLFW.GLFW_KEY_LEFT_ALT, GLFW.GLFW_KEY_RIGHT_ALT -> "ALT";
            case GLFW.GLFW_KEY_SPACE -> "SPACE";
            case GLFW.GLFW_KEY_ENTER -> "ENTER";
            default -> String.valueOf(key);
        };
    }
}
