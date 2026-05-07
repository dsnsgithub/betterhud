package dsns.betterhud.mods;

import dsns.betterhud.util.BaseMod;
import dsns.betterhud.util.CustomText;
import dsns.betterhud.util.ModSettings;
//? if mc >= "26.1" {
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
//?} else {
/*import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;*/
//?}

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
    //? if mc >= "26.1" {
    public CustomText onStartTick(Minecraft client) {
        Player player = client.player;

        if (
            player == null ||
            player.getUUID() == null ||
            client.getConnection() == null ||
            client.getConnection().getPlayerInfo(player.getUUID()) == null
        ) return null;

        return new CustomText(
            client.getConnection().getPlayerInfo(player.getUUID()).getLatency() + " ms",
            getModSettings()
        );
    }
    //?} else {
    /*public CustomText onStartTick(MinecraftClient client) {
        PlayerEntity player = client.player;

        if (
            player == null ||
            player.getUuid() == null ||
            client.getNetworkHandler() == null ||
            client.getNetworkHandler().getPlayerListEntry(player.getUuid()) == null
        ) return null;

        return new CustomText(
            client.getNetworkHandler().getPlayerListEntry(player.getUuid()).getLatency() + " ms",
            getModSettings()
        );
    }*/
    //?}
}
