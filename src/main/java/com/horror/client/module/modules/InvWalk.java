package com.horror.client.module.modules;

import com.horror.client.gui.ClickGui;
import com.horror.client.mixin.KeyBindingAccessor;
import com.horror.client.module.Category;
import com.horror.client.module.Module;
import com.horror.client.module.setting.BooleanSetting;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

/** Allows normal movement while inventory-like screens are open. */
public class InvWalk extends Module {
    private final BooleanSetting sneak = new BooleanSetting("Sneak", false);

    public InvWalk() {
        super("InvWalk", "Move while inventory screens are open.", Category.MOVEMENT);
        addSettings(sneak);
    }

    @Override
    public void onTick() {
        if (mc.currentScreen == null || mc.currentScreen instanceof ChatScreen || mc.currentScreen instanceof ClickGui) {
            return;
        }

        setPressed(mc.options.forwardKey);
        setPressed(mc.options.backKey);
        setPressed(mc.options.leftKey);
        setPressed(mc.options.rightKey);
        setPressed(mc.options.jumpKey);
        if (sneak.get() || !(mc.currentScreen instanceof HandledScreen<?>)) {
            setPressed(mc.options.sneakKey);
        }
    }

    @Override
    public void onDisable() {
        if (mc.options == null) {
            return;
        }
        mc.options.forwardKey.setPressed(false);
        mc.options.backKey.setPressed(false);
        mc.options.leftKey.setPressed(false);
        mc.options.rightKey.setPressed(false);
        mc.options.jumpKey.setPressed(false);
        mc.options.sneakKey.setPressed(false);
    }

    private void setPressed(KeyBinding keyBinding) {
        InputUtil.Key key = ((KeyBindingAccessor) keyBinding).getBoundKey();
        keyBinding.setPressed(InputUtil.isKeyPressed(mc.getWindow().getHandle(), key.getCode()));
    }
}
