package dsns.betterhud.mods;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import dsns.betterhud.util.BaseMod;
import dsns.betterhud.util.CustomText;
import dsns.betterhud.util.ModSettings;

public class Biome implements BaseMod {
	@Override
	public String getModID() {
		return "Biome";
	}
	
	@Override
	public ModSettings getModSettings() {
	    return new ModSettings("top-right");
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
		biomeColors.put("Bamboo", 0xff32cd32);
		biomeColors.put("Snowy", 0xffffffff);
		biomeColors.put("Frozen", 0xff87ceeb);
		biomeColors.put("Void", 0xff000000);
		biomeColors.put("Flower", 0xffff1493);
		biomeColors.put("Ice", 0xff00ffff);
		biomeColors.put("Soul Sand", 0xff8b4513);
		biomeColors.put("Crimson", 0xffff0000);
		biomeColors.put("Warped", 0xff00ff7f);
		biomeColors.put("Nether", 0xffff4500);
		biomeColors.put("End", 0xff8a2be2);
		biomeColors.put("Mushroom", 0xffff00ff);
		biomeColors.put("Savanna", 0xffff4500);
		biomeColors.put("Badlands", 0xffcd853f);
		biomeColors.put("Swamp", 0xff8b4513);
		biomeColors.put("Shore", 0xff808080);
		biomeColors.put("Taiga", 0xff556b2f);
		biomeColors.put("Mountains", 0xffa9a9a9);
		biomeColors.put("Jungle", 0xff00ff00);
		biomeColors.put("Birch", 0xff7fff00);
		biomeColors.put("Dark Forest", 0xff006400);
		biomeColors.put("Forest", 0xff228b22);
		biomeColors.put("River", 0xff4169e1);
		biomeColors.put("Warm Ocean", 0xff26d9ed);
		biomeColors.put("Cold Ocean", 0xff377de6);
		biomeColors.put("Deep Ocean", 0xff377de6);
		biomeColors.put("Ocean", 0xff34b1eb);
		biomeColors.put("Plains", 0xff00ff00);
		biomeColors.put("Desert", 0xffffff00);
		biomeColors.put("Beach", 0xffffd700);
		biomeColors.put("Hills", 0xff2e8b57);

		int color = 0xffffffff; // default color

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
