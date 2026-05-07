package dsns.betterhud.util;

//? if mc >= "26.1" {
import net.minecraft.client.Minecraft;
//?} else {
/*import net.minecraft.client.MinecraftClient;*/
//?}

public interface BaseMod {
    public String getModID();

    public ModSettings getModSettings();

    //? if mc >= "26.1" {
    public CustomText onStartTick(Minecraft client);
    //?} else {
    /*public CustomText onStartTick(MinecraftClient client);*/
    //?}
}
