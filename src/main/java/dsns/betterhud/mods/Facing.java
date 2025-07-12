package dsns.betterhud.mods;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import dsns.betterhud.util.BaseMod;
import dsns.betterhud.util.CustomText;

public class Facing implements BaseMod {
	@Override
	public String getModID() {
		return "Facing";
	}

	@Override
	public CustomText onStartTick(MinecraftClient client) {
		PlayerEntity player = client.player;

		if (player == null || player.getHorizontalFacing() == null)
			return null;

		return new CustomText(formatSnakeCase(player.getHorizontalFacing().name()));
	}

	public String formatSnakeCase(String biomeName) {
		// Split the string by underscores
		String[] words = biomeName.split("_");

		// Capitalize each word
		for (int i = 0; i < words.length; i++) {
			words[i] = words[i].substring(0, 1).toUpperCase() + words[i].substring(1).toLowerCase();
		}

		// Join the words with spaces
		return String.join(" ", words);
	}
}
