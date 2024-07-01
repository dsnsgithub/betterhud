package dsns.betterhud.util;

import net.minecraft.client.MinecraftClient;

public interface BaseMod {
	public String getModID();

	public CustomText onStartTick(MinecraftClient client);
}
