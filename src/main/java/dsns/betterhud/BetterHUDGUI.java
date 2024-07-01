package dsns.betterhud;

import java.util.ArrayList;
import java.util.List;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

import dsns.betterhud.mods.*;
import dsns.betterhud.util.BaseMod;
import dsns.betterhud.util.CustomText;

public class BetterHUDGUI implements HudRenderCallback, ClientTickEvents.StartTick {
	private final MinecraftClient client = MinecraftClient.getInstance();
	private final List<CustomText> leftTextList = new ObjectArrayList<>();
	private final List<CustomText> rightTextList = new ObjectArrayList<>();

	@Override
	public void onStartTick(MinecraftClient client) {
		this.leftTextList.clear();
		this.rightTextList.clear();

		ArrayList<BaseMod> mods = new ArrayList<>();
		mods.add(new FPS());
		mods.add(new Ping());
		mods.add(new Momentum());
		mods.add(new Coordinates());
		mods.add(new Biome());
		mods.add(new Facing());

		for (BaseMod mod : mods) {
			ModSettings modSettings = Config.settings.get(mod.getModID());
			if (!modSettings.enabled)
				continue;

			CustomText modText = mod.onStartTick(client);
			if (modText == null)
				continue;

			if (modSettings.orientation.equals("top-left")) {
				this.leftTextList.add(modText);
			} else {
				this.rightTextList.add(modText);
			}
		}
	}

	@Override
	public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
		if (client.getDebugHud().shouldShowDebugHud())
			return;
		if (client.options.hudHidden)
			return;

		int x = Config.horizontalMargin;
		int y = Config.verticalMargin;

		for (CustomText text : leftTextList) {
			drawString(drawContext, text, x, y);

			y += client.textRenderer.fontHeight + Config.lineHeight;
		}

		y = Config.verticalMargin;
		for (CustomText text : rightTextList) {
			int offset = (client.textRenderer.getWidth(text.text) - 1) + (Config.horizontalPadding * 2) + Config.horizontalMargin;
			x = client.getWindow().getScaledWidth() - offset;
			drawString(drawContext, text, x, y);

			y += client.textRenderer.fontHeight + Config.lineHeight;
		}
	}

	private void drawString(DrawContext drawContext, CustomText text, int x, int y) {
		drawContext.fill(x, y, 
				x + (client.textRenderer.getWidth(text.text) - 1) + (Config.horizontalPadding * 2), 
				y + (client.textRenderer.fontHeight - 1) + (Config.verticalPadding * 2), text.backgroundColor);
		drawContext.drawText(client.textRenderer, text.text, x + Config.horizontalPadding, y + Config.verticalPadding,
				text.color, true);
		
		// drawContext.fill(x - Config.horizontalPadding, y - Config.verticalPadding,
		// 		x + this.client.textRenderer.getWidth(text.text) + Config.horizontalPadding,
		// 		y + this.client.textRenderer.fontHeight + Config.verticalPadding - 1, text.backgroundColor);

		// drawContext.drawText(this.client.textRenderer, text.text, x, y, text.color, true);
	}
}
