package com.horror.client.module.modules;

import com.horror.client.module.Category;
import com.horror.client.module.Module;
import com.horror.client.module.setting.NumberSetting;

/** Changes third-person camera distance. */
public class CameraDistance extends Module {
    private final NumberSetting distance = new NumberSetting("Distance", 8, 4, 50, 1);

    public CameraDistance() {
        super("CameraDistance", "Changes third-person camera distance.", Category.RENDER);
        addSettings(distance);
    }

    public double getDistance() {
        return distance.get();
    }
}
