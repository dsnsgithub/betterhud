package dsns.betterhud;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Properties;

import net.fabricmc.loader.api.FabricLoader;

class ModSettings {
	public String orientation;
	public boolean enabled;

	public ModSettings(String orientation, boolean enabled) {
		this.orientation = orientation;
		this.enabled = enabled;
	}

	public ModSettings(String orientation) {
		this(orientation, true);
	}

	public ModSettings() {
		this("top-left", true);
	}
}

public class Config {
	public static int verticalPadding = 4;
	public static int horizontalPadding = 4;

	public static int verticalMargin = 1;
	public static int horizontalMargin = 1;

	public static int lineHeight = 8;
	
	public static int textColor = 0xffffffff;
	public static int backgroundColor = 0x88000000;

	public static HashMap<String, ModSettings> settings = new HashMap<String, ModSettings>();

	private static final Path configPath = FabricLoader.getInstance().getConfigDir()
			.resolve("betterhud.properties");

	public static HashMap<String, ModSettings> getDefaults() {
		HashMap<String, ModSettings> defaults = new HashMap<String, ModSettings>();

		defaults.put("FPS", new ModSettings("top-left"));
		defaults.put("Ping", new ModSettings("top-left"));
		defaults.put("Momentum", new ModSettings("top-left"));
		defaults.put("Coordinates", new ModSettings("top-right"));
		defaults.put("Biome", new ModSettings("top-right"));
		defaults.put("Facing", new ModSettings("top-right"));

		return defaults;
	}

	public static void configure() {
		if (settings.size() == 0) {
			settings = getDefaults();
		}

		if (Files.exists(configPath)) {
			deserialize();
		} else {
			serialize();
		}
	}

	public static void serialize() {
		Properties prop = new Properties();
		for (String modID : settings.keySet()) {
			ModSettings modSettings = settings.get(modID);
			prop.setProperty(modID + ".enabled", String.valueOf(modSettings.enabled));
			prop.setProperty(modID + ".orientation", modSettings.orientation);
		}

		prop.setProperty("verticalPadding", String.valueOf(Config.verticalPadding));
		prop.setProperty("horizontalPadding" + "", String.valueOf(Config.horizontalPadding));
		prop.setProperty("verticalMargin", String.valueOf(Config.verticalMargin));
		prop.setProperty("horizontalMargin" + "", String.valueOf(Config.horizontalMargin));
		prop.setProperty("textColor", String.valueOf(Config.textColor));
		prop.setProperty("backgroundColor", String.valueOf(Config.backgroundColor));

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
			e.printStackTrace();
		}
		for (String modID : settings.keySet()) {
			ModSettings modSettings = settings.get(modID);
			modSettings.enabled = Boolean.parseBoolean(prop.getProperty(modID + ".enabled"));
			modSettings.orientation = prop.getProperty(modID + ".orientation");
		}
		Config.verticalPadding = Integer.parseInt(prop.getProperty("verticalPadding"));
		Config.horizontalPadding = Integer.parseInt(prop.getProperty("horizontalPadding"));
		Config.textColor = Integer.parseInt(prop.getProperty("textColor"));
		Config.backgroundColor = Integer.parseInt(prop.getProperty("backgroundColor"));
	}
}
