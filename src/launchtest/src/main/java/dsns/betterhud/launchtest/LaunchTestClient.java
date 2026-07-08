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

public class LaunchTestClient implements ClientModInitializer {
	private static final String SCREENSHOT_NAME = "betterhud-survival-world.png";
	private static final String[] MINECRAFT_RENDER_TARGET_GETTERS = {"getMainRenderTarget", "method_1522"};
	private static final String GAME_RENDERER_RENDER_TARGET_GETTER = "mainRenderTarget";
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

	private static RenderTarget mainRenderTarget(Minecraft client) {
		for (String getter : MINECRAFT_RENDER_TARGET_GETTERS) {
			try {
				return (RenderTarget) Minecraft.class.getMethod(getter).invoke(client);
			} catch (ReflectiveOperationException e) {
				continue;
			}
		}
		try {
			return (RenderTarget) GameRenderer.class.getMethod(GAME_RENDERER_RENDER_TARGET_GETTER).invoke(client.gameRenderer);
		} catch (ReflectiveOperationException e) {
			throw new IllegalStateException("Could not resolve the main render target", e);
		}
	}

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
