package dsns.betterhud.launchtest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.stream.Stream;

import com.mojang.blaze3d.pipeline.RenderTarget;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.renderer.GameRenderer;

/**
 * Drives the launch test on every supported Minecraft version: the client is
 * started with {@code --quickPlaySingleplayer ci-world} (see the launchtest
 * Gradle project), joins the pre-generated survival world, screenshots it with
 * the HUD active, and exits cleanly. A crash anywhere on the way fails the run.
 */
public class LaunchTestClient implements ClientModInitializer {
	private static final String SCREENSHOT_NAME = "betterhud-survival-world.png";
	// 200 ticks (10s) gives the software renderer on CI time to draw chunks;
	// the screenshot is written asynchronously, so leave a few more seconds
	// before renaming it and stopping the client.
	private static final int SCREENSHOT_TICK = 200;
	private static final int STOP_TICK = 300;
	private static final int WORLD_JOIN_TIMEOUT_TICKS = 2400;

	private int totalTicks;
	private int worldTicks = -1;

	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			totalTicks++;
			if (worldTicks < 0 && totalTicks >= WORLD_JOIN_TIMEOUT_TICKS) {
				fail("the game never joined the test world");
			}
			if (client.player == null || client.level == null) {
				return;
			}

			worldTicks++;
			if (worldTicks == SCREENSHOT_TICK) {
				Screenshot.grab(client.gameDirectory, mainRenderTarget(client), message -> {});
			} else if (worldTicks == STOP_TICK) {
				renameScreenshot(client.gameDirectory);
				client.stop();
			}
		});
	}

	// Minecraft.getMainRenderTarget() moved to GameRenderer in MC 26.2, so no
	// direct call compiles against every supported version. This mod only runs
	// in production, where 26.x uses plain Mojang names and 1.21.x is remapped
	// to version-stable intermediary names, so resolve the accessor under
	// whichever name the running game has.
	private static RenderTarget mainRenderTarget(Minecraft client) {
		for (String name : new String[] {"getMainRenderTarget", "method_1522"}) {
			try {
				return (RenderTarget) Minecraft.class.getMethod(name).invoke(client);
			} catch (ReflectiveOperationException e) {
				// try the next name
			}
		}
		try {
			return (RenderTarget) GameRenderer.class.getMethod("mainRenderTarget").invoke(client.gameRenderer);
		} catch (ReflectiveOperationException e) {
			throw new IllegalStateException("Could not resolve the main render target", e);
		}
	}

	// The Screenshot.grab overloads taking a file name differ across versions;
	// only the auto-named one is stable, so rename its output afterwards.
	private static void renameScreenshot(File gameDirectory) {
		Path dir = gameDirectory.toPath().resolve("screenshots");
		try (Stream<Path> files = Files.list(dir)) {
			Path newest = files.filter(f -> f.getFileName().toString().endsWith(".png"))
					.max(Comparator.comparingLong(f -> f.toFile().lastModified()))
					.orElseThrow(() -> new IOException("no screenshot in " + dir));
			Files.move(newest, dir.resolve(SCREENSHOT_NAME), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
			fail("the screenshot was not saved");
		}
	}

	private static void fail(String reason) {
		System.err.println("betterhud launch test failed: " + reason);
		System.exit(1);
	}
}
