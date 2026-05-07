package dsns.betterhud;

import dsns.betterhud.util.BaseMod;
import dsns.betterhud.util.CustomText;
import dsns.betterhud.util.ModSettings;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
//? if mc >= "26.1" {
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
//?} else {
/*import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;*/
//?}

public class BetterHUDGUI
    //? if mc >= "26.1"
    implements ClientTickEvents.StartTick {
    //?> /*implements HudRenderCallback, ClientTickEvents.StartTick {*/

    public static int verticalPadding = 4;
    public static int horizontalPadding = 4;

    public static int verticalMargin = 1;
    public static int horizontalMargin = 1;

    public static int lineHeight = 1;

    //? if mc >= "26.1" {
    private final Minecraft client = Minecraft.getInstance();
    //?} else {
    /*private final MinecraftClient client = MinecraftClient.getInstance();*/
    //?}

    private final List<CustomText> topLeftText = new ObjectArrayList<>();
    private final List<CustomText> topRightText = new ObjectArrayList<>();
    private final List<CustomText> bottomLeftList = new ObjectArrayList<>();
    private final List<CustomText> bottomRightText = new ObjectArrayList<>();
    private final List<CustomText> customPositionText = new ObjectArrayList<>();

    @Override
    //? if mc >= "26.1" {
    public void onStartTick(Minecraft client) {
    //?} else {
    /*public void onStartTick(MinecraftClient client) {*/
    //?}
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

    //? if mc >= "26.1" {
    public void onHudRender(
        GuiGraphicsExtractor drawContext,
        DeltaTracker tickCounter
    ) {
        if (client.getDebugOverlay().showDebugScreen()) return;
        if (client.options.hideGui) return;
    //?} else {
    /*@Override
    public void onHudRender(
        DrawContext drawContext,
        RenderTickCounter tickCounter
    ) {
        if (client.getDebugHud().shouldShowDebugHud()) return;
        if (client.options.hudHidden) return;*/
    //?}

        int x = horizontalMargin;
        int y = verticalMargin;

        for (CustomText text : topLeftText) {
            drawString(drawContext, text, x, y);

            //? if mc >= "26.1" {
            y += (client.font.lineHeight - 1) + (verticalPadding * 2) + lineHeight;
            //?} else {
            /*y += (client.textRenderer.fontHeight - 1) + (verticalPadding * 2) + lineHeight;*/
            //?}
        }

        //? if mc >= "26.1" {
        y = client.getWindow().getGuiScaledHeight() - verticalMargin;
        //?} else {
        /*y = client.getWindow().getScaledHeight() - verticalMargin;*/
        //?}

        for (CustomText text : bottomLeftList) {
            //? if mc >= "26.1" {
            y -= (client.font.lineHeight - 1) + (verticalPadding * 2);
            //?} else {
            /*y -= (client.textRenderer.fontHeight - 1) + (verticalPadding * 2);*/
            //?}
            drawString(drawContext, text, x, y);
            y -= lineHeight;
        }

        y = verticalMargin;
        for (CustomText text : topRightText) {
            //? if mc >= "26.1" {
            int offset = (client.font.width(text.text) - 1) + (horizontalPadding * 2) + horizontalMargin;
            x = client.getWindow().getGuiScaledWidth() - offset;
            //?} else {
            /*int offset = (client.textRenderer.getWidth(text.text) - 1) + (horizontalPadding * 2) + horizontalMargin;
            x = client.getWindow().getScaledWidth() - offset;*/
            //?}
            drawString(drawContext, text, x, y);

            //? if mc >= "26.1" {
            y += (client.font.lineHeight - 1) + (verticalPadding * 2) + lineHeight;
            //?} else {
            /*y += (client.textRenderer.fontHeight - 1) + (verticalPadding * 2) + lineHeight;*/
            //?}
        }

        //? if mc >= "26.1" {
        y = client.getWindow().getGuiScaledHeight() - verticalMargin;
        //?} else {
        /*y = client.getWindow().getScaledHeight() - verticalMargin;*/
        //?}
        for (CustomText text : bottomRightText) {
            //? if mc >= "26.1" {
            int offset = (client.font.width(text.text) - 1) + (horizontalPadding * 2) + horizontalMargin;
            x = client.getWindow().getGuiScaledWidth() - offset;
            y -= (client.font.lineHeight - 1) + (verticalPadding * 2);
            //?} else {
            /*int offset = (client.textRenderer.getWidth(text.text) - 1) + (horizontalPadding * 2) + horizontalMargin;
            x = client.getWindow().getScaledWidth() - offset;
            y -= (client.textRenderer.fontHeight - 1) + (verticalPadding * 2);*/
            //?}

            drawString(drawContext, text, x, y);
            y -= lineHeight;
        }

        for (CustomText text : customPositionText) {
            float xPercent = text.customX / 100.0f;
            float yPercent = text.customY / 100.0f;

            //? if mc >= "26.1" {
            int maxX = client.getWindow().getGuiScaledWidth() - (horizontalPadding * 2) - (client.font.width(text.text) - 1);
            int maxY = client.getWindow().getGuiScaledHeight() - (verticalPadding * 2) - (client.font.lineHeight - 1);
            //?} else {
            /*int maxX = client.getWindow().getScaledWidth() - (horizontalPadding * 2) - (client.textRenderer.getWidth(text.text) - 1);
            int maxY = client.getWindow().getScaledHeight() - (verticalPadding * 2) - (client.textRenderer.fontHeight - 1);*/
            //?}

            int scaledX = (int) (xPercent * maxX);
            int scaledY = (int) (yPercent * maxY);

            drawString(drawContext, text, scaledX, scaledY);
        }
    }

    //? if mc >= "26.1" {
    private void drawString(GuiGraphicsExtractor drawContext, CustomText text, int x, int y) {
        drawContext.fill(
            x, y,
            x + (client.font.width(text.text) - 1) + (horizontalPadding * 2),
            y + (client.font.lineHeight - 1) + (verticalPadding * 2),
            text.backgroundColor
        );
        drawContext.text(client.font, text.text, x + horizontalPadding, y + verticalPadding, text.color, true);
    }
    //?} else {
    /*private void drawString(DrawContext drawContext, CustomText text, int x, int y) {
        drawContext.fill(
            x, y,
            x + (client.textRenderer.getWidth(text.text) - 1) + (horizontalPadding * 2),
            y + (client.textRenderer.fontHeight - 1) + (verticalPadding * 2),
            text.backgroundColor
        );
        drawContext.drawText(client.textRenderer, text.text, x + horizontalPadding, y + verticalPadding, text.color, true);
    }*/
    //?}
}
