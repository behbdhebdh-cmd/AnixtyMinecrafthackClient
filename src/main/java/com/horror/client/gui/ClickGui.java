package com.horror.client.gui;

import com.horror.client.HorrorClient;
import com.horror.client.config.ConfigManager;
import com.horror.client.gui.render.Animation;
import com.horror.client.gui.render.Render2D;
import com.horror.client.gui.widget.ModuleButton;
import com.horror.client.gui.widget.Panel;
import com.horror.client.module.Category;
import com.horror.client.module.Module;
import com.horror.client.module.ModuleManager;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

/** Compact ClickGUI with draggable category windows and searchable modules. */
public class ClickGui extends Screen {
    private final List<Panel> panels = new ArrayList<>();
    private String search = "";
    private Module bindingModule;
    private ModuleFilter filter = ModuleFilter.ALL;
    private final Animation openAnim = new Animation(0);

    public ClickGui() {
        super(Text.literal("Horror ClickGUI"));
    }

    @Override
    protected void init() {
        panels.clear();
        int startX = 12;
        int startY = 30;
        int spacingX = Panel.WIDTH + 8;
        int spacingY = 154;
        int columns = Math.max(2, Math.min(4, (this.width - 24) / spacingX));
        Category[] categories = Category.values();
        for (int i = 0; i < categories.length; i++) {
            int col = i % columns;
            int row = i / columns;
            panels.add(new Panel(categories[i], startX + col * spacingX, startY + row * spacingY));
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // Smooth Vape-style fade-in of the whole interface.
        Render2D.setAlpha((float) openAnim.to(1.0, 11));
        try {
            drawGui(ctx, mouseX, mouseY);
        } catch (Exception ignored) {
            // Never let a rendering glitch crash the client.
        } finally {
            Render2D.resetAlpha();
        }
        super.render(ctx, mouseX, mouseY, delta);
    }

    private void drawGui(DrawContext ctx, int mouseX, int mouseY) {
        Render2D.roundedRect(ctx, 0, 0, this.width, this.height, 0, Theme.BACKDROP);

        TextRenderer font = this.textRenderer;

        // Title strip + Wurst-style search.
        String title = HorrorClient.NAME;
        float tw = Render2D.textWidth(font, title, 1.1f);
        Render2D.roundedRect(ctx, 8, 6, tw + 20, 16, 2, Theme.HEADER);
        Render2D.rect(ctx, 8, 6, 3, 16, Theme.ACCENT);
        Render2D.text(ctx, font, title, 16, 9f, 1.0f, Theme.TEXT);

        String status = ModuleManager.getEnabled().size() + "/" + ModuleManager.getModules().size()
                + " *" + favoriteCount();
        Render2D.roundedRect(ctx, 8 + tw + 24, 6, 54, 16, 2, Theme.HEADER);
        Render2D.text(ctx, font, status, 8 + tw + 31, 9f, 0.85f, Theme.TEXT_DIM);
        float filterEnd = drawFilterButtons(ctx, font, 8 + tw + 82, 6, mouseX, mouseY);
        drawActionButtons(ctx, font, filterEnd + 6, 6, mouseX, mouseY);

        String query = search.isEmpty() ? "Search..." : fitSearch(font, search, 130);
        int searchColor = search.isEmpty() ? Theme.TEXT_DIM : Theme.TEXT;
        Render2D.roundedRect(ctx, this.width - 156, 6, 148, 16, 2, Theme.HEADER);
        Render2D.rect(ctx, this.width - 156, 21, 148, 1, Theme.HEADER_EDGE);
        Render2D.text(ctx, font, query, this.width - 149, 9f, 0.9f, searchColor);
        if (bindingModule != null) {
            String bindText = "Bind " + bindingModule.getName();
            Render2D.roundedRect(ctx, this.width / 2f - 58, 6, 116, 16, 2, Theme.HEADER);
            Render2D.rect(ctx, this.width / 2f - 58, 21, 116, 1, Theme.ACCENT);
            Render2D.text(ctx, font, fitSearch(font, bindText, 104), this.width / 2f - 52, 9f, 0.85f, Theme.TEXT);
        }

        for (Panel panel : panels) {
            panel.render(ctx, font, mouseX, mouseY, search, filter, this.height);
        }

        Module hovered = null;
        for (Panel panel : panels) {
            Module m = panel.getHovered(mouseX, mouseY, search, filter);
            if (m != null) {
                hovered = m;
            }
        }
        if (hovered != null) {
            drawTooltip(ctx, font, hovered.getDescription(), mouseX, mouseY);
            drawInspector(ctx, font, hovered);
        }

        if (!search.isEmpty()) {
            String matches = countMatches() + " matches";
            Render2D.text(ctx, font, matches, this.width - 156, 25f, 0.75f, Theme.TEXT_DIM);
        }
    }

    private float drawFilterButtons(DrawContext ctx, TextRenderer font, float x, float y, int mouseX, int mouseY) {
        float bx = x;
        for (ModuleFilter option : ModuleFilter.values()) {
            float w = Render2D.textWidth(font, option.label, 0.75f) + 10;
            boolean active = option == filter;
            boolean hover = mouseX >= bx && mouseX <= bx + w && mouseY >= y && mouseY <= y + 16;
            int color = active ? Theme.ACCENT_SOFT : hover ? Theme.HEADER : Theme.PANEL_INNER;
            Render2D.roundedRect(ctx, bx, y, w, 16, 2, color);
            if (active) {
                Render2D.rect(ctx, bx, y + 15, w, 1, Theme.ACCENT);
            }
            Render2D.text(ctx, font, option.label, bx + 5, y + 5f, 0.75f, active ? Theme.TEXT : Theme.TEXT_DIM);
            bx += w + 3;
        }
        return bx;
    }

    private void drawActionButtons(DrawContext ctx, TextRenderer font, float x, float y, int mouseX, int mouseY) {
        float bx = x;
        String[] labels = {"Open", "Close", "Reset"};
        for (String label : labels) {
            float w = Render2D.textWidth(font, label, 0.72f) + 9;
            boolean hover = mouseX >= bx && mouseX <= bx + w && mouseY >= y && mouseY <= y + 16;
            Render2D.roundedRect(ctx, bx, y, w, 16, 2, hover ? Theme.HEADER : Theme.PANEL_INNER);
            Render2D.text(ctx, font, label, bx + 4.5f, y + 5f, 0.72f, hover ? Theme.TEXT : Theme.TEXT_DIM);
            bx += w + 3;
        }
    }

    private String fitSearch(TextRenderer font, String value, int maxWidth) {
        String text = value;
        while (!text.isEmpty() && font.getWidth(text) > maxWidth) {
            text = text.substring(1);
        }
        return text;
    }

    private int countMatches() {
        String query = search.trim().toLowerCase();
        int count = 0;
        for (Module module : ModuleManager.getModules()) {
            if (matchesFilter(module)
                    && (module.getName().toLowerCase().contains(query)
                    || module.getDescription().toLowerCase().contains(query)
                    || module.getCategory().title.toLowerCase().contains(query))) {
                count++;
            }
        }
        return count;
    }

    private boolean matchesFilter(Module module) {
        return switch (filter) {
            case ENABLED -> module.isEnabled();
            case FAVORITES -> module.isFavorite();
            case ALL -> true;
        };
    }

    private int favoriteCount() {
        int count = 0;
        for (Module module : ModuleManager.getModules()) {
            if (module.isFavorite()) {
                count++;
            }
        }
        return count;
    }

    private void drawTooltip(DrawContext ctx, TextRenderer font, String text, int mouseX, int mouseY) {
        float scale = 0.85f;
        float w = Render2D.textWidth(font, text, scale);
        float x = mouseX + 11;
        float y = mouseY + 9;
        Render2D.roundedRect(ctx, x - 5, y - 4, w + 10, 14, 4, Theme.TOOLTIP_BG);
        Render2D.roundedRect(ctx, x - 5, y - 4, 2.5f, 14, 1.5f, Theme.ACCENT);
        Render2D.text(ctx, font, text, x, y, scale, Theme.TEXT);
    }

    private void drawInspector(DrawContext ctx, TextRenderer font, Module module) {
        float x = 8;
        float y = this.height - 24;
        float w = Math.min(this.width - 16, 330);
        Render2D.roundedRect(ctx, x, y, w, 16, 2, Theme.HEADER);
        Render2D.rect(ctx, x, y, 3, 16, module.isEnabled() ? Theme.ACCENT : Theme.ACCENT_2);

        String key = module.getKey() >= 0 ? ModuleButton.keyName(module.getKey()) : "-";
        String text = module.getName() + "  [" + module.getCategory().title + "]  "
                + (module.isEnabled() ? "On" : "Off") + "  Key " + key
                + "  Settings " + module.getSettings().size();
        Render2D.text(ctx, font, fitSearch(font, text, (int) w - 12), x + 8, y + 5f, 0.75f, Theme.TEXT);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && clickFilter(mouseX, mouseY)) {
            return true;
        }
        if (button == 0 && clickAction(mouseX, mouseY)) {
            return true;
        }
        for (Panel panel : panels) {
            Module hovered = panel.getHovered((int) mouseX, (int) mouseY, search, filter);
            if (button == 2 && hovered != null) {
                bindingModule = hovered;
                return true;
            }
            if (panel.mouseClicked(mouseX, mouseY, button, search, filter)) {
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean clickFilter(double mouseX, double mouseY) {
        TextRenderer font = this.textRenderer;
        String title = HorrorClient.NAME;
        float tw = Render2D.textWidth(font, title, 1.1f);
        float bx = 8 + tw + 82;
        for (ModuleFilter option : ModuleFilter.values()) {
            float w = Render2D.textWidth(font, option.label, 0.75f) + 10;
            if (mouseX >= bx && mouseX <= bx + w && mouseY >= 6 && mouseY <= 22) {
                filter = option;
                return true;
            }
            bx += w + 3;
        }
        return false;
    }

    private boolean clickAction(double mouseX, double mouseY) {
        TextRenderer font = this.textRenderer;
        String title = HorrorClient.NAME;
        float tw = Render2D.textWidth(font, title, 1.1f);
        float bx = 8 + tw + 82;
        for (ModuleFilter option : ModuleFilter.values()) {
            bx += Render2D.textWidth(font, option.label, 0.75f) + 13;
        }
        bx += 6;

        String[] labels = {"Open", "Close", "Reset"};
        for (String label : labels) {
            float w = Render2D.textWidth(font, label, 0.72f) + 9;
            if (mouseX >= bx && mouseX <= bx + w && mouseY >= 6 && mouseY <= 22) {
                runAction(label);
                return true;
            }
            bx += w + 3;
        }
        return false;
    }

    private void runAction(String label) {
        if ("Open".equals(label)) {
            for (Panel panel : panels) {
                panel.setExpandedState(true);
            }
        } else if ("Close".equals(label)) {
            for (Panel panel : panels) {
                panel.setExpandedState(false);
            }
        } else if ("Reset".equals(label)) {
            resetPanels();
        }
        ConfigManager.save();
    }

    private void resetPanels() {
        int startX = 12;
        int startY = 30;
        int spacingX = Panel.WIDTH + 8;
        int spacingY = 154;
        int columns = Math.max(2, Math.min(4, (this.width - 24) / spacingX));
        for (int i = 0; i < panels.size(); i++) {
            int col = i % columns;
            int row = i / columns;
            panels.get(i).resetLayout(startX + col * spacingX, startY + row * spacingY);
            panels.get(i).setExpandedState(false); // clean, collapsed view after a reset
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        for (Panel panel : panels) {
            panel.mouseDragged(mouseX, mouseY);
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (Panel panel : panels) {
            panel.mouseReleased(button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        for (Panel panel : panels) {
            if (panel.mouseScrolled(mouseX, mouseY, amount, search, filter, this.height)) {
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (!Character.isISOControl(chr)) {
            search += chr;
            return true;
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (bindingModule != null) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                bindingModule = null;
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_BACKSPACE || keyCode == GLFW.GLFW_KEY_DELETE) {
                bindingModule.setKey(-1);
            } else {
                bindingModule.setKey(keyCode);
            }
            ConfigManager.save();
            bindingModule = null;
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE && !search.isEmpty()) {
            search = search.substring(0, search.length() - 1);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE && !search.isEmpty()) {
            search = "";
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
