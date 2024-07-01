package dsns.betterhud.mods;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import dsns.betterhud.util.BaseMod;
import dsns.betterhud.util.CustomText;

public class Ping implements BaseMod {
	@Override
	public String getModID() {
		return "Ping";
	}

	@Override
	public CustomText onStartTick(MinecraftClient client) {
		PlayerEntity player = client.player;

		if (player == null)
			return null;

		return new CustomText(client.getNetworkHandler().getPlayerListEntry(player.getUuid()).getLatency() + " ms");
	}
}
