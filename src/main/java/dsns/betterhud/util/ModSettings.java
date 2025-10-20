package dsns.betterhud.util;

import java.util.HashMap;

public class ModSettings {

    private HashMap<String, Setting> settings = new HashMap<String, Setting>();

    public ModSettings() {
        settings.put("enabled", Setting.createBooleanSetting(true));

        settings.put(
            "orientation",
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

        settings.put("customPosition", Setting.createBooleanSetting(false));
        settings.put("customX", Setting.createIntegerSetting(0, 0, 100));
        settings.put("customY", Setting.createIntegerSetting(0, 0, 100));
        settings.put("textColor", Setting.createColorSetting(0xffffffff));
        settings.put("backgroundColor", Setting.createColorSetting(0x00000000));
    }

    public ModSettings(String orientation) {
        super();
        settings.replace(
            "orientation",
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

    public HashMap<String, Setting> getSettings() {
        return settings;
    }

    public Setting getSetting(String key) {
        return settings.get(key);
    }

    public void setSettings(HashMap<String, Setting> settings) {
        this.settings = settings;
    }
}
