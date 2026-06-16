package com.horror.client.module;

import com.horror.client.config.ConfigManager;
import com.horror.client.module.modules.*;
import com.horror.client.util.SingleplayerGuard;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Registry and dispatcher for all trainer modules.
 */
public final class ModuleManager {
    private static final List<Module> MODULES = new ArrayList<>();
    private static final List<Module> ENABLED_MODULES = new ArrayList<>();
    private static final List<Module> ENABLED_TICK_MODULES = new ArrayList<>();
    private static final List<Module> ENABLED_RENDER3D_MODULES = new ArrayList<>();
    private static final List<Module> KEYBOUND_MODULES = new ArrayList<>();
    private static final Map<Class<? extends Module>, Module> BY_CLASS = new HashMap<>();
    private static final Set<Class<? extends Module>> ENABLED_CLASSES = new HashSet<>();
    private static final Set<Class<? extends Module>> TICK_CLASSES = new HashSet<>();
    private static final Set<Class<? extends Module>> RENDER3D_CLASSES = new HashSet<>();
    private static final Map<Integer, Boolean> KEY_STATES = new HashMap<>();
    private static boolean antiCactusEnabled;
    private static boolean cameraDistanceEnabled;
    private static boolean cameraNoClipEnabled;
    private static boolean criticalsEnabled;
    private static boolean fastBreakEnabled;
    private static boolean noFallEnabled;
    private static boolean noFireOverlayEnabled;
    private static boolean noFogEnabled;
    private static boolean noHurtcamEnabled;
    private static boolean noPortalOverlayEnabled;
    private static boolean noPumpkinEnabled;
    private static boolean noSlowdownEnabled;
    private static boolean noVignetteEnabled;
    private static boolean noWallOverlayEnabled;
    private static boolean noWaterOverlayEnabled;
    private static boolean noWebEnabled;
    private static boolean reachEnabled;
    private static boolean snowShoeEnabled;
    private static boolean xrayEnabled;

    private ModuleManager() {}

    public static void init() {
        MODULES.clear();
        ENABLED_MODULES.clear();
        ENABLED_TICK_MODULES.clear();
        ENABLED_RENDER3D_MODULES.clear();
        KEYBOUND_MODULES.clear();
        BY_CLASS.clear();
        ENABLED_CLASSES.clear();
        TICK_CLASSES.clear();
        RENDER3D_CLASSES.clear();
        KEY_STATES.clear();
        resetFastFlags();

        // Combat
        register(new KillAura());
        register(new AutoCrystal());
        register(new Criticals());
        register(new AutoTotem());
        register(new AutoSword());
        register(new AutoArmor());
        register(new Reach());

        // Movement
        register(new Flight());
        register(new Speed());
        register(new Bunnyhop());
        register(new AutoWalk());
        register(new AntiAfk());
        register(new InvWalk());
        register(new Sneak());
        register(new Jetpack());
        register(new AutoSwim());
        register(new Dolphin());
        register(new Jesus());
        register(new NoClip());
        register(new HighJump());
        register(new FastLadder());
        register(new Spider());
        register(new Glide());
        register(new Parkour());
        register(new SafeWalk());
        register(new SnowShoe());
        register(new NoWeb());
        register(new NoFall());
        register(new Sprint());
        register(new Step());

        // Player
        register(new AutoTool());
        register(new AutoFish());
        register(new AutoSoup());
        register(new AutoDrop());
        register(new FastPlace());
        register(new FastBreak());
        register(new AutoEat());
        register(new AutoRespawn());
        register(new NoSlowdown());
        register(new AntiCactus());
        register(new AntiFire());

        // Render
        register(new ESP());
        register(new Tracers());
        register(new ItemESP());
        register(new ChestESP());
        register(new PortalESP());
        register(new BarrierESP());
        register(new MobSpawnESP());
        register(new LiquidESP());
        register(new SearchESP());
        register(new Fullbright());
        register(new Xray());
        register(new NoWeather());
        register(new NoFireOverlay());
        register(new NoWaterOverlay());
        register(new NoWallOverlay());
        register(new NoVignette());
        register(new NoPumpkin());
        register(new NoPortalOverlay());
        register(new NoHurtcam());
        register(new NoFog());
        register(new CameraDistance());
        register(new CameraNoClip());
        register(new AntiBlind());
        register(new NoLevitation());
        register(new Freecam());

        // World
        register(new Nuker());
        register(new Scaffold());
        register(new AutoMine());
        register(new AutoFarm());
        register(new AutoTorch());
        register(new VeinMiner());
        register(new Excavator());
        register(new Tunneller());
        register(new AutoBonemeal());

        // Misc
        register(new ClickGuiModule());
        register(new Hud());
        register(new FpsDisplay());

        ConfigManager.load();
    }

