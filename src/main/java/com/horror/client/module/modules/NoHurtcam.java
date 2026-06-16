package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;

/** Prevents the camera from tilting when you take damage. */
public class NoHurtcam extends Module {
    public NoHurtcam() {
        super("NoHurtcam", "Disables damage camera tilt.", Category.RENDER);
    }
}
