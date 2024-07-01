package dsns.betterhud.mods;

import net.minecraft.client.MinecraftClient;
import dsns.betterhud.mixin.MinecraftClientAccessor;
import dsns.betterhud.util.BaseMod;
import dsns.betterhud.util.CustomText;

public class FPS implements BaseMod {
	@Override
	public String getModID() {
		return "FPS";
	}

	@Override
	public CustomText onStartTick(MinecraftClient client) {
		int currentFPS = MinecraftClientAccessor.getCurrentFPS();

		return new CustomText(currentFPS + " FPS");
	}
}
