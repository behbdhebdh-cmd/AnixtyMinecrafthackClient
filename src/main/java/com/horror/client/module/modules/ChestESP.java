package com.horror.client.module.modules;

import com.horror.client.gui.render.Render3D;
import com.horror.client.module.Category;
import com.horror.client.module.Module;
import com.horror.client.module.setting.BooleanSetting;
import com.horror.client.module.setting.ModeSetting;
import com.horror.client.module.setting.NumberSetting;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.WorldChunk;

import java.util.ArrayList;
import java.util.List;

/** Highlights nearby storage blocks. */
public class ChestESP extends Module {
    private final NumberSetting radius = new NumberSetting("Radius", 24, 8, 64, 1);
    private final NumberSetting height = new NumberSetting("Height", 16, 4, 64, 1);
    private final ModeSetting style = new ModeSetting("Style", "Boxes", "Boxes", "Lines", "Both");
    private final BooleanSetting fill = new BooleanSetting("Fill", true);
    private final NumberSetting fillAlpha = new NumberSetting("FillAlpha", 0.25, 0.05, 0.6, 0.05);
    private final NumberSetting maxBoxes = new NumberSetting("MaxBoxes", 512, 32, 2048, 32);
    private final NumberSetting updateMs = new NumberSetting("UpdateMs", 500, 100, 2000, 50);

    private final List<BlockPos> storages = new ArrayList<>();
    private long lastUpdate;

    public ChestESP() {
        super("ChestESP", "Highlights chests and storage blocks.", Category.RENDER);
        addSettings(radius, height, style, fill, fillAlpha, maxBoxes, updateMs);
    }

    @Override
    public void onRender3D(WorldRenderContext context) {
        if (mc.world == null || mc.player == null) {
            return;
        }

        updateStorages();
        boolean drawBoxes = !style.is("Lines");
        boolean drawLines = !style.is("Boxes");

        for (BlockPos pos : storages) {
            BlockState state = mc.world.getBlockState(pos);
            float[] color = colorFor(state.getBlock());
            if (drawBoxes) {
                Box box = new Box(pos);
                if (fill.get()) {
                    Render3D.drawSolidBox(context, box, color[0], color[1], color[2], (float) fillAlpha.get());
                }
                Render3D.drawBox(context, box, color[0], color[1], color[2], 0.78f);
            }
            if (drawLines) {
                Render3D.drawTracer(context, Vec3d.ofCenter(pos), color[0], color[1], color[2], 0.72f);
            }
        }
    }

    private void updateStorages() {
        long now = System.currentTimeMillis();
        if (now - lastUpdate < updateMs.getInt()) {
            return;
        }
        lastUpdate = now;
        storages.clear();

        BlockPos center = mc.player.getBlockPos();
        int blockRadius = radius.getInt();
        int verticalRadius = height.getInt();
        int chunkRadius = Math.max(1, (blockRadius + 15) >> 4);
        int centerChunkX = center.getX() >> 4;
        int centerChunkZ = center.getZ() >> 4;
        int max = maxBoxes.getInt();
        double maxDistanceSq = blockRadius * blockRadius;

        for (int chunkX = centerChunkX - chunkRadius; chunkX <= centerChunkX + chunkRadius; chunkX++) {
            for (int chunkZ = centerChunkZ - chunkRadius; chunkZ <= centerChunkZ + chunkRadius; chunkZ++) {
                WorldChunk chunk = mc.world.getChunkManager().getWorldChunk(chunkX, chunkZ, false);
                if (chunk == null) {
                    continue;
                }
                for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                    BlockPos pos = blockEntity.getPos();
                    if (Math.abs(pos.getY() - center.getY()) > verticalRadius) {
                        continue;
                    }
                    if (pos.getSquaredDistance(center) > maxDistanceSq) {
                        continue;
                    }
                    BlockState state = mc.world.getBlockState(pos);
                    if (!isStorage(state.getBlock())) {
                        continue;
                    }
                    storages.add(pos.toImmutable());
                    if (storages.size() >= max) {
                        return;
                    }
                }
            }
        }
    }

    private boolean isStorage(Block block) {
        return block instanceof ChestBlock || block instanceof EnderChestBlock
                || block instanceof BarrelBlock || block instanceof ShulkerBoxBlock;
    }

    private float[] colorFor(Block block) {
        if (block instanceof EnderChestBlock) {
            return new float[]{0.62f, 0.22f, 1.0f};
        }
        if (block instanceof ShulkerBoxBlock) {
            return new float[]{0.95f, 0.28f, 0.82f};
        }
        if (block instanceof BarrelBlock) {
            return new float[]{0.74f, 0.46f, 0.22f};
        }
        return new float[]{1.0f, 0.64f, 0.08f};
    }

    @Override
    public void onDisable() {
        storages.clear();
        lastUpdate = 0;
    }
}
