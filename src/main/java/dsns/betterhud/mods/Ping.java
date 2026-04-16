package dsns.betterhud.mods;

import dsns.betterhud.util.BaseMod;
import dsns.betterhud.util.CustomText;
import dsns.betterhud.util.ModSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class Ping implements BaseMod {

    private static final ModSettings SETTINGS = new ModSettings("top-left");

    @Override
    public String getModID() {
        return "Ping";
    }

    @Override
    public ModSettings getModSettings() {
        return SETTINGS;
    }

    @Override
    public CustomText onStartTick(Minecraft client) {
        Player player = client.player;

        if (
            player == null ||
            player.getUUID() == null ||
            client.getConnection() == null ||
            client.getConnection().getPlayerInfo(player.getUUID()) ==
            null
        ) return null;

        return new CustomText(
            client
                    .getConnection()
                    .getPlayerInfo(player.getUUID())
                    .getLatency() +
                " ms",
            getModSettings()
        );
    }
}
