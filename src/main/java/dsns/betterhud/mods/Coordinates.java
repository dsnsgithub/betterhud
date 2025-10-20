package dsns.betterhud.mods;

import dsns.betterhud.util.BaseMod;
import dsns.betterhud.util.CustomText;
import dsns.betterhud.util.ModSettings;
import dsns.betterhud.util.Setting;
import java.util.HashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

class CoordinatesSettings extends ModSettings {

    public CoordinatesSettings(String position) {
        super(position);
        HashMap<String, Setting> settings = super.getSettings();
        settings.put("decimal", Setting.createBooleanSetting(false));
    }
}

public class Coordinates implements BaseMod {

    @Override
    public String getModID() {
        return "Coordinates";
    }

    @Override
    public ModSettings getModSettings() {
        return new CoordinatesSettings("top-right");
    }

    @Override
    public CustomText onStartTick(MinecraftClient client) {
        PlayerEntity player = client.player;

        if (player == null) return null;

        String roundX = String.format("%.2f", player.getX());
        String roundY = String.format("%.2f", player.getY());
        String roundZ = String.format("%.2f", player.getZ());

        return new CustomText(
            roundX + ", " + roundY + ", " + roundZ,
            getModSettings()
        );
    }
}
