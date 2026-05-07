package dsns.betterhud.mods;

//? if mc < "26.1"
/*import dsns.betterhud.mixin.MinecraftClientAccessor;*/
import dsns.betterhud.util.BaseMod;
import dsns.betterhud.util.CustomText;
import dsns.betterhud.util.ModSettings;
//? if mc >= "26.1" {
import net.minecraft.client.Minecraft;
//?} else {
/*import net.minecraft.client.MinecraftClient;*/
//?}

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
    //? if mc >= "26.1" {
    public CustomText onStartTick(Minecraft client) {
        int currentFPS = client.getFps();
        return new CustomText(currentFPS + " FPS", getModSettings());
    }
    //?} else {
    /*public CustomText onStartTick(MinecraftClient client) {
        int currentFPS = MinecraftClientAccessor.getCurrentFPS();
        return new CustomText(currentFPS + " FPS", getModSettings());
    }*/
    //?}
}
