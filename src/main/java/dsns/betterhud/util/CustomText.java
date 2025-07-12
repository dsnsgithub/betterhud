package dsns.betterhud.util;
import dsns.betterhud.Config;

public class CustomText {
	public String text;
	public int color; // colors are in ARGB format
	public int backgroundColor; // colors are in ARGB format
	public boolean customPosition = false;
	public int customX = 0;
	public int customY = 0;

	public CustomText(String text, int color, int backgroundColor) {
		this.text = text;
		this.color = color;
		this.backgroundColor = backgroundColor;
	}

	public CustomText(String text) {
		this(text, Config.textColor, Config.backgroundColor);
	}

	public CustomText(String text, int color) {
		this(text, color, Config.backgroundColor);
	}
}