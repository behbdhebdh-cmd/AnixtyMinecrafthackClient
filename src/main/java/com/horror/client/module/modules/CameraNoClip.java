package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;

/** Prevents third-person camera distance from shrinking into blocks. */
public class CameraNoClip extends Module {
    public CameraNoClip() {
        super("CameraNoClip", "Stops blocks from pushing the camera in.", Category.RENDER);
    }
}
