package dsns.betterhud;

import dsns.betterhud.util.BaseMod;
import dsns.betterhud.util.CustomText;
import dsns.betterhud.util.ModSettings;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;

import org.joml.Matrix3x2fStack;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

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
            if (!modSettings.getSetting("Enabled").getBooleanValue())
                continue;

            CustomText modText = mod.onStartTick(client);
            if (modText == null)
                continue;

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
            GuiGraphicsExtractor drawContext,
            DeltaTracker tickCounter) {
        if (client.getDebugOverlay().showDebugScreen())
            return;
        if (client.gui.hud.isHidden())
            return;

        int x = horizontalMargin;
        int y = verticalMargin;

        for (CustomText text : topLeftText) {
            drawString(drawContext, text, x, y);

            y += scaledElementHeight(text) + lineHeight;
        }

        y = client.getWindow().getGuiScaledHeight() - verticalMargin;

        for (CustomText text : bottomLeftList) {
            y -= scaledElementHeight(text);
            drawString(drawContext, text, x, y);
            y -= lineHeight;
        }

        y = verticalMargin;
        for (CustomText text : topRightText) {
            int offset = (client.font.width(text.text) - 1) + (horizontalPadding * 2) + horizontalMargin;

            x = client.getWindow().getGuiScaledWidth() - offset;
            drawString(drawContext, text, x, y);

            y += scaledElementHeight(text) + lineHeight;
        }

        y = client.getWindow().getGuiScaledHeight() - verticalMargin;
        for (CustomText text : bottomRightText) {
            int offset = scaledElementWidth(text) + horizontalMargin;
            x = client.getWindow().getGuiScaledWidth() - offset;
            y -= scaledElementHeight(text);

            drawString(drawContext, text, x, y);
            y -= lineHeight;
        }

        for (CustomText text : customPositionText) {
            float xPercent = text.customX / 100.0f;
            float yPercent = text.customY / 100.0f;

            int maxX = client.getWindow().getGuiScaledWidth() - scaledElementWidth(text);
            int maxY = client.getWindow().getGuiScaledHeight() - scaledElementHeight(text);

            int scaledX = (int) (xPercent * maxX);
            int scaledY = (int) (yPercent * maxY);

            drawString(drawContext, text, scaledX, scaledY);
        }
    }

    private void drawString(
            GuiGraphicsExtractor drawContext,
            CustomText text,
            int x,
            int y) {
        Matrix3x2fStack poses = drawContext.pose();
        poses.pushMatrix();
        poses.translate(x, y);
        poses.scale(text.scale, text.scale);

        int w = (client.font.width(text.text) - 1) + (horizontalPadding * 2);
        int h = (client.font.lineHeight - 1) + (verticalPadding * 2);

        drawContext.fill(0, 0, w, h, text.backgroundColor);

        drawContext.text(
                client.font,
                text.text,
                horizontalPadding,
                verticalPadding,
                text.color,
                true);

        poses.popMatrix();
    }

    private int scaledElementWidth(CustomText text) {
        int w = (client.font.width(text.text) - 1) + (horizontalPadding * 2);
        return (int) (w * text.scale);
    }

    private int scaledElementHeight(CustomText text) {
        int h = (client.font.lineHeight - 1) + (verticalPadding * 2);
        return (int) (h * text.scale);
    }
}
