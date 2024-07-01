package dsns.betterhud;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Properties;

import net.fabricmc.loader.api.FabricLoader;

class ModSettings {
	public String orientation;
	public boolean enabled;
	public boolean customPosition;
	public int customX;
	public int customY;

	public ModSettings(String orientation, boolean enabled, boolean customPosition, int customX, int customY) {
		this.orientation = orientation;
		this.enabled = enabled;
		this.customPosition = customPosition;
		this.customX = customX;
		this.customY = customY;
	}

	public ModSettings(String orientation) {
		this(orientation, true, false, 0, 0);
	}

	public ModSettings() {
		this("top-left", true, false, 0, 0);
	}
}

public class Config {
	public static int verticalPadding = 4;
	public static int horizontalPadding = 4;

	public static int verticalMargin = 1;
	public static int horizontalMargin = 1;

	public static int lineHeight = 1;

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
		defaults.put("Time", new ModSettings("bottom-right"));

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
			prop.setProperty(modID + ".customPosition", String.valueOf(modSettings.customPosition));
			prop.setProperty(modID + ".customX", String.valueOf(modSettings.customX));
			prop.setProperty(modID + ".customY", String.valueOf(modSettings.customY));
		}

		prop.setProperty("verticalPadding", String.valueOf(Config.verticalPadding));
		prop.setProperty("horizontalPadding" + "", String.valueOf(Config.horizontalPadding));
		prop.setProperty("verticalMargin", String.valueOf(Config.verticalMargin));
		prop.setProperty("horizontalMargin" + "", String.valueOf(Config.horizontalMargin));
		prop.setProperty("lineHeight", String.valueOf(Config.lineHeight));
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
			modSettings.enabled = getBooleanProperty(prop, modID + ".enabled", modSettings.enabled);
			modSettings.orientation = getStringProperty(prop, modID + ".orientation", modSettings.orientation);
			modSettings.customPosition = getBooleanProperty(prop, modID + ".customPosition",
					modSettings.customPosition);
			modSettings.customX = getIntProperty(prop, modID + ".customX", modSettings.customX);
			modSettings.customY = getIntProperty(prop, modID + ".customY", modSettings.customY);
		}

		Config.verticalPadding = getIntProperty(prop, "verticalPadding", Config.verticalPadding);
		Config.horizontalPadding = getIntProperty(prop, "horizontalPadding", Config.horizontalPadding);
		Config.verticalMargin = getIntProperty(prop, "verticalMargin", Config.verticalMargin);
		Config.horizontalMargin = getIntProperty(prop, "horizontalMargin", Config.horizontalMargin);
		Config.lineHeight = getIntProperty(prop, "lineHeight", Config.lineHeight);
		Config.textColor = getIntProperty(prop, "textColor", Config.textColor);
		Config.backgroundColor = getIntProperty(prop, "backgroundColor", Config.backgroundColor);

		serialize();
	}

	private static String getStringProperty(Properties prop, String key, String defaultValue) {
		try {
			String value = prop.getProperty(key);
			return (value != null) ? value : defaultValue;
		} catch (Exception e) {
			return defaultValue;
		}
	}

	private static int getIntProperty(Properties prop, String key, int defaultValue) {
		try {
			String value = prop.getProperty(key);
			return (value != null) ? Integer.parseInt(value) : defaultValue;
		} catch (Exception e) {
			return defaultValue;
		}
	}

	private static boolean getBooleanProperty(Properties prop, String key, boolean defaultValue) {
		try {
			String value = prop.getProperty(key);
			return (value != null) ? Boolean.parseBoolean(value) : defaultValue;
		} catch (Exception e) {
			return defaultValue;
		}
	}
}
