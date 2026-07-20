package dsns.betterhud;

import dsns.betterhud.util.BaseMod;
import dsns.betterhud.util.CustomText;
import dsns.betterhud.util.HudLayout;
import dsns.betterhud.util.HudRenderer;
import dsns.betterhud.util.ModSettings;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
//? if <1.21.6 {
/*import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;*/
//?}
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
//? if >=26 {
import net.minecraft.client.gui.GuiGraphicsExtractor;
//?} else {
/*import net.minecraft.client.gui.GuiGraphics;*/
//?}

//? if >=1.21.6 {
public class BetterHUDGUI implements ClientTickEvents.StartTick {
//?} else {
/*public class BetterHUDGUI implements HudRenderCallback, ClientTickEvents.StartTick {*/
//?}

    private final Minecraft client = Minecraft.getInstance();
    private final List<CustomText> hudTexts = new ObjectArrayList<>();

    @Override
    public void onStartTick(Minecraft client) {
        if (BetterHUD.openEditorKey != null) {
            while (BetterHUD.openEditorKey.consumeClick()) {
                //? if >=26.2 {
                client.gui.setScreen(new HudEditorScreen());
                //?} else {
                /*client.setScreen(new HudEditorScreen());*/
                //?}
            }
        }

        this.hudTexts.clear();

        for (BaseMod mod : BetterHUD.mods) {
            ModSettings modSettings = mod.getModSettings();
            if (!modSettings.getSetting("Enabled").getBooleanValue()) continue;

            CustomText modText = mod.onStartTick(client);
            if (modText == null) continue;

            modText.applyPlacement(modSettings);
            this.hudTexts.add(modText);
        }
    }

    public void onHudRender(
        //? if >=26 {
        GuiGraphicsExtractor drawContext,
        //?} else {
        /*GuiGraphics drawContext,*/
        //?}
        DeltaTracker tickCounter
    ) {
        if (client.getDebugOverlay().showDebugScreen()) return;
        //? if >=26.2 {
        if (client.gui.hud.isHidden()) return;
        //?} else {
        /*if (client.options.hideGui) return;*/
        //?}

        // The editor draws its own live preview of every element.
        //? if >=26.2 {
        if (client.gui.screen() instanceof HudEditorScreen) return;
        //?} else {
        /*if (client.screen instanceof HudEditorScreen) return;*/
        //?}

        for (HudLayout.Placed placed : HudLayout.layout(client, hudTexts)) {
            HudRenderer.draw(drawContext, client, placed.text, placed.x, placed.y);
        }
    }
}
