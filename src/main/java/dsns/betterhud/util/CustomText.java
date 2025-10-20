package dsns.betterhud.util;

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

    public CustomText(String text, ModSettings settings) {
        this(
            text,
            settings.getSetting("textColor").getColorValue(),
            settings.getSetting("backgroundColor").getColorValue()
        );
    }

    public CustomText(String text, int color, ModSettings settings) {
        this(
            text,
            color,
            settings.getSetting("backgroundColor").getColorValue()
        );
    }
}
