package com.horror.client.util;

import net.minecraft.client.MinecraftClient;

/** Keeps trainer logic scoped to Minecraft's integrated singleplayer server. */
public final class SingleplayerGuard {
    private SingleplayerGuard() {}

    public static boolean canRun() {
        MinecraftClient mc = MinecraftClient.getInstance();
        return mc.world != null && mc.player != null && mc.isInSingleplayer();
    }
}
