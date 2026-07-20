package dsns.betterhud;

import dsns.betterhud.mods.*;
import dsns.betterhud.util.BaseMod;
import java.util.ArrayList;
import java.util.Arrays;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
//? if >=26 {
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
//?} else {
/*import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;*/
//?}
//? if >=1.21.6 {
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
//?} else {
/*import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
*///?}
import net.minecraft.client.KeyMapping;
//? if >=26 {
import net.minecraft.resources.Identifier;
//?} else if >=1.21.6 {
/*import net.minecraft.resources.ResourceLocation;*/
//?}
import org.lwjgl.glfw.GLFW;
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

    public static KeyMapping openEditorKey;

    @Override
    public void onInitializeClient() {
        Config.configure();

        //? if >=26 {
        openEditorKey = KeyMappingHelper.registerKeyMapping(
        //?} else {
        /*openEditorKey = KeyBindingHelper.registerKeyBinding(*/
        //?}
            new KeyMapping(
                "key.betterhud.open_editor",
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                //? if >=26 {
                KeyMapping.Category.register(
                    Identifier.fromNamespaceAndPath("betterhud", "main")
                )
                //?} else if >=1.21.9 {
                /*KeyMapping.Category.register(
                    ResourceLocation.fromNamespaceAndPath("betterhud", "main")
                )*/
                //?} else {
                /*"key.categories.betterhud"*/
                //?}
            )
        );

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
