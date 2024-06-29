package dsns.betterhud;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dsns.betterhud.gui.BetterHUDGUI;

@Environment(EnvType.CLIENT)
public class BetterHUD implements ClientModInitializer  {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("betterhud");

	@Override
	public void onInitializeClient() {
		BetterHUDGUI betterHUDGUI = new BetterHUDGUI();
		HudRenderCallback.EVENT.register(betterHUDGUI);
		ClientTickEvents.START_CLIENT_TICK.register(betterHUDGUI);
	}
}