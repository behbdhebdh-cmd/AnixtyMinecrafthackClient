package com.horror.client.module.modules;

import com.horror.client.gui.render.Render3D;
import com.horror.client.module.Category;
import com.horror.client.module.Module;
import com.horror.client.module.setting.ModeSetting;
import com.horror.client.module.setting.NumberSetting;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.List;

/** Draws boxes around dropped items. */
public class ItemESP extends Module {
    private final ModeSetting style = new ModeSetting("Style", "Boxes", "Boxes", "Lines", "Both");
    private final ModeSetting boxSize = new ModeSetting("Box", "Fancy", "Fancy", "Accurate");
    private final NumberSetting range = new NumberSetting("Range", 64, 8, 192, 4);
    private final NumberSetting maxItems = new NumberSetting("MaxItems", 192, 16, 1024, 16);
    private final NumberSetting updateMs = new NumberSetting("UpdateMs", 250, 50, 1500, 50);

    private final List<ItemEntity> items = new ArrayList<>();
    private long lastUpdate;

    public ItemESP() {
        super("ItemESP", "Highlights dropped items.", Category.RENDER);
        addSettings(style, boxSize, range, maxItems, updateMs);
    }

    @Override
    public void onRender3D(WorldRenderContext context) {
        if (mc.world == null || mc.player == null) {
            return;
        }
        updateItems();
        boolean drawBoxes = !style.is("Lines");
        boolean drawLines = !style.is("Boxes");
        float tickDelta = context.tickDelta();

        for (ItemEntity item : items) {
            if (item.isRemoved()) {
                continue;
            }
            Box box = boxFor(item, tickDelta);
            if (drawBoxes) {
                Render3D.drawBox(context, box, 1.0f, 0.86f, 0.18f, 0.82f);
            }
            if (drawLines) {
                Render3D.drawTracer(context, box.getCenter(), 1.0f, 0.86f, 0.18f, 0.72f);
            }
        }
    }

    private void updateItems() {
        long now = System.currentTimeMillis();
        if (now - lastUpdate < updateMs.getInt()) {
            return;
        }
        lastUpdate = now;
        items.clear();

        double maxDistanceSq = range.get() * range.get();
        int limit = maxItems.getInt();
        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof ItemEntity item)) {
                continue;
            }
            if (entity.squaredDistanceTo(mc.player) > maxDistanceSq) {
                continue;
            }
            items.add(item);
            if (items.size() >= limit) {
                break;
            }
        }
    }

    private Box boxFor(ItemEntity item, float tickDelta) {
        return Render3D.lerpedBox(item, tickDelta, boxSize.is("Fancy") ? 0.06 : 0);
    }

    @Override
    public void onDisable() {
        items.clear();
        lastUpdate = 0;
    }
}
