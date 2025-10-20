package dsns.betterhud.mods;

import dsns.betterhud.util.BaseMod;
import dsns.betterhud.util.CustomText;
import dsns.betterhud.util.ModSettings;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import net.minecraft.client.MinecraftClient;

public class Time implements BaseMod {

    @Override
    public String getModID() {
        return "Time";
    }

    @Override
    public ModSettings getModSettings() {
        return new ModSettings("bottom-right");
    }

    @Override
    public CustomText onStartTick(MinecraftClient client) {
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
        String formattedTime = currentTime.format(formatter);

        return new CustomText(formattedTime, getModSettings());
    }
}
