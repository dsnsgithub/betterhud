package dsns.betterhud.mods;

import dsns.betterhud.util.BaseMod;
import dsns.betterhud.util.CustomText;
import dsns.betterhud.util.ModSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class Facing implements BaseMod {

    private static final ModSettings SETTINGS = new ModSettings(
        "top-right"
    );

    @Override
    public String getModID() {
        return "Facing";
    }

    @Override
    public ModSettings getModSettings() {
        return SETTINGS;
    }

    @Override
    public CustomText onStartTick(Minecraft client) {
        Player player = client.player;

        if (player == null || player.getDirection() == null) return null;

        return new CustomText(
            formatSnakeCase(player.getDirection().name()),
            getModSettings()
        );
    }

    public String formatSnakeCase(String biomeName) {
        // Split the string by underscores
        String[] words = biomeName.split("_");

        // Capitalize each word
        for (int i = 0; i < words.length; i++) {
            words[i] =
                words[i].substring(0, 1).toUpperCase() +
                words[i].substring(1).toLowerCase();
        }

        // Join the words with spaces
        return String.join(" ", words);
    }
}
