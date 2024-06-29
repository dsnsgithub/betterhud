package dsns.betterhud.gui;

import java.util.List;
import java.util.Optional;

import dsns.betterhud.mixin.MinecraftClientAccessor;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.world.biome.Biome;

public class BetterHUDGUI implements HudRenderCallback, ClientTickEvents.StartTick {
	private final MinecraftClient client = MinecraftClient.getInstance();
	private final List<Text> leftTextList = new ObjectArrayList<>();
	private final List<Text> rightTextList = new ObjectArrayList<>();

	@Override
	public void onStartTick(MinecraftClient client) {
		this.leftTextList.clear();
		this.rightTextList.clear();

		int currentFPS = MinecraftClientAccessor.getCurrentFPS();
		Text fpsText = Text.literal(currentFPS + " FPS");
		this.leftTextList.add(fpsText);

		PlayerEntity player = client.player;

		if (player != null) {
			this.leftTextList.add(Text.literal(client.getNetworkHandler().getPlayerListEntry(player.getUuid()).getLatency() + " ms"));

			String roundX = String.format("%.2f", player.getX());
			String roundY = String.format("%.2f", player.getY());
			String roundZ = String.format("%.2f", player.getZ());

			Text coordsText = Text.literal(roundX + ", " + roundY + ", " + roundZ);
			this.rightTextList.add(coordsText);

			Optional<RegistryKey<Biome>> biome = client.world.getBiome(player.getBlockPos()).getKey();
			if (biome.isPresent()) {
				Text biomeText = Text.literal(BetterHUDGUI.capitalise(biome.get().getValue().getPath()));

				this.rightTextList.add(biomeText);
			}
		}
	}

	@Override
	public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
		if (this.client.getDebugHud().shouldShowDebugHud())
			return;
		if (this.client.options.hudHidden)
			return;

		int x = 5;
		int y = 3;

		for (Text text : this.leftTextList) {
			drawString(drawContext, text, x, y);

			y += this.client.textRenderer.fontHeight + 4;
		}

		y = 3;
		for (Text text : this.rightTextList) {
			int offset = this.client.textRenderer.getWidth(text) + 4;
			x = this.client.getWindow().getScaledWidth() - offset;
			drawString(drawContext, text, x, y);

			y += this.client.textRenderer.fontHeight + 4;
		}
	}

	private void drawString(DrawContext drawContext, Text text, int x, int y) {
		// colors are in ARGB format
		int textColor = 0xffffffff;
		int backgroundColor = 0x88000000;
		int verticalPadding = 2;
		int horizontalPadding = 4;

		drawContext.fill(x - horizontalPadding, y - verticalPadding, x + this.client.textRenderer.getWidth(text) + horizontalPadding - 1,
				y + this.client.textRenderer.fontHeight + verticalPadding - 1, backgroundColor);

		drawContext.drawText(this.client.textRenderer, text, x, y, textColor, true);
	}

	public static String capitalise(String str) {
		// Capitalise first letter of a String
		if (str == null)
			return null;
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}
}
