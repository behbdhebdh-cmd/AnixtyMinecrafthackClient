package com.horror.client.mixin;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Exposes the step height field so the Step module can change it. */
@Mixin(Entity.class)
public interface EntityAccessor {
    @Accessor("stepHeight")
    void setStepHeight(float stepHeight);
}
