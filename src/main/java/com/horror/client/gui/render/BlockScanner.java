package com.horror.client.gui.render;

import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Throttled, cached block search shared by the ESP scanner modules.
 *
 * <p>Scanning a large radius every render frame is catastrophic for the frame
 * rate (radius 32 = ~275k block checks per frame; radius 96 = over 7 million).
 * This helper only rescans when the player moves a block, the radius changes, or
 * a short interval elapses, and otherwise returns the cached result, so
 * rendering can run at full frame rate.
 */
public final class BlockScanner {
    private static final long INTERVAL_MS = 400;
    private static final int MAX_RESULTS = 4000;

    private final List<BlockPos> found = new ArrayList<>();
    private long lastScan;
    private BlockPos lastCenter;
    private int lastHRadius = -1;
    private int lastVRadius = -1;

    public List<BlockPos> scan(BlockPos center, int hRadius, int vRadius, Predicate<BlockPos> match) {
        long now = System.currentTimeMillis();
        if (now - lastScan < INTERVAL_MS && center.equals(lastCenter)
                && hRadius == lastHRadius && vRadius == lastVRadius) {
            return found;
        }
        lastScan = now;
        lastCenter = center;
        lastHRadius = hRadius;
        lastVRadius = vRadius;

        found.clear();
        for (BlockPos pos : BlockPos.iterate(center.add(-hRadius, -vRadius, -hRadius),
                center.add(hRadius, vRadius, hRadius))) {
            if (match.test(pos)) {
                found.add(pos.toImmutable());
                if (found.size() >= MAX_RESULTS) {
                    break;
                }
            }
        }
        return found;
    }
}
