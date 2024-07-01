package dsns.betterhud.mods;

import net.minecraft.client.MinecraftClient;
import dsns.betterhud.util.BaseMod;
import dsns.betterhud.util.CustomText;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Time implements BaseMod {
	@Override
	public String getModID() {
		return "Time";
	}

	@Override
	public CustomText onStartTick(MinecraftClient client) {
		LocalTime currentTime = LocalTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
		String formattedTime = currentTime.format(formatter);

		return new CustomText(formattedTime);
	}
}
