package dsns.betterhud.mods;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
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

		if (player == null || player.getPos() == null)
			return null;

		Vec3d currentPosition = player.getPos();
		double travelledX = currentPosition.x - player.lastRenderX;
		double travelledZ = currentPosition.z - player.lastRenderZ;
		double currentSpeed = MathHelper.sqrt((float) (travelledX * travelledX + travelledZ * travelledZ)) / 0.05F;

		return new CustomText(String.format("%.2f m/s", currentSpeed));
	}
}
