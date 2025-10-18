package dsns.betterhud.mods;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import dsns.betterhud.util.BaseMod;
import dsns.betterhud.util.CustomText;

public class Momentum implements BaseMod {
	@Override
	public String getModID() {
		return "Momentum";
	}

	@Override
	public CustomText onStartTick(MinecraftClient client) {
		PlayerEntity player = client.player;

		if (player == null)
			return null;

		double travelledX = player.getX() - player.lastRenderX;
		double travelledZ = player.getZ() - player.lastRenderZ;
		double currentSpeed = MathHelper.sqrt((float) (travelledX * travelledX + travelledZ * travelledZ)) / 0.05F;

		return new CustomText(String.format("%.2f m/s", currentSpeed));
	}
}
