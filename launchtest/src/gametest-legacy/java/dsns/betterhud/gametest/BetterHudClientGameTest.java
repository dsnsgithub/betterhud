package dsns.betterhud.gametest;

// Copy of /src/gametest/.../BetterHudClientGameTest.java for Minecraft
// 1.21.4-1.21.11, whose fabric-client-gametest-api-v1 (4.x) names the client
// world accessor getClientWorld() instead of getClientLevel() (5.x, 26.x).
// Keep the two files in sync when changing the test.

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;

@SuppressWarnings("UnstableApiUsage")
public class BetterHudClientGameTest implements FabricClientGameTest {
	@Override
	public void runTest(ClientGameTestContext context) {
		assertScreenshotSaved(context.takeScreenshot("betterhud-title-screen"));

		try (TestSingleplayerContext singleplayer = context.worldBuilder()
				.adjustSettings(creator -> creator.setGameMode(WorldCreationUiState.SelectedGameMode.SURVIVAL))
				.create()) {
			singleplayer.getClientWorld().waitForChunksRender();

			// Let the world tick a little with the HUD rendering before capturing evidence.
			context.waitTicks(40);
			assertScreenshotSaved(context.takeScreenshot("betterhud-survival-world"));

			// Open the BetterHUD settings menu and capture it too, so the
			// settings configuration can be validated per version.
			context.runOnClient(client -> client.setScreen(createSettingsScreen(client)));
			context.waitTicks(20);
			assertScreenshotSaved(context.takeScreenshot("betterhud-settings-menu"));
			context.runOnClient(client -> client.setScreen(null));
		}
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

	private static void assertScreenshotSaved(Path screenshot) {
		try {
			if (!Files.isRegularFile(screenshot) || Files.size(screenshot) == 0) {
				throw new AssertionError("Screenshot was not saved: " + screenshot);
			}
		} catch (IOException e) {
			throw new UncheckedIOException("Could not verify screenshot " + screenshot, e);
		}
	}
}
