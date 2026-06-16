package com.horror.client.gui.widget;

import com.horror.client.config.ConfigManager;
import com.horror.client.gui.ModuleFilter;
import com.horror.client.gui.Theme;
import com.horror.client.gui.render.Render2D;
import com.horror.client.module.Category;
import com.horror.client.module.Module;
import com.horror.client.module.ModuleManager;
import com.horror.client.module.setting.BooleanSetting;
import com.horror.client.module.setting.ModeSetting;
import com.horror.client.module.setting.NumberSetting;
import com.horror.client.module.setting.Setting;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * A draggable category panel with rounded glass styling and animated,
 * collapsible per-module settings.
 *
 * <p>Left-drag header to move, right-click header to collapse the panel,
 * left-click a module to toggle, right-click a module to reveal its settings.
 */
public class Panel {
    public static final int WIDTH = 128;
    public static final int HEADER_HEIGHT = 15;
    private static final int INSET = 9;
    private static final float TEXT_SCALE = 0.85f;

    private final Category category;
    private final List<ModuleButton> buttons = new ArrayList<>();

    private int x;
    private int y;
    private boolean expanded = true;
    private boolean pinned;

    private boolean dragging;
    private int dragOffsetX;
    private int dragOffsetY;
    private NumberSetting draggingSlider;
    private int scrollOffset;
    private int shownInnerHeight;

    public Panel(Category category, int x, int y) {
        this.category = category;
        ConfigManager.PanelState state = ConfigManager.getPanelState(category);
        this.x = state != null ? state.x() : x;
        this.y = state != null ? state.y() : y;
        // Start collapsed by default so the GUI opens as a clean list of category
        // headers instead of a wall of ~80 modules; expanded state still persists.
        this.expanded = state != null && state.expanded();
        this.pinned = state != null && state.pinned();
        for (Module module : ModuleManager.getByCategory(category)) {
            buttons.add(new ModuleButton(module));
        }
        sortButtons();
    }

    // ---- Rendering ----
    public void render(DrawContext ctx, TextRenderer font, int mouseX, int mouseY,
                       String search, ModuleFilter filter, int screenHeight) {
        List<ModuleButton> visible = visibleButtons(search, filter);

        // Advance expand animations once and cache per-module pixel heights.
        double[] settingsPx = new double[visible.size()];
        int innerHeight = 0;
        for (int i = 0; i < visible.size(); i++) {
            Module module = visible.get(i).getModule();
            double progress = visible.get(i).expandProgress();
            settingsPx[i] = expanded ? fullSettingsHeight(module) * progress : 0;
            if (expanded) {
                innerHeight += ModuleButton.HEIGHT + (int) Math.round(settingsPx[i]);
            }
        }

        int maxInnerHeight = Math.max(36, screenHeight - y - HEADER_HEIGHT - 24);
        clampScroll(innerHeight, maxInnerHeight);
        shownInnerHeight = expanded ? Math.min(innerHeight, maxInnerHeight) : 0;
        int contentHeight = HEADER_HEIGHT + shownInnerHeight;

        Render2D.roundedRect(ctx, x + 2, y + 3, WIDTH, contentHeight, 2, Theme.SHADOW);
        Render2D.roundedRect(ctx, x, y, WIDTH, contentHeight, 2, Theme.BORDER);
        Render2D.roundedRect(ctx, x + 1, y + 1, WIDTH - 2, contentHeight - 2, 1, Theme.PANEL);

        Render2D.rect(ctx, x + 1, y + 1, WIDTH - 2, HEADER_HEIGHT - 1, Theme.HEADER);
        Render2D.rect(ctx, x + 1, y + HEADER_HEIGHT - 1, WIDTH - 2, 1, Theme.HEADER_EDGE);
        Render2D.text(ctx, font, category.title, x + 6, y + 4f, 0.8f, Theme.TEXT);
        String count = activeCount(visible) + "/" + visible.size();
        Render2D.text(ctx, font, count, x + WIDTH - 40, y + 4f, 0.75f, Theme.TEXT_DIM);
        drawHeaderButtons(ctx, mouseX, mouseY);
        if (!expanded) {
            return;
        }

        int bodyTop = y + HEADER_HEIGHT;
        ctx.enableScissor(x, bodyTop, x + WIDTH, bodyTop + shownInnerHeight);
        int rowY = bodyTop + scrollOffset;
        for (int i = 0; i < visible.size(); i++) {
            ModuleButton btn = visible.get(i);
            btn.render(ctx, font, x, rowY, WIDTH, mouseX, mouseY);
            rowY += ModuleButton.HEIGHT;

            int areaH = (int) Math.round(settingsPx[i]);
            if (areaH > 0) {
                int sy = rowY;
                for (Setting setting : btn.getModule().getSettings()) {
                    renderSetting(ctx, font, setting, sy, mouseX);
                    sy += settingHeight(setting);
                }
                rowY += areaH;
            }
        }
        ctx.disableScissor();

        if (innerHeight > shownInnerHeight) {
            float trackH = shownInnerHeight - 4;
            float thumbH = Math.max(10, trackH * shownInnerHeight / (float) innerHeight);
            float thumbY = bodyTop + 2 + (-scrollOffset / (float) (innerHeight - shownInnerHeight)) * (trackH - thumbH);
            Render2D.rect(ctx, x + WIDTH - 4, bodyTop + 2, 2, trackH, Theme.TRACK);
            Render2D.rect(ctx, x + WIDTH - 4, thumbY, 2, thumbH, Theme.ACCENT);
        }
    }

