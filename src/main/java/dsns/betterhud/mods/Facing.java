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

public class Facing implements BaseMod {

    private static final ModSettings SETTINGS = new ModSettings("top-right");

    @Override
    public String getModID() {
        return "Facing";
    }

    @Override
    public ModSettings getModSettings() {
        return SETTINGS;
    }

    @Override
    //? if mc >= "26.1" {
    public CustomText onStartTick(Minecraft client) {
        Player player = client.player;

        if (player == null || player.getDirection() == null) return null;

        return new CustomText(
            formatSnakeCase(player.getDirection().name()),
            getModSettings()
        );
    }
    //?} else {
    /*public CustomText onStartTick(MinecraftClient client) {
        PlayerEntity player = client.player;

        if (player == null || player.getHorizontalFacing() == null) return null;

        return new CustomText(
            formatSnakeCase(player.getHorizontalFacing().name()),
            getModSettings()
        );
    }*/
    //?}

    public String formatSnakeCase(String biomeName) {
        String[] words = biomeName.split("_");
        for (int i = 0; i < words.length; i++) {
            words[i] =
                words[i].substring(0, 1).toUpperCase() +
                words[i].substring(1).toLowerCase();
        }
        return String.join(" ", words);
    }
}
