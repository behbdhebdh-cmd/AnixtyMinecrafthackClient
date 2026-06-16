package com.horror.client.gui.render;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

/**
 * Modern 2D primitives for the ClickGUI, built on {@link DrawContext#fill} so
 * they always render reliably (unlike raw immediate-mode buffers, which don't
 * compose with DrawContext's deferred drawing). Rounded corners are produced
 * with per-row scanline fills.
 */
public final class Render2D {
    private Render2D() {}

    /** Global alpha multiplier (0..1) for smooth fade-in/out of the whole GUI. */
    private static float globalAlpha = 1f;

    public static void setAlpha(float alpha) {
        globalAlpha = Math.max(0f, Math.min(1f, alpha));
    }

    public static void resetAlpha() {
        globalAlpha = 1f;
    }

    private static int apply(int color) {
        if (globalAlpha >= 1f) {
            return color;
        }
        int a = (int) ((color >>> 24) * globalAlpha);
        return (a << 24) | (color & 0x00FFFFFF);
    }

    public static void roundedRect(DrawContext ctx, float fx, float fy, float fw, float fh,
                                   float fr, int color) {
        color = apply(color);
        int x = Math.round(fx);
        int y = Math.round(fy);
        int w = Math.round(fw);
        int h = Math.round(fh);
        if (w <= 0 || h <= 0) {
            return;
        }
        int r = Math.round(Math.min(fr, Math.min(w, h) / 2f));

        if (r <= 0) {
            ctx.fill(x, y, x + w, y + h, color);
            return;
        }

        // Straight middle band (full width).
        ctx.fill(x, y + r, x + w, y + h - r, color);

        // Rounded corner rows top and bottom.
        for (int i = 0; i < r; i++) {
            int off = r - i;
            int inset = r - (int) Math.floor(Math.sqrt((double) r * r - (double) off * off));
            ctx.fill(x + inset, y + i, x + w - inset, y + i + 1, color);
            ctx.fill(x + inset, y + h - 1 - i, x + w - inset, y + h - i, color);
        }
    }

    public static void rect(DrawContext ctx, float fx, float fy, float fw, float fh, int color) {
        color = apply(color);
        int x = Math.round(fx);
        int y = Math.round(fy);
        int w = Math.round(fw);
        int h = Math.round(fh);
        if (w > 0 && h > 0) {
            ctx.fill(x, y, x + w, y + h, color);
        }
    }

    /** Vertical two-colour rounded look (top tint over a solid body). */
    public static void roundedGradient(DrawContext ctx, float x, float y, float w, float h,
                                       float radius, int top, int bottom) {
        roundedRect(ctx, x, y, w, h, radius, bottom);
        roundedRect(ctx, x, y, w, h / 2f, radius, top);
    }

    public static void text(DrawContext ctx, TextRenderer font, String s, float x, float y,
                            float scale, int color) {
        MatrixStack ms = ctx.getMatrices();
        ms.push();
        ms.translate(x, y, 0);
        ms.scale(scale, scale, 1f);
        ctx.drawText(font, s, 0, 0, apply(color), false);
        ms.pop();
    }

    public static float textWidth(TextRenderer font, String s, float scale) {
        return font.getWidth(s) * scale;
    }

    /** Linear blend between two ARGB colours. */
    public static int lerpColor(int from, int to, float t) {
        t = Math.max(0f, Math.min(1f, t));
        int a = (int) ((from >> 24 & 0xFF) + ((to >> 24 & 0xFF) - (from >> 24 & 0xFF)) * t);
        int r = (int) ((from >> 16 & 0xFF) + ((to >> 16 & 0xFF) - (from >> 16 & 0xFF)) * t);
        int g = (int) ((from >> 8 & 0xFF) + ((to >> 8 & 0xFF) - (from >> 8 & 0xFF)) * t);
        int b = (int) ((from & 0xFF) + ((to & 0xFF) - (from & 0xFF)) * t);
        return a << 24 | r << 16 | g << 8 | b;
    }

    /** Replaces a colour's alpha (0..1). */
    public static int withAlpha(int color, float alpha) {
        int a = (int) (Math.max(0f, Math.min(1f, alpha)) * 255f);
        return a << 24 | (color & 0x00FFFFFF);
    }
}