    private void drawHeaderButtons(DrawContext ctx, int mouseX, int mouseY) {
        int minX = x + WIDTH - 13;
        int pinX = x + WIDTH - 25;
        boolean minHover = mouseX >= minX && mouseX <= minX + 10 && mouseY >= y + 2 && mouseY <= y + 12;
        boolean pinHover = mouseX >= pinX && mouseX <= pinX + 10 && mouseY >= y + 2 && mouseY <= y + 12;

        Render2D.rect(ctx, pinX, y + 3, 8, 8, pinned ? Theme.ACCENT : Theme.TOGGLE_OFF);
        Render2D.rect(ctx, pinX + 3, y + 10, 2, 3, pinHover ? Theme.TEXT : Theme.TEXT_DIM);

        int arrowColor = minHover ? Theme.TEXT : expanded ? Theme.ACCENT_2 : Theme.ACCENT;
        if (expanded) {
            Render2D.rect(ctx, minX + 2, y + 7, 7, 2, arrowColor);
        } else {
            Render2D.rect(ctx, minX + 2, y + 5, 7, 2, arrowColor);
            Render2D.rect(ctx, minX + 4, y + 3, 2, 6, arrowColor);
        }
    }

    private void renderSetting(DrawContext ctx, TextRenderer font, Setting setting, int sy, int mouseX) {
        Render2D.roundedRect(ctx, x + 5, sy, WIDTH - 10, settingHeight(setting) - 1, 3, Theme.PANEL_INNER);

        if (setting instanceof NumberSetting number) {
            Render2D.text(ctx, font, setting.getName(), x + INSET, sy + 3.5f, TEXT_SCALE, Theme.TEXT_DIM);
            String value = format(number);
            Render2D.text(ctx, font, value, x + WIDTH - INSET - Render2D.textWidth(font, value, TEXT_SCALE),
                    sy + 3.5f, TEXT_SCALE, Theme.TEXT);

            float trackLeft = x + INSET;
            float trackW = WIDTH - INSET * 2f;
            float trackY = sy + 14f;
            Render2D.roundedRect(ctx, trackLeft, trackY, trackW, 3, 1.5f, Theme.TRACK);
            float fill = (float) (trackW * number.getFraction());
            Render2D.roundedRect(ctx, trackLeft, trackY, fill, 3, 1.5f, Theme.ACCENT);
            float knob = 7;
            Render2D.roundedRect(ctx, trackLeft + fill - knob / 2f, trackY + 1.5f - knob / 2f, knob, knob,
                    knob / 2f, Theme.KNOB);
        } else if (setting instanceof BooleanSetting bool) {
            Render2D.text(ctx, font, setting.getName(), x + INSET, sy + 4f, TEXT_SCALE, Theme.TEXT_DIM);
            float pw = 16, ph = 8;
            float px = x + WIDTH - INSET - pw, py = sy + (settingHeight(setting) - ph) / 2f;
            Render2D.roundedRect(ctx, px, py, pw, ph, ph / 2f,
                    bool.get() ? Theme.ACCENT : Theme.TOGGLE_OFF);
            float knob = ph - 3;
            float kx = bool.get() ? px + pw - knob - 1.5f : px + 1.5f;
            Render2D.roundedRect(ctx, kx, py + 1.5f, knob, knob, knob / 2f, Theme.KNOB);
        } else if (setting instanceof ModeSetting mode) {
            Render2D.text(ctx, font, setting.getName(), x + INSET, sy + 4f, TEXT_SCALE, Theme.TEXT_DIM);
            String value = mode.get();
            float vw = Render2D.textWidth(font, value, TEXT_SCALE);
            Render2D.roundedRect(ctx, x + WIDTH - INSET - vw - 8, sy + 2.5f, vw + 8,
                    settingHeight(setting) - 5, 3, Theme.ACCENT_SOFT);
            Render2D.text(ctx, font, value, x + WIDTH - INSET - vw - 4, sy + 4f, TEXT_SCALE, Theme.TEXT);
        }
    }

