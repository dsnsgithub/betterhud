package dsns.betterhud.gametest;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.TestInput;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import org.lwjgl.glfw.GLFW;

@SuppressWarnings("UnstableApiUsage")
public class BetterHudClientGameTest implements FabricClientGameTest {
	@Override
	public void runTest(ClientGameTestContext context) {
		assertScreenshotSaved(context.takeScreenshot("betterhud-title-screen"));

		try (TestSingleplayerContext singleplayer = context.worldBuilder()
				.adjustSettings(creator -> creator.setGameMode(WorldCreationUiState.SelectedGameMode.SURVIVAL))
				.create()) {
			singleplayer.getClientLevel().waitForChunksRender();

			context.waitTicks(40);
			assertScreenshotSaved(context.takeScreenshot("betterhud-survival-world"));

			exerciseHudEditor(context);
		}
	}

	private static void exerciseHudEditor(ClientGameTestContext context) {
		TestInput input = context.getInput();

		input.pressKey(GLFW.GLFW_KEY_RIGHT_SHIFT);
		context.waitTicks(5);
		assertScreenshotSaved(context.takeScreenshot("betterhud-hud-editor"));

		double guiScale = context.computeOnClient(client -> (double) client.getWindow().getGuiScale());

		input.setCursorPos(12.0 * guiScale, 8.0 * guiScale);
		input.holdMouse(GLFW.GLFW_MOUSE_BUTTON_LEFT);
		for (int i = 0; i < 10; i++) {
			input.moveCursor(8.0 * guiScale, 5.0 * guiScale);
			context.waitTick();
		}
		input.releaseMouse(GLFW.GLFW_MOUSE_BUTTON_LEFT);
		assertScreenshotSaved(context.takeScreenshot("betterhud-hud-editor-dragged"));

		input.pressKey(GLFW.GLFW_KEY_ESCAPE);
		context.waitTicks(2);

		Properties config = loadConfig();
		if (!"custom".equals(config.getProperty("FPS.Position"))) {
			throw new AssertionError(
					"Dragging in the HUD editor did not give the FPS element a custom position: " + config);
		}
		double customX = Double.parseDouble(config.getProperty("FPS.Custom X"));
		double customY = Double.parseDouble(config.getProperty("FPS.Custom Y"));
		if (customX <= 1 || customY <= 1) {
			throw new AssertionError(
					"Dragging in the HUD editor did not move the FPS element: x=" + customX + "% y=" + customY + "%");
		}

		input.pressKey(GLFW.GLFW_KEY_RIGHT_SHIFT);
		context.waitTicks(5);
		input.setCursorPos(92.0 * guiScale, 58.0 * guiScale);
		input.holdMouse(GLFW.GLFW_MOUSE_BUTTON_LEFT);
		for (int i = 0; i < 10; i++) {
			input.moveCursor(-8.0 * guiScale, -5.0 * guiScale);
			context.waitTick();
		}
		input.releaseMouse(GLFW.GLFW_MOUSE_BUTTON_LEFT);
		context.waitTicks(2);
		input.pressKey(GLFW.GLFW_KEY_ESCAPE);
		context.waitTicks(2);

		config = loadConfig();
		if (!"top-left".equals(config.getProperty("FPS.Position"))) {
			throw new AssertionError(
					"Dragging into the corner did not dock the FPS element back: " + config);
		}
	}

	private static Properties loadConfig() {
		Path config = FabricLoader.getInstance().getConfigDir().resolve("betterhud.properties");
		Properties properties = new Properties();
		try (InputStream in = Files.newInputStream(config)) {
			properties.load(in);
		} catch (IOException e) {
			throw new UncheckedIOException("Could not read " + config, e);
		}
		return properties;
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
