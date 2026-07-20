package dsns.betterhud;

import dsns.betterhud.util.BaseMod;
import dsns.betterhud.util.CustomText;
import dsns.betterhud.util.HudLayout;
import dsns.betterhud.util.HudRenderer;
import dsns.betterhud.util.ModSettings;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
//? if >=26 {
import net.minecraft.client.gui.GuiGraphicsExtractor;
//?} else {
/*import net.minecraft.client.gui.GuiGraphics;*/
//?}
import net.minecraft.client.gui.screens.Screen;
//? if >=1.21.9 {
import net.minecraft.client.input.MouseButtonEvent;
//?}
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
//? if >=1.21.6 {
import org.joml.Matrix3x2fStack;
//?} else {
/*import com.mojang.blaze3d.vertex.PoseStack;*/
//?}

/**
 * A Lunar Client style HUD editor: every enabled element is shown live and
 * can be dragged anywhere on the screen, docked into a corner stack by
 * dropping it there, and resized with the scroll wheel. Elements can never
 * overlap. Closing the screen (Escape) saves the layout to the config file.
 */
public class HudEditorScreen extends Screen {

    private static final int SNAP_DISTANCE = 5;

    // Dragging this close to a corner's dock anchor (the spot a docked
    // element occupies, margin included) docks the element there.
    private static final int DOCK_SNAP_DISTANCE = 10;

    private static final String[] CORNERS = {
        "top-left",
        "top-right",
        "bottom-left",
        "bottom-right",
    };

    private static final int BACKDROP_COLOR = 0x50000000;
    private static final int NO_LEVEL_BACKDROP_COLOR = 0xff181818;

    // Opaque beveled grays so the Settings button reads as a button rather
    // than as another HUD element (those are translucent black boxes).
    private static final int BUTTON_BORDER_COLOR = 0xff000000;
    private static final int BUTTON_BORDER_HOVERED_COLOR = 0xffffffff;
    private static final int BUTTON_BODY_COLOR = 0xff6c6c6c;
    private static final int BUTTON_BODY_HOVERED_COLOR = 0xff8d8d8d;
    private static final int BUTTON_BEVEL_LIGHT_COLOR = 0xffb4b4b4;
    private static final int BUTTON_BEVEL_DARK_COLOR = 0xff414141;

    // The title and the Settings button live in the center of the screen:
    // people rarely place a HUD element there, so they stay visible.
    private static final float TITLE_SCALE = 2.0f;
    private static final float BUTTON_TEXT_SCALE = 1.25f;
    private static final int OUTLINE_COLOR = 0x66ffffff;
    private static final int OUTLINE_HOVERED_COLOR = 0xccffffff;
    private static final int OUTLINE_DRAGGED_COLOR = 0xff55ffff;
    private static final int GUIDE_COLOR = 0xaa55ffff;
    private static final int TEXT_COLOR = 0xffffffff;
    private static final int TEXT_MUTED_COLOR = 0xffaaaaaa;

    private final Screen parent;
    private final List<CustomText> texts = new ObjectArrayList<>();
    private final Map<CustomText, BaseMod> owners = new IdentityHashMap<>();
    private List<HudLayout.Placed> placedElements = new ObjectArrayList<>();

    private BaseMod draggedMod = null;
    private double grabOffsetX = 0;
    private double grabOffsetY = 0;
    private boolean snappedCenterX = false;
    private boolean snappedCenterY = false;

    private int settingsButtonX = 0;
    private int settingsButtonY = 0;
    private int settingsButtonWidth = 0;
    private int settingsButtonHeight = 0;

    public HudEditorScreen() {
        this(null);
    }