    private static void register(Module module) {
        MODULES.add(module);
        BY_CLASS.put(module.getClass(), module);
        if (overrides(module.getClass(), "onTick")) {
            TICK_CLASSES.add(module.getClass());
        }
        if (overrides(module.getClass(), "onRender3D", WorldRenderContext.class)) {
            RENDER3D_CLASSES.add(module.getClass());
        }
        if (module.isEnabled()) {
            addEnabledModule(module);
        }
    }

    public static void onModuleStateChanged(Module module, boolean enabled) {
        updateFastFlags(module, enabled);
        if (enabled) {
            addEnabledModule(module);
        } else if (ENABLED_CLASSES.remove(module.getClass())) {
            ENABLED_MODULES.remove(module);
            ENABLED_TICK_MODULES.remove(module);
            ENABLED_RENDER3D_MODULES.remove(module);
        }
    }

    public static void onModuleKeyChanged(Module module, int oldKey, int newKey) {
        if (oldKey >= 0) {
            KEY_STATES.remove(oldKey);
        }
        if (newKey >= 0) {
            if (!KEYBOUND_MODULES.contains(module)) {
                KEYBOUND_MODULES.add(module);
            }
        } else {
            KEYBOUND_MODULES.remove(module);
        }
    }

    public static List<Module> getModules() {
        return MODULES;
    }

    public static List<Module> getByCategory(Category category) {
        List<Module> result = new ArrayList<>();
        for (Module module : MODULES) {
            if (module.getCategory() == category) {
                result.add(module);
            }
        }
        return result;
    }

    public static List<Module> getEnabled() {
        return new ArrayList<>(ENABLED_MODULES);
    }

