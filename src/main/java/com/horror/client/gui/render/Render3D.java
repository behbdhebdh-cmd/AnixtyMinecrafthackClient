package com.horror.client.gui.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

/**
 * World-space ESP rendering for boxes, outlines and tracers.
 *
 * <p>Everything is appended to two shared buffers (lines + quads) and drawn ONCE
 * per frame in {@link #flush} with the depth test disabled, so ESP shows through
 * walls and the whole frame costs a single draw call per buffer instead of one
 * per object. This is the Wurst-style approach adapted to 1.20.1.
 */
public final class Render3D {
    private static final BufferBuilder LINES = new BufferBuilder(0x100000);
    private static final BufferBuilder QUADS = new BufferBuilder(0x100000);
    private static boolean buildingLines;
    private static boolean buildingQuads;

    private Render3D() {}

    public static Box lerpedBox(Entity entity, float tickDelta, double extraSize) {
        Vec3d offset = entity.getLerpedPos(tickDelta).subtract(entity.getPos());
        Box box = entity.getBoundingBox().offset(offset);
        return extraSize > 0 ? box.offset(0, extraSize, 0).expand(extraSize) : box;
    }

    public static void drawBox(WorldRenderContext ctx, Box box, float r, float g, float b, float a) {
        Matrix4f m = ctx.matrixStack().peek().getPositionMatrix();
        Vec3d cam = ctx.camera().getPos();
        beginLines();
        float x1 = (float) (box.minX - cam.x), y1 = (float) (box.minY - cam.y), z1 = (float) (box.minZ - cam.z);
        float x2 = (float) (box.maxX - cam.x), y2 = (float) (box.maxY - cam.y), z2 = (float) (box.maxZ - cam.z);

        line(m, x1, y1, z1, x2, y1, z1, r, g, b, a);
        line(m, x2, y1, z1, x2, y1, z2, r, g, b, a);
        line(m, x2, y1, z2, x1, y1, z2, r, g, b, a);
        line(m, x1, y1, z2, x1, y1, z1, r, g, b, a);

        line(m, x1, y2, z1, x2, y2, z1, r, g, b, a);
        line(m, x2, y2, z1, x2, y2, z2, r, g, b, a);
        line(m, x2, y2, z2, x1, y2, z2, r, g, b, a);
        line(m, x1, y2, z2, x1, y2, z1, r, g, b, a);

        line(m, x1, y1, z1, x1, y2, z1, r, g, b, a);
        line(m, x2, y1, z1, x2, y2, z1, r, g, b, a);
        line(m, x2, y1, z2, x2, y2, z2, r, g, b, a);
        line(m, x1, y1, z2, x1, y2, z2, r, g, b, a);
    }

    public static void drawSolidBox(WorldRenderContext ctx, Box box, float r, float g, float b, float a) {
        Matrix4f m = ctx.matrixStack().peek().getPositionMatrix();
        Vec3d cam = ctx.camera().getPos();
        beginQuads();
        float x1 = (float) (box.minX - cam.x), y1 = (float) (box.minY - cam.y), z1 = (float) (box.minZ - cam.z);
        float x2 = (float) (box.maxX - cam.x), y2 = (float) (box.maxY - cam.y), z2 = (float) (box.maxZ - cam.z);

        quad(m, r, g, b, a, x1, y1, z1, x2, y1, z1, x2, y1, z2, x1, y1, z2);
        quad(m, r, g, b, a, x1, y2, z1, x1, y2, z2, x2, y2, z2, x2, y2, z1);
        quad(m, r, g, b, a, x1, y1, z1, x1, y2, z1, x2, y2, z1, x2, y1, z1);
        quad(m, r, g, b, a, x2, y1, z1, x2, y2, z1, x2, y2, z2, x2, y1, z2);
        quad(m, r, g, b, a, x1, y1, z2, x2, y1, z2, x2, y2, z2, x1, y2, z2);
        quad(m, r, g, b, a, x1, y1, z1, x1, y1, z2, x1, y2, z2, x1, y2, z1);
    }

    public static void drawTracer(WorldRenderContext ctx, Vec3d targetCenter, float r, float g, float b, float a) {
        Matrix4f m = ctx.matrixStack().peek().getPositionMatrix();
        Vec3d cam = ctx.camera().getPos();
        beginLines();
        Vec3d start = Vec3d.fromPolar(ctx.camera().getPitch(), ctx.camera().getYaw());
        Vec3d end = targetCenter.subtract(cam);
        line(m, (float) start.x, (float) start.y, (float) start.z,
                (float) end.x, (float) end.y, (float) end.z, r, g, b, a);
    }

    /** Draw and reset everything accumulated this frame. Call once after all 3D rendering. */
    public static void flush(WorldRenderContext ctx) {
        if (!buildingLines && !buildingQuads) {
            return;
        }
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();   // through walls
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        if (buildingQuads) {
            BufferRenderer.drawWithGlobalProgram(QUADS.end());
            buildingQuads = false;
        }
        if (buildingLines) {
            RenderSystem.lineWidth(1.5f);
            BufferRenderer.drawWithGlobalProgram(LINES.end());
            buildingLines = false;
        }

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    private static void beginLines() {
        if (!buildingLines) {
            LINES.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
            buildingLines = true;
        }
    }

    private static void beginQuads() {
        if (!buildingQuads) {
            QUADS.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            buildingQuads = true;
        }
    }

    private static void line(Matrix4f m, float x1, float y1, float z1, float x2, float y2, float z2,
                             float r, float g, float b, float a) {
        LINES.vertex(m, x1, y1, z1).color(r, g, b, a).next();
        LINES.vertex(m, x2, y2, z2).color(r, g, b, a).next();
    }

    private static void quad(Matrix4f m, float r, float g, float b, float a,
                             float x1, float y1, float z1, float x2, float y2, float z2,
                             float x3, float y3, float z3, float x4, float y4, float z4) {
        QUADS.vertex(m, x1, y1, z1).color(r, g, b, a).next();
        QUADS.vertex(m, x2, y2, z2).color(r, g, b, a).next();
        QUADS.vertex(m, x3, y3, z3).color(r, g, b, a).next();
        QUADS.vertex(m, x4, y4, z4).color(r, g, b, a).next();
    }
}
