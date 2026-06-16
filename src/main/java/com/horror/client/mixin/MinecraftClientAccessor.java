package com.horror.client.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Exposes the item-use cooldown so FastPlace can reset it. */
@Mixin(MinecraftClient.class)
public interface MinecraftClientAccessor {
    @Accessor("itemUseCooldown")
    void setItemUseCooldown(int cooldown);
}