    // ---- Interaction ----
    public boolean mouseClicked(double mouseX, double mouseY, int button, String search, ModuleFilter filter) {
        if (isOverHeader(mouseX, mouseY)) {
            if (mouseX >= x + WIDTH - 13 && mouseX <= x + WIDTH - 3) {
                expanded = !expanded;
                saveState();
            } else if (mouseX >= x + WIDTH - 25 && mouseX <= x + WIDTH - 15) {
                pinned = !pinned;
                saveState();
            } else if (button == 0 && !pinned) {
                dragging = true;
                dragOffsetX = (int) mouseX - x;
                dragOffsetY = (int) mouseY - y;
            } else if (button == 1) {
                expanded = !expanded;
                saveState();
            }
            return true;
        }
        if (!expanded) {
            return false;
        }

        if (!isOverBody(mouseX, mouseY)) {
            return false;
        }

        int rowY = y + HEADER_HEIGHT + scrollOffset;
        for (ModuleButton btn : visibleButtons(search, filter)) {
            Module module = btn.getModule();
            if (btn.isHovered(x, rowY, WIDTH, (int) mouseX, (int) mouseY)) {
                if (button == 0) {
                    if (Screen.hasControlDown()) {
                        module.toggleFavorite();
                        sortButtons();
                        ConfigManager.save();
                    } else {
                        module.toggle();
                    }
                } else if (button == 1 && module.hasSettings()) {
                    module.setExpanded(!module.isExpanded());
                    ConfigManager.save();
                }
                return true;
            }
            rowY += ModuleButton.HEIGHT;

            if (module.isExpanded()) {
                for (Setting setting : module.getSettings()) {
                    int height = settingHeight(setting);
                    if (mouseY >= rowY && mouseY <= rowY + height && mouseX >= x && mouseX <= x + WIDTH) {
                        handleSettingClick(setting, mouseX);
                        ConfigManager.save();
                        return true;
                    }
                    rowY += height;
                }
            }
        }
        return false;
    }

    private void handleSettingClick(Setting setting, double mouseX) {
        if (setting instanceof NumberSetting number) {
            draggingSlider = number;
            updateSlider(mouseX);
        } else if (setting instanceof BooleanSetting bool) {
            bool.toggle();
        } else if (setting instanceof ModeSetting mode) {
            mode.cycle();
        }
    }

    public void mouseReleased(int button) {
        if (button == 0) {
            boolean shouldSave = dragging || draggingSlider != null;
            dragging = false;
            draggingSlider = null;
            if (shouldSave) {
                saveState();
            }
        }
    }

    public void mouseDragged(double mouseX, double mouseY) {
        if (dragging) {
            x = (int) mouseX - dragOffsetX;
            y = (int) mouseY - dragOffsetY;
        } else if (draggingSlider != null) {
            updateSlider(mouseX);
        }
    }

    private void updateSlider(double mouseX) {
        float trackLeft = x + INSET;
        float trackW = WIDTH - INSET * 2f;
        draggingSlider.setFromFraction((mouseX - trackLeft) / trackW);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double amount, String search,
                                 ModuleFilter filter, int screenHeight) {
        if (!expanded || !isOverPanel(mouseX, mouseY, screenHeight)) {
            return false;
        }
        int innerHeight = visibleInnerHeight(search, filter);
        int maxInnerHeight = Math.max(36, screenHeight - y - HEADER_HEIGHT - 24);
        if (innerHeight <= maxInnerHeight) {
            return false;
        }
        scrollOffset += (int) (amount * 14);
        clampScroll(innerHeight, maxInnerHeight);
        return true;
    }

