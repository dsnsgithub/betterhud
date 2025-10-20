package dsns.betterhud;

import dsns.betterhud.util.BaseMod;
import dsns.betterhud.util.ModSettings;
import dsns.betterhud.util.Setting;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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
        if (Files.exists(configPath)) {
            deserialize();
        } else {
            serialize();
        }
    }

    public static void serialize() {
        HashMap<String, ModSettings> settings = new HashMap<>();
        
        if (settings.size() == 0) {
            for (BaseMod mod : BetterHUD.mods) {
                settings.put(mod.getModID(), mod.getModSettings());
            }
        }
        
        Properties prop = new Properties();

        HashMap<String, String> serialized = new HashMap<String, String>();

        for (Map.Entry<String, ModSettings> entry : settings.entrySet()) {
            ModSettings modSettings = entry.getValue();

            for (Map.Entry<String, Setting> settingEntry : modSettings
                .getSettings()
                .entrySet()) {
                System.out.println(settingEntry);
                System.out.println(settingEntry.getValue());
                System.out.println(settingEntry.getValue().getStringValue());

                serialized.put(
                    entry.getKey() + "." + settingEntry.getKey(),
                    settingEntry.getValue().getStringValue()
                );
            }
        }

        System.out.println("Serialized settings: " + serialized);

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
            System.out.println(
                "Loaded config with " + prop.size() + " properties."
            );
        } catch (Exception e) {
            System.err.println("Failed to load config: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        Map<String, String> map = new HashMap<String, String>();
        for (final String name : prop.stringPropertyNames()) {
            map.put(name, prop.getProperty(name));
        }

        // Load per-mod prefixed settings
        int updatedCount = 0;
        for (Map.Entry<String, ModSettings> modEntry : settings.entrySet()) {
            String modID = modEntry.getKey();
            ModSettings modSetting = modEntry.getValue();

            for (Map.Entry<String, Setting> entry : modSetting
                .getSettings()
                .entrySet()) {
                String fullKey = modID + "." + entry.getKey();
                String val = map.get(fullKey);
                if (val != null) {
                    entry.getValue().setValue(val);
                    updatedCount++;
                    System.out.println(
                        "Deserialized: " + fullKey + " = " + val
                    ); // Debug (remove after)
                } else {
                    System.out.println(
                        "No value found for " + fullKey + "; keeping default."
                    ); // Debug (remove after)
                }
            }
        }

        // Backwards compatibility: Migrate old global settings to all mods (one-time)
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
                    updatedCount++;
                }
            }
            System.out.println(
                "Migrated global backgroundColor to all mods: " + globalBg
            );
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
                    updatedCount++;
                }
            }
            System.out.println(
                "Migrated global textColor to all mods: " + globalText
            );
        }

        // Ignore other old globals (e.g., horizontalMargin) unless you map them to specific settings

        System.out.println("Deserialized " + updatedCount + " settings.");
        serialize(); // Re-save with migrated values
    }
}
