package dsns.betterhud.util;

import java.util.LinkedHashMap;

public class ModSettings {

    public static final String[] POSITIONS = {
        "top-left",
        "top-right",
        "bottom-left",
        "bottom-right",
        "custom",
    };

    private LinkedHashMap<String, Setting> settings = new LinkedHashMap<>();

    public ModSettings() {
        settings.put("Enabled", Setting.createBooleanSetting(true));

        // A corner docks the element into that corner's stack; "custom" places
        // it at Custom X/Y. Set by dragging in the HUD editor, or from the
        // settings screen to reset an element back to a corner.
        settings.put(
            "Position",
            Setting.createStringSetting("top-left", POSITIONS)
        );

        // Percentages (0-100) of the space the element can occupy without
        // leaving the screen. Doubles so dragging in the HUD editor is smooth;
        // old integer config values still parse.
        settings.put("Custom X", Setting.createDoubleSetting(0, 0, 100));
        settings.put("Custom Y", Setting.createDoubleSetting(0, 0, 100));
        settings.put("Text Color", Setting.createColorSetting(0xffffffff));
        settings.put("Scale", Setting.createDoubleSetting(1.0, 0.1, 10.0));
        settings.put(
            "Background Color",
            Setting.createColorSetting(0x88000000)
        );
    }

    public ModSettings(String position) {
        this();
        settings.replace(
            "Position",
            Setting.createStringSetting(position, POSITIONS)
        );
    }

    public LinkedHashMap<String, Setting> getSettings() {
        return settings;
    }

    public Setting getSetting(String key) {
        return settings.get(key);
    }

    public void setSettings(LinkedHashMap<String, Setting> settings) {
        this.settings = settings;
    }
}