    public HudEditorScreen(Screen parent) {
        super(Component.translatable("betterhud.editor.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        refreshElements();
    }

    @Override
    public void tick() {
        refreshElements();
    }

    /**
     * Rebuilds the preview text of every enabled element. Elements whose mod
     * has nothing to show right now (e.g. Ping in singleplayer) get a
     * placeholder so they can still be positioned.
     */
    private void refreshElements() {
        Minecraft client = Minecraft.getInstance();

        texts.clear();
        owners.clear();

        for (BaseMod mod : BetterHUD.mods) {
            ModSettings settings = mod.getModSettings();
            if (!settings.getSetting("Enabled").getBooleanValue()) continue;

            CustomText text = mod.onStartTick(client);
            if (text == null) text = new CustomText(mod.getModID(), settings);

            texts.add(text);
            owners.put(text, mod);
        }
    }

    private CustomText textFor(BaseMod mod) {
        for (Map.Entry<CustomText, BaseMod> entry : owners.entrySet()) {
            if (entry.getValue() == mod) return entry.getKey();
        }
        return null;
    }

    private HudLayout.Placed placedFor(BaseMod mod) {
        for (HudLayout.Placed placed : placedElements) {
            if (owners.get(placed.text) == mod) return placed;
        }
        return null;
    }

    private HudLayout.Placed elementAt(double mouseX, double mouseY) {
        // Iterate backwards so the element drawn on top wins.
        for (int i = placedElements.size() - 1; i >= 0; i--) {
            HudLayout.Placed placed = placedElements.get(i);
            if (
                mouseX >= placed.x &&
                mouseX < placed.x + placed.width &&
                mouseY >= placed.y &&
                mouseY < placed.y + placed.height
            ) return placed;
        }
        return null;
    }

    private boolean overSettingsButton(double mouseX, double mouseY) {
        return (
            settingsButtonWidth > 0 &&
            mouseX >= settingsButtonX &&
            mouseX < settingsButtonX + settingsButtonWidth &&
            mouseY >= settingsButtonY &&
            mouseY < settingsButtonY + settingsButtonHeight
        );
    }

    private boolean handleMouseDown(double mouseX, double mouseY, int button) {
        if (button == 0 && overSettingsButton(mouseX, mouseY)) {
            Screen settings = SettingsScreenBuilder.createSettingsScreen(this);
            if (settings != null) {
                Minecraft client = Minecraft.getInstance();
                //? if >=26.2 {
                client.gui.setScreen(settings);
                //?} else {
                /*client.setScreen(settings);*/
                //?}
            }
            return true;
        }

        if (button != 0) return false;

        HudLayout.Placed placed = elementAt(mouseX, mouseY);
        if (placed == null) return false;

        draggedMod = owners.get(placed.text);
        grabOffsetX = mouseX - placed.x;
        grabOffsetY = mouseY - placed.y;
        return true;
    }

    private boolean handleMouseDrag(double mouseX, double mouseY) {
        if (draggedMod == null) return false;

        Minecraft client = Minecraft.getInstance();
        CustomText text = textFor(draggedMod);
        if (text == null) return false;

        int elementWidth = HudLayout.elementWidth(client, text);
        int elementHeight = HudLayout.elementHeight(client, text);
        int maxX = this.width - elementWidth;
        int maxY = this.height - elementHeight;

        double x = mouseX - grabOffsetX;
        double y = mouseY - grabOffsetY;

        snappedCenterX = false;
        snappedCenterY = false;

        ModSettings settings = draggedMod.getModSettings();

        // Near a corner the element docks into that corner's stack; the
        // layout then slots it in with the usual margin and stacking.
        String dockCorner = dockCornerAt(x, y, maxX, maxY);
        if (dockCorner != null) {
            settings.getSetting("Position").setValue(dockCorner);
            return true;
        }

        // Snap to the screen edges and the screen center lines.
        double centerX = maxX / 2.0;
        double centerY = maxY / 2.0;
        if (Math.abs(x - centerX) <= SNAP_DISTANCE) {
            x = centerX;
            snappedCenterX = true;
        } else if (Math.abs(x) <= SNAP_DISTANCE) {
            x = 0;
        } else if (Math.abs(x - maxX) <= SNAP_DISTANCE) {
            x = maxX;
        }
        if (Math.abs(y - centerY) <= SNAP_DISTANCE) {
            y = centerY;
            snappedCenterY = true;
        } else if (Math.abs(y) <= SNAP_DISTANCE) {
            y = 0;
        } else if (Math.abs(y - maxY) <= SNAP_DISTANCE) {
            y = maxY;
        }

        x = Math.clamp(x, 0, Math.max(maxX, 0));
        y = Math.clamp(y, 0, Math.max(maxY, 0));

        // Elements may never cover each other: an overlapping spot is
        // rejected and the element stays where it last fit.
        if (overlapsOtherElement(x, y, elementWidth, elementHeight)) {
            snappedCenterX = false;
            snappedCenterY = false;
            return true;
        }

        double percentX = maxX <= 0 ? 0 : x * 100.0 / maxX;
        double percentY = maxY <= 0 ? 0 : y * 100.0 / maxY;

        settings.getSetting("Position").setValue("custom");
        settings
            .getSetting("Custom X")
            .setValue(String.valueOf(Math.round(percentX * 100.0) / 100.0));
        settings
            .getSetting("Custom Y")
            .setValue(String.valueOf(Math.round(percentY * 100.0) / 100.0));
        return true;
    }

    /** The corner whose dock anchor is within snapping range, or null. */
    private String dockCornerAt(double x, double y, int maxX, int maxY) {
        for (String corner : CORNERS) {
            boolean left = corner.endsWith("-left");
            boolean top = corner.startsWith("top");
            double anchorX = left
                ? HudLayout.HORIZONTAL_MARGIN
                : maxX - HudLayout.HORIZONTAL_MARGIN;
            double anchorY = top
                ? HudLayout.VERTICAL_MARGIN
                : maxY - HudLayout.VERTICAL_MARGIN;
            if (
                Math.abs(x - anchorX) <= DOCK_SNAP_DISTANCE &&
                Math.abs(y - anchorY) <= DOCK_SNAP_DISTANCE
            ) return corner;
        }
        return null;
    }

    private boolean overlapsOtherElement(
        double x,
        double y,
        int width,
        int height
    ) {
        for (HudLayout.Placed placed : placedElements) {
            if (owners.get(placed.text) == draggedMod) continue;
            if (
                x < placed.x + placed.width &&
                x + width > placed.x &&
                y < placed.y + placed.height &&
                y + height > placed.y
            ) return true;
        }
        return false;
    }

    private boolean handleMouseUp() {
        if (draggedMod == null) return false;
        draggedMod = null;
        snappedCenterX = false;
        snappedCenterY = false;
        return true;
    }

    //? if >=1.21.9 {
    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        return handleMouseDown(event.x(), event.y(), event.button());
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        return handleMouseUp();
    }

    @Override
    public boolean mouseDragged(
        MouseButtonEvent event,
        double dragX,
        double dragY
    ) {
        return handleMouseDrag(event.x(), event.y());
    }
    //?} else {
    /*@Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return handleMouseDown(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return handleMouseUp();
    }

    @Override
    public boolean mouseDragged(
        double mouseX,
        double mouseY,
        int button,
        double dragX,
        double dragY
    ) {
        return handleMouseDrag(mouseX, mouseY);
    }
    *///?}

    @Override
    public boolean mouseScrolled(
        double mouseX,
        double mouseY,
        double scrollX,
        double scrollY
    ) {
        HudLayout.Placed placed = draggedMod != null
            ? placedFor(draggedMod)
            : elementAt(mouseX, mouseY);
        if (placed == null || scrollY == 0) return false;

        ModSettings settings = owners.get(placed.text).getModSettings();
        float oldScale = settings.getSetting("Scale").getFloatValue();
        float scale = oldScale + (scrollY > 0 ? 0.1f : -0.1f);
        scale = Math.round(scale * 10.0f) / 10.0f;
        scale = Math.clamp(scale, 0.1f, 10.0f);

        // Growing may not push the element into a neighbour; shrinking is
        // always allowed so elements can never get stuck.
        if (scale > oldScale && resizeWouldOverlap(placed.text, scale)) {
            return true;
        }

        settings.getSetting("Scale").setValue(String.valueOf(scale));
        return true;
    }

    private boolean resizeWouldOverlap(CustomText subject, float newScale) {
        Minecraft client = Minecraft.getInstance();
        float oldScale = subject.scale;
        subject.scale = newScale;
        List<HudLayout.Placed> trial = HudLayout.layout(client, texts);
        subject.scale = oldScale;

        HudLayout.Placed resized = null;
        for (HudLayout.Placed placed : trial) {
            if (placed.text == subject) resized = placed;
        }
        if (resized == null) return false;

        for (HudLayout.Placed placed : trial) {
            if (placed.text == subject) continue;
            if (
                resized.x < placed.x + placed.width &&
                resized.x + resized.width > placed.x &&
                resized.y < placed.y + placed.height &&
                resized.y + resized.height > placed.y
            ) return true;
        }
        return false;
    }

    //? if >=26 {
    @Override
    public void extractRenderState(
        GuiGraphicsExtractor drawContext,
        int mouseX,
        int mouseY,
        float delta
    ) {
        renderEditor(drawContext, mouseX, mouseY);
    }
    //?} else {
    /*@Override
    public void render(
        GuiGraphics drawContext,
        int mouseX,
        int mouseY,
        float delta
    ) {
        renderEditor(drawContext, mouseX, mouseY);
    }
    *///?}

    private void renderEditor(
        //? if >=26 {
        GuiGraphicsExtractor drawContext,
        //?} else {
        /*GuiGraphics drawContext,*/
        //?}
        int mouseX,
        int mouseY
    ) {
        Minecraft client = Minecraft.getInstance();

        // Opened from Mod Menu there may be no world behind the screen; use a
        // solid backdrop instead of a translucent one.
        drawContext.fill(
            0,
            0,
            this.width,
            this.height,
            client.level == null ? NO_LEVEL_BACKDROP_COLOR : BACKDROP_COLOR
        );

        for (CustomText text : texts) {
            text.applyPlacement(owners.get(text).getModSettings());
        }
        placedElements = HudLayout.layout(client, texts);

        if (snappedCenterX && draggedMod != null) {
            drawContext.fill(
                this.width / 2,
                0,
                this.width / 2 + 1,
                this.height,
                GUIDE_COLOR
            );
        }
        if (snappedCenterY && draggedMod != null) {
            drawContext.fill(
                0,
                this.height / 2,
                this.width,
                this.height / 2 + 1,
                GUIDE_COLOR
            );
        }

        HudLayout.Placed hovered = elementAt(mouseX, mouseY);

        for (HudLayout.Placed placed : placedElements) {
            HudRenderer.draw(drawContext, client, placed.text, placed.x, placed.y);

            int outlineColor = OUTLINE_COLOR;
            if (owners.get(placed.text) == draggedMod) {
                outlineColor = OUTLINE_DRAGGED_COLOR;
            } else if (placed == hovered && draggedMod == null) {
                outlineColor = OUTLINE_HOVERED_COLOR;
            }
            drawOutline(
                drawContext,
                placed.x - 1,
                placed.y - 1,
                placed.width + 2,
                placed.height + 2,
                outlineColor
            );
        }

        String heading = I18n.get("betterhud.editor.title");
        drawScaledCenteredText(
            drawContext,
            client,
            heading,
            this.height / 2 - 26,
            TEXT_COLOR,
            TITLE_SCALE
        );

        drawSettingsButton(drawContext, client, mouseX, mouseY);

        // The hints live at the bottom center, clear of the corner stacks.
        HudLayout.Placed described = draggedMod != null
            ? placedFor(draggedMod)
            : hovered;
        if (described != null) {
            BaseMod mod = owners.get(described.text);
            String info =
                mod.getModID() +
                "  ·  " +
                I18n.get(
                    "betterhud.editor.scale",
                    String.format("%.1f", described.text.scale)
                );
            drawCenteredText(drawContext, client, info, this.height - 20, TEXT_COLOR);
        } else {
            drawCenteredText(
                drawContext,
                client,
                I18n.get("betterhud.editor.hint"),
                this.height - 32,
                TEXT_MUTED_COLOR
            );
            drawCenteredText(
                drawContext,
                client,
                I18n.get("betterhud.editor.hint2"),
                this.height - 20,
                TEXT_MUTED_COLOR
            );
        }
    }

    private void drawSettingsButton(
        //? if >=26 {
        GuiGraphicsExtractor drawContext,
        //?} else {
        /*GuiGraphics drawContext,*/
        //?}
        Minecraft client,
        int mouseX,
        int mouseY
    ) {
        if (!SettingsScreenBuilder.settingsScreenAvailable()) {
            settingsButtonWidth = 0;
            return;
        }

        // Classic button proportions (20px tall, text centered at
        // (height - 8) / 2, ~10px side padding), scaled with the text.
        String label = I18n.get("betterhud.editor.settings");
        settingsButtonWidth =
            (int) ((client.font.width(label) + 20) * BUTTON_TEXT_SCALE);
        settingsButtonHeight = (int) (20 * BUTTON_TEXT_SCALE);
        settingsButtonX = (this.width - settingsButtonWidth) / 2;
        settingsButtonY = this.height / 2 + 2;

        boolean hovered = overSettingsButton(mouseX, mouseY);
        int x = settingsButtonX;
        int y = settingsButtonY;
        int w = settingsButtonWidth;
        int h = settingsButtonHeight;

        drawContext.fill(
            x,
            y,
            x + w,
            y + h,
            hovered ? BUTTON_BORDER_HOVERED_COLOR : BUTTON_BORDER_COLOR
        );
        drawContext.fill(
            x + 1,
            y + 1,
            x + w - 1,
            y + h - 1,
            hovered ? BUTTON_BODY_HOVERED_COLOR : BUTTON_BODY_COLOR
        );
        drawContext.fill(x + 1, y + 1, x + w - 1, y + 2, BUTTON_BEVEL_LIGHT_COLOR);
        drawContext.fill(x + 1, y + 1, x + 2, y + h - 1, BUTTON_BEVEL_LIGHT_COLOR);
        drawContext.fill(x + 1, y + h - 2, x + w - 1, y + h - 1, BUTTON_BEVEL_DARK_COLOR);
        drawContext.fill(x + w - 2, y + 2, x + w - 1, y + h - 1, BUTTON_BEVEL_DARK_COLOR);

        drawScaledCenteredText(
            drawContext,
            client,
            label,
            settingsButtonY +
                (int) ((settingsButtonHeight - 8 * BUTTON_TEXT_SCALE) / 2),
            TEXT_COLOR,
            BUTTON_TEXT_SCALE
        );
    }

    private void drawScaledCenteredText(
        //? if >=26 {
        GuiGraphicsExtractor drawContext,
        //?} else {
        /*GuiGraphics drawContext,*/
        //?}
        Minecraft client,
        String text,
        int y,
        int color,
        float scale
    ) {
        int x = (int) ((this.width - client.font.width(text) * scale) / 2);

        //? if >=1.21.6 {
        Matrix3x2fStack poses = drawContext.pose();
        poses.pushMatrix();
        poses.translate(x, y);
        poses.scale(scale, scale);
        //?} else {
        /*PoseStack poses = drawContext.pose();
        poses.pushPose();
        poses.translate(x, y, 0);
        poses.scale(scale, scale, 1);*/
        //?}

        //? if >=26 {
        drawContext.text(client.font, text, 0, 0, color, true);
        //?} else {
        /*drawContext.drawString(client.font, text, 0, 0, color, true);*/
        //?}

        //? if >=1.21.6 {
        poses.popMatrix();
        //?} else {
        /*poses.popPose();*/
        //?}
    }

    private void drawCenteredText(
        //? if >=26 {
        GuiGraphicsExtractor drawContext,
        //?} else {
        /*GuiGraphics drawContext,*/
        //?}
        Minecraft client,
        String text,
        int y,
        int color
    ) {
        int x = (this.width - client.font.width(text)) / 2;
        //? if >=26 {
        drawContext.text(client.font, text, x, y, color, true);
        //?} else {
        /*drawContext.drawString(client.font, text, x, y, color, true);*/
        //?}
    }

    private void drawOutline(
        //? if >=26 {
        GuiGraphicsExtractor drawContext,
        //?} else {
        /*GuiGraphics drawContext,*/
        //?}
        int x,
        int y,
        int width,
        int height,
        int color
    ) {
        drawContext.fill(x, y, x + width, y + 1, color);
        drawContext.fill(x, y + height - 1, x + width, y + height, color);
        drawContext.fill(x, y + 1, x + 1, y + height - 1, color);
        drawContext.fill(x + width - 1, y + 1, x + width, y + height - 1, color);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        Minecraft client = Minecraft.getInstance();
        //? if >=26.2 {
        client.gui.setScreen(parent);
        //?} else {
        /*client.setScreen(parent);*/
        //?}
    }

    @Override
    public void removed() {
        Config.serialize();
        super.removed();
    }
}
