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
import net.minecraft.util.math.MathHelper;

class MomentumSettings extends ModSettings {

    public MomentumSettings(String position) {
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

public class Momentum implements BaseMod {

    private static final ModSettings SETTINGS = new ModSettings("top-left");

    @Override
    public String getModID() {
        return "Momentum";
    }

    @Override
    public ModSettings getModSettings() {
        return SETTINGS;
    }

    @Override
    public CustomText onStartTick(MinecraftClient client) {
        PlayerEntity player = client.player;

        if (player == null) return null;

        double travelledX = player.getX() - player.lastRenderX;
        double travelledZ = player.getZ() - player.lastRenderZ;
        double currentSpeed =
            MathHelper.sqrt(
                (float) (travelledX * travelledX + travelledZ * travelledZ)
            ) /
            0.05F;

        if (SETTINGS.getSetting("Decimal").getBooleanValue()) {
            return new CustomText(
                String.format("%.2f m/s", currentSpeed),
                getModSettings()
            );
        } else {
            int roundedSpeed = (int) currentSpeed;

            return new CustomText(
                String.format(roundedSpeed + " m/s"),
                getModSettings()
            );
        }
    }
}
