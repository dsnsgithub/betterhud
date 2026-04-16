package dsns.betterhud.mods;

import dsns.betterhud.util.BaseMod;
import dsns.betterhud.util.CustomText;
import dsns.betterhud.util.ModSettings;
import dsns.betterhud.util.Setting;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

class CoordinatesSettings extends ModSettings {

    public CoordinatesSettings(String position) {
        super(position);
        LinkedHashMap<String, Setting> settings = super.getSettings();

        List<Map.Entry<String, Setting>> entries = new ArrayList<>(
            settings.entrySet()
        );
        settings.clear();

        int insertIndex = 2;

        for (int i = 0; i < Math.min(insertIndex, entries.size()); i++) {
            Map.Entry<String, Setting> entry = entries.get(i);
            settings.put(entry.getKey(), entry.getValue());
        }

        settings.put("Decimal", Setting.createBooleanSetting(false));

        for (int i = insertIndex; i < entries.size(); i++) {
            Map.Entry<String, Setting> entry = entries.get(i);
            settings.put(entry.getKey(), entry.getValue());
        }
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
