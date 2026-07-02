package dsns.betterhud.util;

public class CustomText {

    public String text;
    public int color; // colors are in ARGB format
    public int backgroundColor; // colors are in ARGB format
    public boolean customPosition = false;
    public float scale = 1.0f;
    public int customX = 0;
    public int customY = 0;

    public CustomText(String text, int color, float scale, int backgroundColor) {
        this.text = text;
        this.color = color;
        this.scale = scale;
        this.backgroundColor = backgroundColor;
    }

    public CustomText(String text, ModSettings settings) {
        this(
            text,
            settings.getSetting("Text Color").getColorValue(),
            settings.getSetting("Scale").getFloatValue(),
            settings.getSetting("Background Color").getColorValue()
        );
    }

    public CustomText(String text, int color, ModSettings settings) {
        this(
            text,
            color,
            settings.getSetting("Scale").getFloatValue(),
            settings.getSetting("Background Color").getColorValue()
        );
    }
}
