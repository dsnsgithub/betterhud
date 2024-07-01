package dsns.betterhud.util;

public class CustomText {
	public String text;
	public int color; // colors are in ARGB format
	public int backgroundColor; // colors are in ARGB format

	public CustomText(String text, int color, int backgroundColor) {
		this.text = text;
		this.color = color;
		this.backgroundColor = backgroundColor;
	}

	public CustomText(String text) {
		this(text, 0xffffffff, 0x88000000);
	}

	public CustomText(String text, int color) {
		this(text, color, 0x88000000);
	}
}