    public void setExpandedState(boolean expanded) {
        this.expanded = expanded;
        saveState();
    }

    public void resetLayout(int x, int y) {
        this.x = x;
        this.y = y;
        expanded = true;
        pinned = false;
        scrollOffset = 0;
        saveState();
    }

    public Module getHovered(int mouseX, int mouseY, String search, ModuleFilter filter) {
        if (!expanded || !isOverBody(mouseX, mouseY)) {
            return null;
        }
        int rowY = y + HEADER_HEIGHT + scrollOffset;
        for (ModuleButton btn : visibleButtons(search, filter)) {
            Module module = btn.getModule();
            if (btn.isHovered(x, rowY, WIDTH, mouseX, mouseY)) {
                return module;
            }
            rowY += ModuleButton.HEIGHT;
            if (module.isExpanded()) {
                for (Setting setting : module.getSettings()) {
                    rowY += settingHeight(setting);
                }
            }
        }
        return null;
    }

    private int fullSettingsHeight(Module module) {
        int total = 0;
        for (Setting setting : module.getSettings()) {
            total += settingHeight(setting);
        }
        return total;
    }

    private int visibleInnerHeight(String search, ModuleFilter filter) {
        int total = 0;
        for (ModuleButton button : visibleButtons(search, filter)) {
            Module module = button.getModule();
            total += ModuleButton.HEIGHT;
            if (module.isExpanded()) {
                total += fullSettingsHeight(module);
            }
        }
        return total;
    }

    private List<ModuleButton> visibleButtons(String search, ModuleFilter filter) {
        String query = search == null ? "" : search.trim().toLowerCase(Locale.ROOT);
        if (query.isEmpty() && filter == ModuleFilter.ALL) {
            return buttons;
        }

        List<ModuleButton> result = new ArrayList<>();
        for (ModuleButton button : buttons) {
            Module module = button.getModule();
            boolean matchesQuery = query.isEmpty()
                    || module.getName().toLowerCase(Locale.ROOT).contains(query)
                    || module.getDescription().toLowerCase(Locale.ROOT).contains(query)
                    || module.getCategory().title.toLowerCase(Locale.ROOT).contains(query);
            if (matchesFilter(module, filter) && matchesQuery) {
                result.add(button);
            }
        }
        return result;
    }

    private boolean matchesFilter(Module module, ModuleFilter filter) {
        return switch (filter) {
            case ENABLED -> module.isEnabled();
            case FAVORITES -> module.isFavorite();
            case ALL -> true;
        };
    }

    private void sortButtons() {
        buttons.sort(Comparator
                .comparing((ModuleButton button) -> !button.getModule().isFavorite())
                .thenComparing(button -> button.getModule().getName()));
    }

    private int activeCount(List<ModuleButton> visible) {
        int active = 0;
        for (ModuleButton button : visible) {
            if (button.getModule().isEnabled()) {
                active++;
            }
        }
        return active;
    }

    private void clampScroll(int innerHeight, int maxInnerHeight) {
        if (innerHeight <= maxInnerHeight) {
            scrollOffset = 0;
            return;
        }
        int min = maxInnerHeight - innerHeight;
        if (scrollOffset < min) {
            scrollOffset = min;
        }
        if (scrollOffset > 0) {
            scrollOffset = 0;
        }
    }

    private int settingHeight(Setting setting) {
        return setting instanceof NumberSetting ? 21 : 15;
    }

    private String format(NumberSetting number) {
        double value = number.get();
        return value == Math.rint(value) ? String.valueOf((int) value) : String.format("%.1f", value);
    }

    private boolean isOverHeader(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + WIDTH && mouseY >= y && mouseY <= y + HEADER_HEIGHT;
    }

    private boolean isOverBody(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + WIDTH
                && mouseY >= y + HEADER_HEIGHT && mouseY <= y + HEADER_HEIGHT + shownInnerHeight;
    }

    private boolean isOverPanel(double mouseX, double mouseY, int screenHeight) {
        int maxInnerHeight = Math.max(36, screenHeight - y - HEADER_HEIGHT - 24);
        int panelHeight = HEADER_HEIGHT + maxInnerHeight;
        return mouseX >= x && mouseX <= x + WIDTH && mouseY >= y && mouseY <= y + panelHeight;
    }

    private void saveState() {
        ConfigManager.setPanelState(category, x, y, expanded, pinned);
    }
}
