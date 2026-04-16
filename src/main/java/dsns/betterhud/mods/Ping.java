package dsns.betterhud.mods;

import dsns.betterhud.util.BaseMod;
import dsns.betterhud.util.CustomText;
import dsns.betterhud.util.ModSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

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
    public CustomText onStartTick(MinecraftClient client) {
        PlayerEntity player = client.player;

        if (
            player == null ||
            player.getUuid() == null ||
            client.getNetworkHandler() == null ||
            client.getNetworkHandler().getPlayerListEntry(player.getUuid()) ==
            null
        ) return null;

        return new CustomText(
            client
                    .getNetworkHandler()
                    .getPlayerListEntry(player.getUuid())
                    .getLatency() +
                " ms",
            getModSettings()
        );
    }
}
