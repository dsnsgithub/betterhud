package dsns.betterhud.mods;

import dsns.betterhud.mixin.MinecraftClientAccessor;
import dsns.betterhud.util.BaseMod;
import dsns.betterhud.util.CustomText;
import dsns.betterhud.util.ModSettings;
import net.minecraft.client.MinecraftClient;

public class FPS implements BaseMod {

    private static final ModSettings SETTINGS = new ModSettings("top-left");

    @Override
    public String getModID() {
        return "FPS";
    }

    @Override
    public ModSettings getModSettings() {
        return SETTINGS;
    }

    @Override
    public CustomText onStartTick(MinecraftClient client) {
        int currentFPS = MinecraftClientAccessor.getCurrentFPS();

        return new CustomText(currentFPS + " FPS", getModSettings());
    }
}
