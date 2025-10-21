package dsns.betterhud;

import dsns.betterhud.util.BaseMod;
import dsns.betterhud.util.ModSettings;
import dsns.betterhud.util.Setting;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import net.fabricmc.loader.api.FabricLoader;

public class Config {

    private static final Path configPath = FabricLoader.getInstance()
        .getConfigDir()
        .resolve("betterhud.properties");

    private static HashMap<String, ModSettings> settings = new HashMap<>();

    public static void configure() {
        if (settings.isEmpty()) {
            for (BaseMod mod : BetterHUD.mods) {
                settings.put(mod.getModID(), mod.getModSettings());
            }
        }

        if (Files.exists(configPath)) {
            deserialize();
        } else {
            serialize();
        }
    }

    private static String titleCaseToCamelCase(String title) {
        if (title == null || title.trim().isEmpty()) {
            return title;
        }

        String[] words = title.trim().split("\\s+");
        if (words.length == 0) {
            return "";
        }

        StringBuilder result = new StringBuilder(words[0].toLowerCase());

        for (int i = 1; i < words.length; i++) {
            String word = words[i];
            if (!word.isEmpty()) {
                String camelWord =
                    word.substring(0, 1).toUpperCase() +
                    word.substring(1).toLowerCase();
                result.append(camelWord);
            }
        }

        return result.toString();
    }

    public static void serialize() {
        Properties prop = new Properties();
        HashMap<String, String> serialized = new HashMap<String, String>();

        for (Map.Entry<String, ModSettings> entry : settings.entrySet()) {
            ModSettings modSettings = entry.getValue();

            for (Map.Entry<String, Setting> settingEntry : modSettings
                .getSettings()
                .entrySet()) {
                serialized.put(
                    entry.getKey() + "." + settingEntry.getKey(),
                    settingEntry.getValue().getStringValue()
                );
            }
        }

        prop.putAll(serialized);

        try {
            prop.store(Files.newOutputStream(configPath), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deserialize() {
        Properties prop = new Properties();
        try {
            prop.load(Files.newInputStream(configPath));
        } catch (Exception e) {
            System.err.println("Failed to load config: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        for (final String name : prop.stringPropertyNames()) {
            map.put(name, prop.getProperty(name));
        }

        for (Map.Entry<String, ModSettings> modEntry : settings.entrySet()) {
            String modID = modEntry.getKey();
            ModSettings modSetting = modEntry.getValue();

            for (Map.Entry<String, Setting> entry : modSetting
                .getSettings()
                .entrySet()) {
                String fullKey = modID + "." + entry.getKey();
                String oldConfigKey =
                    modID + "." + titleCaseToCamelCase(entry.getKey());

                String val = map.get(fullKey);
                String oldVal = map.get(oldConfigKey);
                if (val != null) {
                    entry.getValue().setValue(val);
                } else if (oldVal != null) {
                    entry.getValue().setValue(oldVal);
                }
            }
        }

        String globalBg = map.get("backgroundColor");
        if (globalBg != null) {
            for (Map.Entry<
                String,
                ModSettings
            > modEntry : settings.entrySet()) {
                Setting bgSetting = modEntry
                    .getValue()
                    .getSetting("Background Color");
                if (bgSetting != null) {
                    bgSetting.setValue(globalBg);
                }
            }
        }

        String globalText = map.get("textColor");
        if (globalText != null) {
            for (Map.Entry<
                String,
                ModSettings
            > modEntry : settings.entrySet()) {
                Setting textSetting = modEntry
                    .getValue()
                    .getSetting("Text Color");
                if (textSetting != null) {
                    textSetting.setValue(globalText);
                }
            }
        }

        serialize();
    }
}
