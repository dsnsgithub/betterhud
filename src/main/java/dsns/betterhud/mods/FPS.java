package dsns.betterhud.mods;

import dsns.betterhud.mixin.MinecraftClientAccessor;
import dsns.betterhud.util.BaseMod;
import dsns.betterhud.util.CustomText;
import dsns.betterhud.util.ModSettings;
import net.minecraft.client.MinecraftClient;

public class FPS implements BaseMod {

    @Override
    public String getModID() {
        return "FPS";
    }

    @Override
    public ModSettings getModSettings() {
        return new ModSettings("top-left");
    }

    @Override
    public CustomText onStartTick(MinecraftClient client) {
        int currentFPS = MinecraftClientAccessor.getCurrentFPS();

        return new CustomText(currentFPS + " FPS", getModSettings());
    }
}
