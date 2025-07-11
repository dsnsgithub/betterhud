package dsns.betterhud;

import java.util.ArrayList;
import java.util.List;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

import dsns.betterhud.mods.*;
import dsns.betterhud.util.BaseMod;
import dsns.betterhud.util.CustomText;

public class BetterHUDGUI implements ClientTickEvents.StartTick {
	private final MinecraftClient client = MinecraftClient.getInstance();
	private final List<CustomText> topLeftText = new ObjectArrayList<>();
	private final List<CustomText> topRightText = new ObjectArrayList<>();
	private final List<CustomText> bottomLeftList = new ObjectArrayList<>();
	private final List<CustomText> bottomRightText = new ObjectArrayList<>();
	private final List<CustomText> customPositionText = new ObjectArrayList<>();

	@Override
	public void onStartTick(MinecraftClient client) {
		this.topLeftText.clear();
		this.topRightText.clear();
		this.bottomLeftList.clear();
		this.bottomRightText.clear();
		this.customPositionText.clear();

		ArrayList<BaseMod> mods = new ArrayList<>();
		mods.add(new FPS());
		mods.add(new Ping());
		mods.add(new Momentum());
		mods.add(new Coordinates());
		mods.add(new Biome());
		mods.add(new Facing());
		mods.add(new Time());

		for (BaseMod mod : mods) {
			ModSettings modSettings = Config.settings.get(mod.getModID());
			if (!modSettings.enabled)
				continue;

			CustomText modText = mod.onStartTick(client);
			if (modText == null)
				continue;

			if (modSettings.customPosition) {
				modText.customPosition = true;
				modText.customX = modSettings.customX;
				modText.customY = modSettings.customY;
				this.customPositionText.add(modText);
			} else if (modSettings.orientation.equals("top-left")) {
				this.topLeftText.add(modText);
			} else if (modSettings.orientation.equals("top-right")) {
				this.topRightText.add(modText);
			} else if (modSettings.orientation.equals("bottom-left")) {
				this.bottomLeftList.add(modText);
			} else if (modSettings.orientation.equals("bottom-right")) {
				this.bottomRightText.add(modText);
			}
		}
	}

	public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
		if (client.getDebugHud().shouldShowDebugHud())
			return;
		if (client.options.hudHidden)
			return;

		int x = Config.horizontalMargin;
		int y = Config.verticalMargin;

		for (CustomText text : topLeftText) {
			drawString(drawContext, text, x, y);

			y += (client.textRenderer.fontHeight - 1) + (Config.verticalPadding * 2) + Config.lineHeight;
		}

		y = client.getWindow().getScaledHeight() - Config.verticalMargin;

		for (CustomText text : bottomLeftList) {
			y -= (client.textRenderer.fontHeight - 1) + (Config.verticalPadding * 2);
			drawString(drawContext, text, x, y);
			y -= Config.lineHeight;
		}

		y = Config.verticalMargin;
		for (CustomText text : topRightText) {
			int offset = (client.textRenderer.getWidth(text.text) - 1) + (Config.horizontalPadding * 2)
					+ Config.horizontalMargin;
			x = client.getWindow().getScaledWidth() - offset;
			drawString(drawContext, text, x, y);

			y += (client.textRenderer.fontHeight - 1) + (Config.verticalPadding * 2) + Config.lineHeight;
		}

		y = client.getWindow().getScaledHeight() - Config.verticalMargin;
		for (CustomText text : bottomRightText) {
			int offset = (client.textRenderer.getWidth(text.text) - 1) + (Config.horizontalPadding * 2)
					+ Config.horizontalMargin;
			x = client.getWindow().getScaledWidth() - offset;

			y -= (client.textRenderer.fontHeight - 1) + (Config.verticalPadding * 2);

			drawString(drawContext, text, x, y);

			y -= Config.lineHeight;
		}

		for (CustomText text : customPositionText) {
			float xPercent = text.customX / 100.0f;
			float yPercent = text.customY / 100.0f;

			int maxX = client.getWindow().getScaledWidth() - (Config.horizontalPadding * 2)
					- (client.textRenderer.getWidth(text.text) - 1);
			int maxY = client.getWindow().getScaledHeight() - (Config.verticalPadding * 2)
					- (client.textRenderer.fontHeight - 1);

			int scaledX = (int) (xPercent * maxX);
			int scaledY = (int) (yPercent * maxY);

			drawString(drawContext, text, scaledX, scaledY);
		}
	}

	private void drawString(DrawContext drawContext, CustomText text, int x, int y) {
		drawContext.fill(x, y,
				x + (client.textRenderer.getWidth(text.text) - 1) + (Config.horizontalPadding * 2),
				y + (client.textRenderer.fontHeight - 1) + (Config.verticalPadding * 2), text.backgroundColor);

		drawContext.drawText(client.textRenderer, text.text, x + Config.horizontalPadding, y + Config.verticalPadding,
				text.color, true);
	}
}
