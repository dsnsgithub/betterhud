package dsns.betterhud.util;

import net.minecraft.client.Minecraft;
//? if >=26 {
import net.minecraft.client.gui.GuiGraphicsExtractor;
//?} else {
/*import net.minecraft.client.gui.GuiGraphics;*/
//?}
//? if >=1.21.6 {
import org.joml.Matrix3x2fStack;
//?} else {
/*import com.mojang.blaze3d.vertex.PoseStack;*/
//?}

/**
 * Draws a single HUD element (background + text). Shared between the in-game
 * HUD and the HUD editor screen.
 */
public final class HudRenderer {

    private HudRenderer() {}

    public static void draw(
        //? if >=26 {
        GuiGraphicsExtractor drawContext,
        //?} else {
        /*GuiGraphics drawContext,*/
        //?}
        Minecraft client,
        CustomText text,
        int x,
        int y
    ) {
        //? if >=1.21.6 {
        Matrix3x2fStack poses = drawContext.pose();
        poses.pushMatrix();
        poses.translate(x, y);
        poses.scale(text.scale, text.scale);
        //?} else {
        /*PoseStack poses = drawContext.pose();
        poses.pushPose();
        poses.translate(x, y, 0);
        poses.scale(text.scale, text.scale, 1);*/
        //?}

        int w =
            (client.font.width(text.text) - 1) +
            (HudLayout.HORIZONTAL_PADDING * 2);
        int h =
            (client.font.lineHeight - 1) + (HudLayout.VERTICAL_PADDING * 2);

        drawContext.fill(0, 0, w, h, text.backgroundColor);

        //? if >=26 {
        drawContext.text(
            client.font,
            text.text,
            HudLayout.HORIZONTAL_PADDING,
            HudLayout.VERTICAL_PADDING,
            text.color,
            true
        );
        //?} else {
        /*drawContext.drawString(
            client.font,
            text.text,
            HudLayout.HORIZONTAL_PADDING,
            HudLayout.VERTICAL_PADDING,
            text.color,
            true
        );*/
        //?}

        //? if >=1.21.6 {
        poses.popMatrix();
        //?} else {
        /*poses.popPose();*/
        //?}
    }
}
