package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;

/**
 * Prevents fall damage. Resets the client fall distance every tick, while
 * {@code PlayerMoveC2SPacketMixin} forces {@code onGround = true} on outgoing
 * movement packets so the server never registers a fall.
 */
public class NoFall extends Module {
    public NoFall() {
        super("NoFall", "Prevents fall damage.", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        if (mc.player != null) {
            mc.player.fallDistance = 0;
        }
    }
}
