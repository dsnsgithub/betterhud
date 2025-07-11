package dsns.betterhud;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class BetterHUD implements ClientModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("betterhud");

	@Override
	public void onInitializeClient() {
		Config.configure();

		BetterHUDGUI betterHUDGUI = new BetterHUDGUI();
		HudElementRegistry.addLast(Identifier.of("betterhud", "hud"), betterHUDGUI::onHudRender);
		ClientTickEvents.START_CLIENT_TICK.register(betterHUDGUI);
	}
}