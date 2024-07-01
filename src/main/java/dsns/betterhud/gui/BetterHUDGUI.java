package dsns.betterhud.gui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;

class CustomText {
	public Text text;
	public int color; // colors are in ARGB format
	public int backgroundColor; // colors are in ARGB format

	public CustomText(Text text, int color, int backgroundColor) {
		this.text = text;
		this.color = color;
		this.backgroundColor = backgroundColor;
	}

	public CustomText(Text text) {
		this(text, 0xffffffff, 0x88000000);
	}

	public CustomText(Text text, int color) {
		this(text, color, 0x88000000);
	}
}

public class BetterHUDGUI implements HudRenderCallback, ClientTickEvents.StartTick {
	private final MinecraftClient client = MinecraftClient.getInstance();
	private final List<CustomText> leftTextList = new ObjectArrayList<>();
	private final List<CustomText> rightTextList = new ObjectArrayList<>();

	@Override
	public void onStartTick(MinecraftClient client) {
		this.leftTextList.clear();
		this.rightTextList.clear();

		int currentFPS = MinecraftClientAccessor.getCurrentFPS();
		Text fpsText = Text.literal(currentFPS + " FPS");
		this.leftTextList.add(new CustomText(fpsText));

		PlayerEntity player = client.player;

		if (player != null) {
			this.leftTextList.add(new CustomText(Text
					.literal(client.getNetworkHandler().getPlayerListEntry(player.getUuid()).getLatency() + " ms")));

			String roundX = String.format("%.2f", player.getX());
			String roundY = String.format("%.2f", player.getY());
			String roundZ = String.format("%.2f", player.getZ());

			Text coordsText = Text.literal(roundX + ", " + roundY + ", " + roundZ);
			this.rightTextList.add(new CustomText(coordsText));

			Optional<RegistryKey<Biome>> biome = client.world.getBiome(player.getBlockPos()).getKey();
			if (biome.isPresent()) {
				String biomeString = BetterHUDGUI.formatSnakeCase(biome.get().getValue().getPath());

				Map<String, Integer> biomeColors = new HashMap<>();
				biomeColors.put("Ocean", 0x0000ff);
				biomeColors.put("Plains", 0x00ff00);
				biomeColors.put("Desert", 0xffff00);
				biomeColors.put("Mountains", 0xa9a9a9);
				biomeColors.put("Forest", 0x228b22);
				biomeColors.put("Taiga", 0x556b2f);
				biomeColors.put("Swamp", 0x8b4513);
				biomeColors.put("River", 0x4169e1);
				biomeColors.put("Nether", 0xff4500);
				biomeColors.put("End", 0x8a2be2);
				biomeColors.put("Frozen", 0x87ceeb);
				biomeColors.put("Snowy", 0xffffff);
				biomeColors.put("Mushroom", 0xff00ff);
				biomeColors.put("Beach", 0xffd700);
				biomeColors.put("Hills", 0x2e8b57);
				biomeColors.put("Jungle", 0x00ff00);
				biomeColors.put("Shore", 0x808080);
				biomeColors.put("Birch", 0x7fff00);
				biomeColors.put("Dark Forest", 0x006400);
				biomeColors.put("Savanna", 0xff4500);
				biomeColors.put("Badlands", 0xcd853f);
				biomeColors.put("Void", 0x000000);
				biomeColors.put("Flower", 0xff1493);
				biomeColors.put("Ice", 0x00ffff);
				biomeColors.put("Bamboo", 0x32cd32);
				biomeColors.put("Soul Sand", 0x8b4513);
				biomeColors.put("Crimson", 0xff0000);
				biomeColors.put("Warped", 0x00ff7f);

				int color = 0xffffff; // default color

				for (Map.Entry<String, Integer> entry : biomeColors.entrySet()) {
					if (biomeString.contains(entry.getKey())) {
						color = entry.getValue();
						break;
					}
				}

				this.rightTextList.add(new CustomText(Text.literal(biomeString), color));

				this.rightTextList.add(new CustomText(Text.literal(
						BetterHUDGUI.formatSnakeCase(player.getHorizontalFacing().getName()))));

				Vec3d currentPosition = player.getPos();
				double travelledX = currentPosition.x - player.prevX;
				double travelledZ = currentPosition.z - player.prevZ;
				double currentSpeed = MathHelper.sqrt((float) (travelledX * travelledX + travelledZ * travelledZ))
						/ 0.05F;
				this.rightTextList.add(new CustomText(Text.literal(String.format("%.2f m/s", currentSpeed))));
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

		for (CustomText text : this.leftTextList) {
			drawString(drawContext, text, x, y);

			y += this.client.textRenderer.fontHeight + 4;
		}

		y = 3;
		for (CustomText text : this.rightTextList) {
			int offset = this.client.textRenderer.getWidth(text.text) + 4;
			x = this.client.getWindow().getScaledWidth() - offset;
			drawString(drawContext, text, x, y);

			y += this.client.textRenderer.fontHeight + 4;
		}
	}

	private void drawString(DrawContext drawContext, CustomText text, int x, int y) {
		// colors are in ARGB format
		// int textColor = 0xffffffff;
		// int backgroundColor = 0x88000000;
		int verticalPadding = 2;
		int horizontalPadding = 4;

		drawContext.fill(x - horizontalPadding, y - verticalPadding,
				x + this.client.textRenderer.getWidth(text.text) + horizontalPadding - 1,
				y + this.client.textRenderer.fontHeight + verticalPadding - 1, text.backgroundColor);

		drawContext.drawText(this.client.textRenderer, text.text, x, y, text.color, true);
	}

	public static String formatSnakeCase(String biomeName) {
		// Split the string by underscores
		String[] words = biomeName.split("_");

		// Capitalize each word
		for (int i = 0; i < words.length; i++) {
			words[i] = words[i].substring(0, 1).toUpperCase() + words[i].substring(1).toLowerCase();
		}

		// Join the words with spaces
		return String.join(" ", words);
	}

}
