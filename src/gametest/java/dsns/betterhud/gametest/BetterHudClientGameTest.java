package dsns.betterhud.gametest;

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
		TestSingleplayerContext singleplayer = context.worldBuilder()
			.adjustSettings(creator -> creator.setGameMode(WorldCreationUiState.SelectedGameMode.SURVIVAL))
			.create();

		singleplayer.getClientLevel().waitForChunksRender();

		context.waitTicks(40);
		context.takeScreenshot("betterhud-survival-world");
	}
}
