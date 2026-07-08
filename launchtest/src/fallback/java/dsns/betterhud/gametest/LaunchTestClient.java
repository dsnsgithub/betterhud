package dsns.betterhud.gametest;

import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;

/**
 * Fallback launch test driver for Minecraft versions whose Fabric API predates
 * the client gametest module (before 1.21.4).
 *
 * <p>When the run is started with {@code --quickPlaySingleplayer ci-world} and
 * {@code -Dbetterhud.launchtest.expectWorld=true} (see the launchtest Gradle
 * project), the game joins the pre-generated survival world; once it has ticked
 * long enough for chunks to render with the HUD active, this mod screenshots it,
 * opens and screenshots the BetterHUD settings menu, and schedules a clean
 * shutdown, making the client exit with code 0. Without a pre-generated world it
 * screenshots the title screen and shuts down there instead. A crash anywhere on
 * the way fails the run.
 */
public class LaunchTestClient implements ClientModInitializer {
	private static final boolean EXPECT_WORLD = Boolean.getBoolean("betterhud.launchtest.expectWorld");

	// 200 ticks (10s) gives the software renderer on CI time to draw chunks.
	private static final int WORLD_SCREENSHOT_TICK = 200;
	private static final int SETTINGS_OPEN_TICK = 220;
	private static final int SETTINGS_SCREENSHOT_TICK = 260;
	private static final int WORLD_STOP_TICK = 280;
	private static final int TITLE_SCREENSHOT_TICK = 40;
	private static final int TITLE_STOP_TICK = 80;
	// 2400 ticks (2min) is plenty to join the pre-generated flat world; bail
	// out with a failure instead of hanging until the CI job timeout.
	private static final int WORLD_JOIN_TIMEOUT_TICKS = 2400;

	private int totalTicks;
	private int worldTicks = -1;
	private int titleTicks = -1;

	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			totalTicks++;

			if (EXPECT_WORLD && worldTicks < 0 && totalTicks >= WORLD_JOIN_TIMEOUT_TICKS) {
				System.err.println("betterhud launch test: the game never joined the pre-generated world");
				System.exit(1);
			}

			if (client.player != null && client.level != null) {
				worldTicks++;
				if (worldTicks == WORLD_SCREENSHOT_TICK) {
					takeScreenshot(client, "betterhud-survival-world");
				} else if (worldTicks == SETTINGS_OPEN_TICK) {
					client.setScreen(createSettingsScreen(client));
				} else if (worldTicks == SETTINGS_SCREENSHOT_TICK) {
					takeScreenshot(client, "betterhud-settings-menu");
				} else if (worldTicks == WORLD_STOP_TICK) {
					client.stop();
				}
			} else if (!EXPECT_WORLD && client.screen instanceof TitleScreen) {
				titleTicks++;
				if (titleTicks == TITLE_SCREENSHOT_TICK) {
					takeScreenshot(client, "betterhud-title-screen");
				} else if (titleTicks == TITLE_STOP_TICK) {
					client.stop();
				}
			}
		});
	}

	/**
	 * Builds BetterHUD's settings screen the same way Mod Menu does: through the
	 * config screen factory that the mod registers via its "modmenu" entrypoint.
	 */
	private static Screen createSettingsScreen(Minecraft client) {
		ModMenuApi entrypoint = FabricLoader.getInstance()
				.getEntrypointContainers("modmenu", ModMenuApi.class).stream()
				.filter(container -> "betterhud".equals(container.getProvider().getMetadata().getId()))
				.findFirst()
				.orElseThrow(() -> new AssertionError("BetterHUD does not register a \"modmenu\" entrypoint"))
				.getEntrypoint();
		Screen screen = entrypoint.getModConfigScreenFactory().create(client.screen);
		if (screen == null) {
			throw new AssertionError("BetterHUD did not provide a settings screen (is Cloth Config loaded?)");
		}
		return screen;
	}

	private static void takeScreenshot(Minecraft client, String name) {
		Screenshot.grab(client.gameDirectory, name + ".png", client.getMainRenderTarget(), message -> {});
	}
}
