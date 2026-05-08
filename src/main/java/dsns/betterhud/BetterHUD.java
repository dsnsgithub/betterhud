package dsns.betterhud;

import dsns.betterhud.mods.*;
import dsns.betterhud.util.BaseMod;
import java.util.ArrayList;
import java.util.Arrays;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
//? if >=1.21.6 {
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
//?} else {
/*import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
*///?}
//? if >=26 {
import net.minecraft.resources.Identifier;
//?} else if >=1.21.6 {
/*import net.minecraft.resources.ResourceLocation;*/
//?}
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class BetterHUD implements ClientModInitializer {

    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("betterhud");

    public static ArrayList<BaseMod> mods = new ArrayList<>(
        Arrays.asList(
            new FPS(),
            new Ping(),
            new Momentum(),
            new Coordinates(),
            new Biome(),
            new Facing(),
            new Time()
        )
    );

    @Override
    public void onInitializeClient() {
        Config.configure();

        BetterHUDGUI betterHUDGUI = new BetterHUDGUI();

        //? if >=26 {
        HudElementRegistry.addLast(
            Identifier.fromNamespaceAndPath("betterhud", "hud"),
            betterHUDGUI::onHudRender
        );
        //?} else if >=1.21.6 {
        /*HudElementRegistry.addLast(
            ResourceLocation.fromNamespaceAndPath("betterhud", "hud"),
            betterHUDGUI::onHudRender
        );*/
        //?} else {
        /*HudRenderCallback.EVENT.register(betterHUDGUI);
        *///?}
        ClientTickEvents.START_CLIENT_TICK.register(betterHUDGUI);
    }
}