    public static List<Module> getEnabledForHud() {
        List<Module> result = new ArrayList<>();
        for (Module module : ENABLED_MODULES) {
            if (module instanceof Hud || module instanceof FpsDisplay || module instanceof ClickGuiModule) {
                continue;
            }
            result.add(module);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Module> T get(Class<T> clazz) {
        return (T) BY_CLASS.get(clazz);
    }

    public static boolean isOn(Class<? extends Module> clazz) {
        return ENABLED_CLASSES.contains(clazz) && SingleplayerGuard.canRun();
    }

    public static boolean isAntiCactusOn() {
        return antiCactusEnabled && SingleplayerGuard.canRun();
    }

    public static boolean isCameraDistanceOn() {
        return cameraDistanceEnabled && SingleplayerGuard.canRun();
    }

    public static boolean isCameraNoClipOn() {
        return cameraNoClipEnabled && SingleplayerGuard.canRun();
    }

    public static boolean isCriticalsOn() {
        return criticalsEnabled && SingleplayerGuard.canRun();
    }

    public static boolean isFastBreakOn() {
        return fastBreakEnabled && SingleplayerGuard.canRun();
    }

    public static boolean isNoFallOn() {
        return noFallEnabled && SingleplayerGuard.canRun();
    }

    public static boolean isNoFireOverlayOn() {
        return noFireOverlayEnabled && SingleplayerGuard.canRun();
    }

    public static boolean isNoFogOn() {
        return noFogEnabled && SingleplayerGuard.canRun();
    }

    public static boolean isNoHurtcamOn() {
        return noHurtcamEnabled && SingleplayerGuard.canRun();
    }

    public static boolean isNoPortalOverlayOn() {
        return noPortalOverlayEnabled && SingleplayerGuard.canRun();
    }

    public static boolean isNoPumpkinOn() {
        return noPumpkinEnabled && SingleplayerGuard.canRun();
    }

    public static boolean isNoSlowdownOn() {
        return noSlowdownEnabled && SingleplayerGuard.canRun();
    }

    public static boolean isNoVignetteOn() {
        return noVignetteEnabled && SingleplayerGuard.canRun();
    }

    public static boolean isNoWallOverlayOn() {
        return noWallOverlayEnabled && SingleplayerGuard.canRun();
    }

    public static boolean isNoWaterOverlayOn() {
        return noWaterOverlayEnabled && SingleplayerGuard.canRun();
    }

    public static boolean isNoWebOn() {
        return noWebEnabled && SingleplayerGuard.canRun();
    }

    public static boolean isReachOn() {
        return reachEnabled && SingleplayerGuard.canRun();
    }

    public static boolean isSnowShoeOn() {
        return snowShoeEnabled && SingleplayerGuard.canRun();
    }

    public static boolean isXrayOn() {
        return xrayEnabled && SingleplayerGuard.canRun();
    }

    public static void onTick() {
        if (ENABLED_TICK_MODULES.isEmpty()) {
            return;
        }
        if (!SingleplayerGuard.canRun()) {
            return;
        }
        List<Module> tickModules = new ArrayList<>(ENABLED_TICK_MODULES);
        for (Module module : tickModules) {
            if (!module.isEnabled()) {
                continue;
            }
            try {
                module.onTick();
            } catch (Exception ignored) {
                // A misbehaving module must never crash the client.
            }
        }
    }

    public static void onKeybinds() {
        if (KEYBOUND_MODULES.isEmpty()) {
            return;
        }
        if (!SingleplayerGuard.canRun()) {
            return;
        }

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.currentScreen != null) {
            return;
        }

        long handle = mc.getWindow().getHandle();
        for (Module module : KEYBOUND_MODULES) {
            int key = module.getKey();
            if (key < 0) {
                continue;
            }

            boolean pressed = InputUtil.isKeyPressed(handle, key);
            boolean wasPressed = KEY_STATES.getOrDefault(key, false);
            if (pressed && !wasPressed) {
                module.toggle();
            }
            KEY_STATES.put(key, pressed);
        }
    }

    public static void onRender3D(WorldRenderContext context) {
        if (ENABLED_RENDER3D_MODULES.isEmpty()) {
            return;
        }
        if (!SingleplayerGuard.canRun()) {
            return;
        }
        List<Module> renderModules = new ArrayList<>(ENABLED_RENDER3D_MODULES);
        for (Module module : renderModules) {
            if (!module.isEnabled()) {
                continue;
            }
            try {
                module.onRender3D(context);
            } catch (Exception ignored) {
            }
        }
        // Flush all accumulated ESP/tracer lines in a single draw call.
        com.horror.client.gui.render.Render3D.flush(context);
    }

    private static boolean overrides(Class<? extends Module> clazz, String methodName, Class<?>... parameterTypes) {
        try {
            Method method = clazz.getMethod(methodName, parameterTypes);
            return method.getDeclaringClass() != Module.class;
        } catch (NoSuchMethodException ignored) {
            return false;
        }
    }

    private static void addEnabledModule(Module module) {
        Class<? extends Module> clazz = module.getClass();
        if (ENABLED_CLASSES.add(clazz)) {
            ENABLED_MODULES.add(module);
        }
        if (TICK_CLASSES.contains(clazz) && !ENABLED_TICK_MODULES.contains(module)) {
            ENABLED_TICK_MODULES.add(module);
        }
        if (RENDER3D_CLASSES.contains(clazz) && !ENABLED_RENDER3D_MODULES.contains(module)) {
            ENABLED_RENDER3D_MODULES.add(module);
        }
    }

    private static void resetFastFlags() {
        antiCactusEnabled = false;
        cameraDistanceEnabled = false;
        cameraNoClipEnabled = false;
        criticalsEnabled = false;
        fastBreakEnabled = false;
        noFallEnabled = false;
        noFireOverlayEnabled = false;
        noFogEnabled = false;
        noHurtcamEnabled = false;
        noPortalOverlayEnabled = false;
        noPumpkinEnabled = false;
        noSlowdownEnabled = false;
        noVignetteEnabled = false;
        noWallOverlayEnabled = false;
        noWaterOverlayEnabled = false;
        noWebEnabled = false;
        reachEnabled = false;
        snowShoeEnabled = false;
        xrayEnabled = false;
    }

    private static void updateFastFlags(Module module, boolean enabled) {
        if (module instanceof AntiCactus) {
            antiCactusEnabled = enabled;
        } else if (module instanceof CameraDistance) {
            cameraDistanceEnabled = enabled;
        } else if (module instanceof CameraNoClip) {
            cameraNoClipEnabled = enabled;
        } else if (module instanceof Criticals) {
            criticalsEnabled = enabled;
        } else if (module instanceof FastBreak) {
            fastBreakEnabled = enabled;
        } else if (module instanceof NoFall) {
            noFallEnabled = enabled;
        } else if (module instanceof NoFireOverlay) {
            noFireOverlayEnabled = enabled;
        } else if (module instanceof NoFog) {
            noFogEnabled = enabled;
        } else if (module instanceof NoHurtcam) {
            noHurtcamEnabled = enabled;
        } else if (module instanceof NoPortalOverlay) {
            noPortalOverlayEnabled = enabled;
        } else if (module instanceof NoPumpkin) {
            noPumpkinEnabled = enabled;
        } else if (module instanceof NoSlowdown) {
            noSlowdownEnabled = enabled;
        } else if (module instanceof NoVignette) {
            noVignetteEnabled = enabled;
        } else if (module instanceof NoWallOverlay) {
            noWallOverlayEnabled = enabled;
        } else if (module instanceof NoWaterOverlay) {
            noWaterOverlayEnabled = enabled;
        } else if (module instanceof NoWeb) {
            noWebEnabled = enabled;
        } else if (module instanceof Reach) {
            reachEnabled = enabled;
        } else if (module instanceof SnowShoe) {
            snowShoeEnabled = enabled;
        } else if (module instanceof Xray) {
            xrayEnabled = enabled;
        }
    }
}
