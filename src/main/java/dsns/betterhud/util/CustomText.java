package dsns.betterhud.util;

public class CustomText {

    public String text;
    public int color; // colors are in ARGB format
    public int backgroundColor; // colors are in ARGB format
    public String position = "top-left"; // a corner, or "custom"
    public float scale = 1.0f;
    public float customX = 0;
    public float customY = 0;

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

    public void applyPlacement(ModSettings settings) {
        this.position = settings.getSetting("Position").getStringValue();
        this.customX = settings.getSetting("Custom X").getFloatValue();
        this.customY = settings.getSetting("Custom Y").getFloatValue();
        this.scale = settings.getSetting("Scale").getFloatValue();
    }
}
