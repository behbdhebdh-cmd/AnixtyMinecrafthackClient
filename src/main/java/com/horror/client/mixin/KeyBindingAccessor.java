package com.horror.client.mixin;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Exposes the current keybinding so InvWalk can mirror physical key state. */
@Mixin(KeyBinding.class)
public interface KeyBindingAccessor {
    @Accessor("boundKey")
    InputUtil.Key getBoundKey();
}
