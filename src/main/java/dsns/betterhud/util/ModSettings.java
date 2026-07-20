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

        settings.put(
            "Position",
            Setting.createStringSetting("top-left", POSITIONS)
        );

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
