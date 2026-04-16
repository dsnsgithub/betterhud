package dsns.betterhud;

import dsns.betterhud.util.BaseMod;
import dsns.betterhud.util.CustomText;
import dsns.betterhud.util.ModSettings;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class BetterHUDGUI implements ClientTickEvents.StartTick {

    public static int verticalPadding = 4;
    public static int horizontalPadding = 4;

    public static int verticalMargin = 1;
    public static int horizontalMargin = 1;

    public static int lineHeight = 1;

    private final Minecraft client = Minecraft.getInstance();
    private final List<CustomText> topLeftText = new ObjectArrayList<>();
    private final List<CustomText> topRightText = new ObjectArrayList<>();
    private final List<CustomText> bottomLeftList = new ObjectArrayList<>();
    private final List<CustomText> bottomRightText = new ObjectArrayList<>();
    private final List<CustomText> customPositionText = new ObjectArrayList<>();

    @Override
    public void onStartTick(Minecraft client) {
        this.topLeftText.clear();
        this.topRightText.clear();
        this.bottomLeftList.clear();
        this.bottomRightText.clear();
        this.customPositionText.clear();

        for (BaseMod mod : BetterHUD.mods) {
            ModSettings modSettings = mod.getModSettings();
            if (!modSettings.getSetting("Enabled").getBooleanValue()) continue;

            CustomText modText = mod.onStartTick(client);
            if (modText == null) continue;

            String orientation = modSettings
                .getSetting("Orientation")
                .getStringValue();

            if (modSettings.getSetting("Custom Position").getBooleanValue()) {
                modText.customPosition = true;
                modText.customX = modSettings
                    .getSetting("Custom X")
                    .getIntValue();
                modText.customY = modSettings
                    .getSetting("Custom Y")
                    .getIntValue();
                this.customPositionText.add(modText);
            } else if (orientation.equals("top-left")) {
                this.topLeftText.add(modText);
            } else if (orientation.equals("top-right")) {
                this.topRightText.add(modText);
            } else if (orientation.equals("bottom-left")) {
                this.bottomLeftList.add(modText);
            } else if (orientation.equals("bottom-right")) {
                this.bottomRightText.add(modText);
            }
        }
    }

    public void onHudRender(
        GuiGraphics drawContext,
        DeltaTracker tickCounter
    ) {
        if (client.getDebugOverlay().showDebugScreen()) return;
        if (client.options.hideGui) return;

        int x = horizontalMargin;
        int y = verticalMargin;

        for (CustomText text : topLeftText) {
            drawString(drawContext, text, x, y);

            y +=
                (client.font.lineHeight - 1) +
                (verticalPadding * 2) +
                lineHeight;
        }

        y = client.getWindow().getGuiScaledHeight() - verticalMargin;

        for (CustomText text : bottomLeftList) {
            y -= (client.font.lineHeight - 1) + (verticalPadding * 2);
            drawString(drawContext, text, x, y);
            y -= lineHeight;
        }

        y = verticalMargin;
        for (CustomText text : topRightText) {
            int offset =
                (client.font.width(text.text) - 1) +
                (horizontalPadding * 2) +
                horizontalMargin;
            x = client.getWindow().getGuiScaledWidth() - offset;
            drawString(drawContext, text, x, y);

            y +=
                (client.font.lineHeight - 1) +
                (verticalPadding * 2) +
                lineHeight;
        }

        y = client.getWindow().getGuiScaledHeight() - verticalMargin;
        for (CustomText text : bottomRightText) {
            int offset =
                (client.font.width(text.text) - 1) +
                (horizontalPadding * 2) +
                horizontalMargin;
            x = client.getWindow().getGuiScaledWidth() - offset;

            y -= (client.font.lineHeight - 1) + (verticalPadding * 2);

            drawString(drawContext, text, x, y);

            y -= lineHeight;
        }

        for (CustomText text : customPositionText) {
            float xPercent = text.customX / 100.0f;
            float yPercent = text.customY / 100.0f;

            int maxX =
                client.getWindow().getGuiScaledWidth() -
                (horizontalPadding * 2) -
                (client.font.width(text.text) - 1);
            int maxY =
                client.getWindow().getGuiScaledHeight() -
                (verticalPadding * 2) -
                (client.font.lineHeight - 1);

            int scaledX = (int) (xPercent * maxX);
            int scaledY = (int) (yPercent * maxY);

            drawString(drawContext, text, scaledX, scaledY);
        }
    }

    private void drawString(
        GuiGraphics drawContext,
        CustomText text,
        int x,
        int y
    ) {
        drawContext.fill(
            x,
            y,
            x +
                (client.font.width(text.text) - 1) +
                (horizontalPadding * 2),
            y + (client.font.lineHeight - 1) + (verticalPadding * 2),
            text.backgroundColor
        );

        drawContext.drawString(
            client.font,
            text.text,
            x + horizontalPadding,
            y + verticalPadding,
            text.color,
            true
        );
    }
}
