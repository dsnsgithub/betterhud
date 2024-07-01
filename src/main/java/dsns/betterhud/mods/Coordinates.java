package dsns.betterhud.mods;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import dsns.betterhud.util.BaseMod;
import dsns.betterhud.util.CustomText;

public class Coordinates implements BaseMod {
	@Override
	public String getModID() {
		return "Coordinates";
	}

	@Override
	public CustomText onStartTick(MinecraftClient client) {
		PlayerEntity player = client.player;

		if (player == null)
			return null;

		String roundX = String.format("%.2f", player.getX());
		String roundY = String.format("%.2f", player.getY());
		String roundZ = String.format("%.2f", player.getZ());

		return new CustomText(roundX + ", " + roundY + ", " + roundZ);
	}
}
