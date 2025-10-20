package dsns.betterhud.util;

public class Setting {

    private String value;
    private String type;
    private final String defaultValue;
    private String[] possibleValues;

    public Setting(String value, String type, String[] possibleValues) {
        this.value = value;
        this.type = type;
        this.defaultValue = value;
        this.possibleValues = possibleValues;
    }

    public static Setting createStringSetting(
        String value,
        String[] possibleValues
    ) {
        return new Setting(value, "string", possibleValues);
    }

    public static Setting createBooleanSetting(boolean value) {
        return new Setting(
            String.valueOf(value),
            "boolean",
            new String[] { "true", "false" }
        );
    }

    public static Setting createIntegerSetting(int value, int min, int max) {
        return new Setting(
            String.valueOf(value),
            "integer",
            new String[] { String.valueOf(min), String.valueOf(max) }
        );
    }
    
    public static Setting createColorSetting(int value) {
        return new Setting(
            String.valueOf(value),
            "color",
            new String[] { String.valueOf(0x00000000), String.valueOf(0xffffffff) }
        );
    }

    public static Setting createDoubleSetting(
        double value,
        double min,
        double max
    ) {
        return new Setting(
            String.valueOf(value),
            "double",
            new String[] { String.valueOf(min), String.valueOf(max) }
        );
    }
    
    public String getType() {
        return type;
    }
    
    public String getStringValue() {
        return value;
    }
    
    public int getIntValue() {
        return Integer.parseInt(value);
    }
    
    public double getDoubleValue() {
        return Double.parseDouble(value);
    }
    
    public int getColorValue() {
        return Integer.parseInt(value);
    }
    
    public boolean getBooleanValue() {
        return Boolean.parseBoolean(value);
    }
    
    public String[] getPossibleValues() {
        return possibleValues;
    }
    
    public String getDefaultValue() {
        return defaultValue;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
}
