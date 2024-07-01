package dsns.betterhud;

class ModSettings {
	public String orientation = "top-left";
	public boolean enabled = true;

	public ModSettings(String orientation, boolean enabled) {
		this.orientation = orientation;
		this.enabled = enabled;
	}

	public ModSettings() {
		this("top-left", true);
	}
}

public class Config {
	public static int verticalPadding = 2;
	public static int horizontalPadding = 4;

	public static ModSettings fps = new ModSettings();
	public static ModSettings ping = new ModSettings();
	public static ModSettings coordinates = new ModSettings();
	public static ModSettings biome = new ModSettings();
	public static ModSettings momentum = new ModSettings();
}
