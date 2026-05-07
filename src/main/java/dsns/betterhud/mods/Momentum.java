package dsns.betterhud.mods;

import dsns.betterhud.util.BaseMod;
import dsns.betterhud.util.CustomText;
import dsns.betterhud.util.ModSettings;
//? if mc >= "26.1" {
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
//?} else {
/*import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;*/
//?}

public class Momentum implements BaseMod {

    private static final ModSettings SETTINGS = new ModSettings("top-left");

    @Override
    public String getModID() {
        return "Momentum";
    }

    @Override
    public ModSettings getModSettings() {
        return SETTINGS;
    }

    @Override
    //? if mc >= "26.1" {
    public CustomText onStartTick(Minecraft client) {
        Player player = client.player;

        if (player == null) return null;

        double travelledX = player.getX() - player.xOld;
        double travelledZ = player.getZ() - player.zOld;
        double currentSpeed =
            Mth.sqrt((float) (travelledX * travelledX + travelledZ * travelledZ)) / 0.05F;

        return new CustomText(String.format("%.2f m/s", currentSpeed), getModSettings());
    }
    //?} else {
    /*public CustomText onStartTick(MinecraftClient client) {
        PlayerEntity player = client.player;

        if (player == null) return null;

        double travelledX = player.getX() - player.lastRenderX;
        double travelledZ = player.getZ() - player.lastRenderZ;
        double currentSpeed =
            MathHelper.sqrt((float) (travelledX * travelledX + travelledZ * travelledZ)) / 0.05F;

        return new CustomText(String.format("%.2f m/s", currentSpeed), getModSettings());
    }*/
    //?}
}
