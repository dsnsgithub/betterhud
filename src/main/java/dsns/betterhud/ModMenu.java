package dsns.betterhud;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dsns.betterhud.util.BaseMod;
import dsns.betterhud.util.ModSettings;
import dsns.betterhud.util.Setting;
import java.util.LinkedHashMap;
import java.util.Map;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.api.Requirement;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;

public class ModMenu implements ModMenuApi {

    private static final Map<String, String> ORIENTATION_LABELS;

    static {
        ORIENTATION_LABELS = new LinkedHashMap<>();
        ORIENTATION_LABELS.put("top-left", "Top Left");
        ORIENTATION_LABELS.put("top-right", "Top Right");
        ORIENTATION_LABELS.put("bottom-left", "Bottom Left");
        ORIENTATION_LABELS.put("bottom-right", "Bottom Right");
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (!FabricLoader.getInstance().isModLoaded("cloth-config2")) {
            return parent -> null;
        }
        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.literal("BetterHUD Settings"));

            builder.setSavingRunnable(Config::serialize);

            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            for (BaseMod mod : BetterHUD.mods) {
                ConfigCategory category = builder.getOrCreateCategory(
                    Component.literal(mod.getModID())
                );
                buildModCategory(category, entryBuilder, mod);
            }

            return builder.build();
        };
    }

    private void buildModCategory(
        ConfigCategory category,
        ConfigEntryBuilder entryBuilder,
        BaseMod mod
    ) {
        ModSettings settings = mod.getModSettings();
        String name = mod.getModID();

        Setting enabled = settings.getSetting("Enabled");
        BooleanListEntry enabledEntry = entryBuilder
            .startBooleanToggle(
                Component.literal("Enable " + name),
                enabled.getBooleanValue()
            )
            .setDefaultValue(Boolean.parseBoolean(enabled.getDefaultValue()))
            .setTooltip(
                Component.literal(
                    "Show the " + name + " module on the HUD."
                )
            )
            .setYesNoTextSupplier(value ->
                Component.literal(value ? "Enabled" : "Disabled")
            )
            .setSaveConsumer(value ->
                enabled.setValue(String.valueOf(value))
            )
            .build();
        category.addEntry(enabledEntry);

        Setting customPosition = settings.getSetting("Custom Position");
        BooleanListEntry customPositionEntry = entryBuilder
            .startBooleanToggle(
                Component.literal("Use Custom Position"),
                customPosition.getBooleanValue()
            )
            .setDefaultValue(
                Boolean.parseBoolean(customPosition.getDefaultValue())
            )
            .setTooltip(
                Component.literal(
                    "Override the screen anchor and place this module at an exact location."
                )
            )
            .setSaveConsumer(value ->
                customPosition.setValue(String.valueOf(value))
            )
            .build();

        SubCategoryBuilder positionGroup = entryBuilder
            .startSubCategory(Component.literal("Position"))
            .setExpanded(true)
            .setTooltip(
                Component.literal(
                    "Where the module is drawn on the screen."
                )
            );

        Setting orientation = settings.getSetting("Orientation");
        positionGroup.add(
            entryBuilder
                .startSelector(
                    Component.literal("Anchor"),
                    orientation.getPossibleValues(),
                    orientation.getStringValue()
                )
                .setDefaultValue(orientation.getDefaultValue())
                .setNameProvider(value ->
                    Component.literal(prettyOrientation(value))
                )
                .setTooltip(
                    Component.literal(
                        "Which corner of the screen the module is anchored to."
                    )
                )
                .setRequirement(Requirement.isFalse(customPositionEntry))
                .setSaveConsumer(value -> orientation.setValue(value))
                .build()
        );

        positionGroup.add(customPositionEntry);

        Setting customX = settings.getSetting("Custom X");
        positionGroup.add(
            entryBuilder
                .startIntSlider(
                    Component.literal("Custom X"),
                    customX.getIntValue(),
                    parseBound(customX, 0, 0),
                    parseBound(customX, 1, 100)
                )
                .setDefaultValue(Integer.parseInt(customX.getDefaultValue()))
                .setTextGetter(value -> Component.literal(value + "%"))
                .setTooltip(
                    Component.literal(
                        "Horizontal position as a percentage of screen width (0% = left edge, 100% = right edge)."
                    )
                )
                .setRequirement(Requirement.isTrue(customPositionEntry))
                .setSaveConsumer(value ->
                    customX.setValue(String.valueOf(value))
                )
                .build()
        );

        Setting customY = settings.getSetting("Custom Y");
        positionGroup.add(
            entryBuilder
                .startIntSlider(
                    Component.literal("Custom Y"),
                    customY.getIntValue(),
                    parseBound(customY, 0, 0),
                    parseBound(customY, 1, 100)
                )
                .setDefaultValue(Integer.parseInt(customY.getDefaultValue()))
                .setTextGetter(value -> Component.literal(value + "%"))
                .setTooltip(
                    Component.literal(
                        "Vertical position as a percentage of screen height (0% = top edge, 100% = bottom edge)."
                    )
                )
                .setRequirement(Requirement.isTrue(customPositionEntry))
                .setSaveConsumer(value ->
                    customY.setValue(String.valueOf(value))
                )
                .build()
        );

        category.addEntry(positionGroup.build());

        SubCategoryBuilder appearanceGroup = entryBuilder
            .startSubCategory(Component.literal("Appearance"))
            .setExpanded(false)
            .setTooltip(
                Component.literal(
                    "Size and colors used to draw the module."
                )
            );

        Setting scale = settings.getSetting("Scale");
        appearanceGroup.add(
            entryBuilder
                .startDoubleField(
                    Component.literal("Scale"),
                    scale.getDoubleValue()
                )
                .setDefaultValue(Double.parseDouble(scale.getDefaultValue()))
                .setMin(parseDoubleBound(scale, 0, 0.1))
                .setMax(parseDoubleBound(scale, 1, 10.0))
                .setTooltip(
                    Component.literal(
                        "Size multiplier applied to the module (1.0 = default size)."
                    )
                )
                .setSaveConsumer(value ->
                    scale.setValue(String.valueOf(value))
                )
                .build()
        );

        Setting textColor = settings.getSetting("Text Color");
        appearanceGroup.add(
            entryBuilder
                .startAlphaColorField(
                    Component.literal("Text Color"),
                    textColor.getColorValue()
                )
                .setDefaultValue(
                    Integer.parseInt(textColor.getDefaultValue())
                )
                .setTooltip(
                    Component.literal(
                        "Color of the displayed text (with alpha)."
                    )
                )
                .setSaveConsumer(value ->
                    textColor.setValue(String.valueOf(value))
                )
                .build()
        );

        Setting backgroundColor = settings.getSetting("Background Color");
        appearanceGroup.add(
            entryBuilder
                .startAlphaColorField(
                    Component.literal("Background Color"),
                    backgroundColor.getColorValue()
                )
                .setDefaultValue(
                    Integer.parseInt(backgroundColor.getDefaultValue())
                )
                .setTooltip(
                    Component.literal(
                        "Color of the rectangle drawn behind the text. Set alpha to 0 for no background."
                    )
                )
                .setSaveConsumer(value ->
                    backgroundColor.setValue(String.valueOf(value))
                )
                .build()
        );

        category.addEntry(appearanceGroup.build());
    }

    private static String prettyOrientation(String raw) {
        String pretty = ORIENTATION_LABELS.get(raw);
        return pretty != null ? pretty : raw;
    }

    private static int parseBound(Setting setting, int index, int fallback) {
        String[] possible = setting.getPossibleValues();
        if (possible == null || possible.length <= index) {
            return fallback;
        }
        try {
            return Integer.parseInt(possible[index]);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private static double parseDoubleBound(
        Setting setting,
        int index,
        double fallback
    ) {
        String[] possible = setting.getPossibleValues();
        if (possible == null || possible.length <= index) {
            return fallback;
        }
        try {
            return Double.parseDouble(possible[index]);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
