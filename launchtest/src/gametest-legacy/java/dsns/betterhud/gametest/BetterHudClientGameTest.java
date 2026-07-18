package dsns.betterhud.gametest;

// Copy of /src/gametest/.../BetterHudClientGameTest.java for Minecraft
// 1.21.4-1.21.11, whose fabric-client-gametest-api-v1 (4.x) names the client
// world accessor getClientWorld() instead of getClientLevel() (5.x, 26.x).
// Keep the two files in sync when changing the test.

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
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

			context.waitTicks(40);
			assertScreenshotSaved(context.takeScreenshot("betterhud-survival-world"));
		}
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
