# Anixty Client

Singleplayer-Trainer für **Minecraft 1.20.1 (Fabric)** mit ClickGUI, HUD-Overlay und
einem Modulsystem mit **funktionierender Logik** (25 Module über 6 Kategorien).
Gedacht für die eigene Singleplayer-Welt.

## Module

| Kategorie | Module |
|-----------|--------|
| Combat    | KillAura, AutoCrystal, Criticals, AutoTotem, Reach |
| Movement  | Flight, Speed, NoFall, Sprint, Step |
| Player    | FastPlace, FastBreak, AutoEat, NoSlowdown |
| Render    | ESP, Tracers, Fullbright, Xray, Freecam |
| World     | Nuker, Scaffold, AutoMine |
| Misc      | ClickGUI, Hud, FpsDisplay |

Viele Module haben einstellbare Settings (Slider/Checkbox/Modus), erreichbar per
**Rechtsklick auf das Modul** in der ClickGUI.

## Bedienung

- **Rechte Shift-Taste** öffnet/aktualisiert die ClickGUI (nur im Spiel/Singleplayer).
- **Linksklick + ziehen** auf einer Panel-Kopfzeile → Panel verschieben.
- **Rechtsklick** auf eine Panel-Kopfzeile → Panel ein-/ausklappen.
- **Linksklick** auf ein Modul → Modul togglen (erscheint dann in der HUD-Liste oben rechts).
- **Rechtsklick** auf ein Modul → Settings (Slider/Checkbox/Modus) ein-/ausklappen.
- **ESC** schließt die GUI.

> Hinweis: Das HUD-Overlay ist an das Modul **Hud** gekoppelt (standardmäßig an).
> Manche Module sind bewusst vereinfacht (z. B. AutoCrystal = Crystals brechen,
> Scaffold = einfache Variante); **Reach** und **Criticals** sind durch die Limits
> des integrierten Servers begrenzt (~6 Blöcke).

## Projektstruktur

```
src/main/java/com/horror/client/
├── HorrorClient.java          # ClientModInitializer: Keybind + Setup
├── module/
│   ├── Category.java          # Kategorien (Combat, Movement, ...)
│   ├── Module.java            # Basis-Modul (Toggle + leere onEnable/onDisable-Hooks)
│   └── ModuleManager.java     # Registry aller Module
└── gui/
    ├── Theme.java             # Farbpalette
    ├── ClickGui.java          # Haupt-Screen
    ├── HudRenderer.java       # Watermark + ArrayList-Overlay
    └── widget/
        ├── Panel.java         # Verschiebbares Kategorie-Panel
        └── ModuleButton.java  # Einzelne Modulzeile
```

Funktionen lassen sich später ergänzen, indem `onEnable()` / `onDisable()` in `Module`
befüllt oder spezialisierte Subklassen registriert werden.

## Bauen

```powershell
$env:JAVA_HOME = "<Pfad zu JDK 17>"
.\gradlew build
```

Das fertige Mod-Jar liegt anschließend in `build/libs/horror-client-1.0.0.jar`.

## Installieren

1. **Fabric Loader** für 1.20.1 installieren.
2. **Fabric API** (0.92.x für 1.20.1) in den `mods`-Ordner legen.
3. `horror-client-1.0.0.jar` in den `mods`-Ordner legen.
