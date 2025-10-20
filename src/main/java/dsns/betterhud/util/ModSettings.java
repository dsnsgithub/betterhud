package dsns.betterhud.util;

import java.util.LinkedHashMap;

public class ModSettings {

    private LinkedHashMap<String, Setting> settings = new LinkedHashMap<
        String,
        Setting
    >();

    public ModSettings() {
        settings.put("Enabled", Setting.createBooleanSetting(true));

        settings.put(
            "Orientation",
            Setting.createStringSetting(
                "top-left",
                new String[] {
                    "top-left",
                    "top-right",
                    "bottom-left",
                    "bottom-right",
                }
            )
        );

        settings.put("Custom Position", Setting.createBooleanSetting(false));
        settings.put("Custom X", Setting.createIntegerSetting(0, 0, 100));
        settings.put("Custom Y", Setting.createIntegerSetting(0, 0, 100));
        settings.put("Text Color", Setting.createColorSetting(0xffffffff));
        settings.put(
            "Background Color",
            Setting.createColorSetting(0x88000000)
        );
    }

    public ModSettings(String orientation) {
        this();
        settings.replace(
            "Orientation",
            Setting.createStringSetting(
                orientation,
                new String[] {
                    "top-left",
                    "top-right",
                    "bottom-left",
                    "bottom-right",
                }
            )
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
