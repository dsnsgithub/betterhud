package dsns.betterhud.mods;

import dsns.betterhud.util.BaseMod;
import dsns.betterhud.util.CustomText;
import dsns.betterhud.util.ModSettings;
import dsns.betterhud.util.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

class CoordinatesSettings extends ModSettings {

    public CoordinatesSettings(String orientation) {
        super(orientation);
        insertSettingAfter(
            "Orientation",
            "Decimal",
            Setting.createBooleanSetting(false)
        );
    }
}

public class Coordinates implements BaseMod {

    private static final CoordinatesSettings SETTINGS = new CoordinatesSettings(
        "top-right"
    );

    @Override
    public String getModID() {
        return "Coordinates";
    }

    @Override
    public ModSettings getModSettings() {
        return SETTINGS;
    }

    @Override
    public CustomText onStartTick(MinecraftClient client) {
        PlayerEntity player = client.player;

        if (player == null) return null;

        if (SETTINGS.getSetting("Decimal").getBooleanValue()) {
            String roundX = String.format("%.2f", player.getX());
            String roundY = String.format("%.2f", player.getY());
            String roundZ = String.format("%.2f", player.getZ());

            return new CustomText(
                roundX + ", " + roundY + ", " + roundZ,
                getModSettings()
            );
        } else {
            int x = player.getBlockX();
            int y = player.getBlockY();
            int z = player.getBlockZ();

            return new CustomText(x + ", " + y + ", " + z, getModSettings());
        }
    }
}
