package dsns.betterhud.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ModSettings {

    protected LinkedHashMap<String, Setting> settings = new LinkedHashMap<>();

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
        Setting orientationSetting = settings.get("Orientation");
        if (orientationSetting != null) {
            orientationSetting.setValue(orientation);
        }
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

    protected void insertSettingAfter(
        String afterKey,
        String newKey,
        Setting newSetting
    ) {
        List<Map.Entry<String, Setting>> entries = new ArrayList<>(
            settings.entrySet()
        );
        int insertIndex = -1;
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).getKey().equals(afterKey)) {
                insertIndex = i + 1;
                break;
            }
        }

        if (insertIndex != -1) {
            settings.clear();
            for (int i = 0; i < insertIndex; i++) {
                Map.Entry<String, Setting> entry = entries.get(i);
                settings.put(entry.getKey(), entry.getValue());
            }
            settings.put(newKey, newSetting);
            for (int i = insertIndex; i < entries.size(); i++) {
                Map.Entry<String, Setting> entry = entries.get(i);
                settings.put(entry.getKey(), entry.getValue());
            }
        } else {
            settings.put(newKey, newSetting);
        }
    }
}
