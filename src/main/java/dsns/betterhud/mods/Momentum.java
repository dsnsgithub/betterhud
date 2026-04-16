package dsns.betterhud.mods;

import dsns.betterhud.util.BaseMod;
import dsns.betterhud.util.CustomText;
import dsns.betterhud.util.ModSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

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
    public CustomText onStartTick(Minecraft client) {
        Player player = client.player;

        if (player == null) return null;

        double travelledX = player.getX() - player.xOld;
        double travelledZ = player.getZ() - player.zOld;
        double currentSpeed =
            Mth.sqrt(
                (float) (travelledX * travelledX + travelledZ * travelledZ)
            ) /
            0.05F;

        return new CustomText(
            String.format("%.2f m/s", currentSpeed),
            getModSettings()
        );
    }
}
