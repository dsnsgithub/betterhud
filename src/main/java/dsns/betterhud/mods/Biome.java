package dsns.betterhud.mods;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import dsns.betterhud.util.BaseMod;
import dsns.betterhud.util.CustomText;

public class Biome implements BaseMod {
	@Override
	public String getModID() {
		return "Biome";
	}

	@Override
	public CustomText onStartTick(MinecraftClient client) {
		PlayerEntity player = client.player;

		if (player == null)
			return null;

		// have to specify this because name of class is Biome
		Optional<RegistryKey<net.minecraft.world.biome.Biome>> biome = client.world.getBiome(player.getBlockPos())
				.getKey();

		if (!biome.isPresent())
			return null;

		String biomeString = formatSnakeCase(biome.get().getValue().getPath());

		// used to maintain order when iterating
		LinkedHashMap<String, Integer> biomeColors = new LinkedHashMap<>();
		biomeColors.put("Bamboo", 0x32cd32);
		biomeColors.put("Snowy", 0xffffff);
		biomeColors.put("Frozen", 0x87ceeb);
		biomeColors.put("Void", 0x000000);
		biomeColors.put("Flower", 0xff1493);
		biomeColors.put("Ice", 0x00ffff);
		biomeColors.put("Soul Sand", 0x8b4513);
		biomeColors.put("Crimson", 0xff0000);
		biomeColors.put("Warped", 0x00ff7f);
		biomeColors.put("Nether", 0xff4500);
		biomeColors.put("End", 0x8a2be2);
		biomeColors.put("Mushroom", 0xff00ff);
		biomeColors.put("Savanna", 0xff4500);
		biomeColors.put("Badlands", 0xcd853f);
		biomeColors.put("Swamp", 0x8b4513);
		biomeColors.put("Shore", 0x808080);
		biomeColors.put("Taiga", 0x556b2f);
		biomeColors.put("Mountains", 0xa9a9a9);
		biomeColors.put("Jungle", 0x00ff00);
		biomeColors.put("Birch", 0x7fff00);
		biomeColors.put("Dark Forest", 0x006400);
		biomeColors.put("Forest", 0x228b22);
		biomeColors.put("River", 0x4169e1);
		biomeColors.put("Warm Ocean", 0x26d9ed);
		biomeColors.put("Cold Ocean", 0x377de6);
		biomeColors.put("Deep Ocean", 0x377de6);
		biomeColors.put("Ocean", 0x34b1eb);
		biomeColors.put("Plains", 0x00ff00);
		biomeColors.put("Desert", 0xffff00);
		biomeColors.put("Beach", 0xffd700);
		biomeColors.put("Hills", 0x2e8b57);

		int color = 0xffffff; // default color

		for (Map.Entry<String, Integer> entry : biomeColors.entrySet()) {
			if (biomeString.contains(entry.getKey())) {
				color = entry.getValue();
				break;
			}
		}

		return new CustomText(biomeString, color);
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
