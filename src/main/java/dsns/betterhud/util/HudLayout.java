package dsns.betterhud.util;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;

/**
 * Shared placement math for HUD elements. Both the in-game HUD renderer and
 * the HUD editor screen go through this class, so the editor preview always
 * matches what the HUD actually renders.
 */
public final class HudLayout {

    public static final int VERTICAL_PADDING = 4;
    public static final int HORIZONTAL_PADDING = 4;

    public static final int VERTICAL_MARGIN = 1;
    public static final int HORIZONTAL_MARGIN = 1;

    public static final int LINE_HEIGHT = 1;

    public static final class Placed {

        public final CustomText text;
        public final int x;
        public final int y;
        public final int width;
        public final int height;

        public Placed(CustomText text, int x, int y, int width, int height) {
            this.text = text;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }

    private HudLayout() {}

    public static int elementWidth(Minecraft client, CustomText text) {
        int w = (client.font.width(text.text) - 1) + (HORIZONTAL_PADDING * 2);
        return (int) (w * text.scale);
    }

    public static int elementHeight(Minecraft client, CustomText text) {
        int h = (client.font.lineHeight - 1) + (VERTICAL_PADDING * 2);
        return (int) (h * text.scale);
    }

    /**
     * Computes the on-screen position of every element. Elements without a
     * custom position stack downwards (or upwards) from their corner in list
     * order; custom-positioned elements map their 0-100% coordinates onto the
     * space the element can occupy without leaving the screen.
     */
    public static List<Placed> layout(Minecraft client, List<CustomText> texts) {
        List<Placed> placed = new ObjectArrayList<>();

        int screenWidth = client.getWindow().getGuiScaledWidth();
        int screenHeight = client.getWindow().getGuiScaledHeight();

        int topLeftY = VERTICAL_MARGIN;
        int topRightY = VERTICAL_MARGIN;
        int bottomLeftY = screenHeight - VERTICAL_MARGIN;
        int bottomRightY = screenHeight - VERTICAL_MARGIN;

        for (CustomText text : texts) {
            int width = elementWidth(client, text);
            int height = elementHeight(client, text);

            if (text.customPosition) {
                int maxX = screenWidth - width;
                int maxY = screenHeight - height;

                int x = (int) (text.customX / 100.0f * maxX);
                int y = (int) (text.customY / 100.0f * maxY);

                placed.add(new Placed(text, x, y, width, height));
            } else if (text.orientation.equals("top-left")) {
                placed.add(
                    new Placed(text, HORIZONTAL_MARGIN, topLeftY, width, height)
                );
                topLeftY += height + LINE_HEIGHT;
            } else if (text.orientation.equals("top-right")) {
                int x = screenWidth - width - HORIZONTAL_MARGIN;
                placed.add(new Placed(text, x, topRightY, width, height));
                topRightY += height + LINE_HEIGHT;
            } else if (text.orientation.equals("bottom-left")) {
                bottomLeftY -= height;
                placed.add(
                    new Placed(
                        text,
                        HORIZONTAL_MARGIN,
                        bottomLeftY,
                        width,
                        height
                    )
                );
                bottomLeftY -= LINE_HEIGHT;
            } else if (text.orientation.equals("bottom-right")) {
                int x = screenWidth - width - HORIZONTAL_MARGIN;
                bottomRightY -= height;
                placed.add(new Placed(text, x, bottomRightY, width, height));
                bottomRightY -= LINE_HEIGHT;
            }
        }

        return placed;
    }
}
