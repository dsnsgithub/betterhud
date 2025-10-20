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

    public static void configure(ArrayList<BaseMod> mods) {
        if (settings.size() == 0) {
            for (BaseMod mod : mods) {
                settings.put(mod.getModID(), mod.getModSettings());
            }
        }

        if (Files.exists(configPath)) {
            deserialize(mods);
        } else {
            serialize();
        }
    }

    public static void serialize() {
        Properties prop = new Properties();
        prop.putAll(settings);

        try {
            prop.store(Files.newOutputStream(configPath), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deserialize(ArrayList<BaseMod> mods) {
        Properties prop = new Properties();
        try {
            prop.load(Files.newInputStream(configPath));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, String> map = new HashMap<String, String>();

        for (final String name : prop.stringPropertyNames()) {
            map.put(name, prop.getProperty(name));
        }

        // set each setting
        for (ModSettings modSetting : settings.values()) {
            for (Map.Entry<String, Setting> entry : modSetting
                .getSettings()
                .entrySet()) {
                entry.getValue().setValue(map.get(entry.getKey()));
            }
        }

        serialize();
    }
}
