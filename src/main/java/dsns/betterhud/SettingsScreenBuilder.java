package dsns.betterhud;

import dsns.betterhud.util.BaseMod;
import dsns.betterhud.util.Setting;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Builds the Cloth Config settings screen opened from the HUD editor's
 * Settings button. Kept free of Mod Menu classes so the editor can use it
 * when Mod Menu is not installed; Cloth Config classes are only touched
 * after the isModLoaded check.
 */
public final class SettingsScreenBuilder {

    // Placement is edited by dragging in the HUD editor, so these settings
    // stay out of the settings screen. They remain in the config file as the
    // persistence format the editor writes.
    private static final Set<String> EDITOR_MANAGED_SETTINGS = Set.of(
        "Orientation",
        "Custom Position",
        "Custom X",
        "Custom Y"
    );

    private SettingsScreenBuilder() {}

    public static boolean settingsScreenAvailable() {
        return FabricLoader.getInstance().isModLoaded("cloth-config2");
    }

    /**
     * Builds the settings screen (everything except placement), or returns
     * null when Cloth Config is not installed.
     */
    public static Screen createSettingsScreen(Screen parent) {
        if (!settingsScreenAvailable()) {
            return null;
        }

        ConfigBuilder builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(Component.literal("BetterHUD Settings"));

        builder.setSavingRunnable(Config::serialize);

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        for (BaseMod mod : BetterHUD.mods) {
            ConfigCategory category = builder.getOrCreateCategory(
                Component.literal(mod.getModID())
            );

            for (Map.Entry<String, Setting> entry : mod
                .getModSettings()
                .getSettings()
                .entrySet()) {
                String key = entry.getKey();
                Setting setting = entry.getValue();

                if (EDITOR_MANAGED_SETTINGS.contains(key)) continue;

                if (setting.getType().equals("boolean")) {
                    category.addEntry(
                        entryBuilder
                            .startBooleanToggle(
                                Component.literal(key),
                                setting.getBooleanValue()
                            )
                            .setDefaultValue(
                                Boolean.valueOf(setting.getDefaultValue())
                            )
                            .setSaveConsumer(value ->
                                setting.setValue(String.valueOf(value))
                            )
                            .build()
                    );
                } else if (setting.getType().equals("string")) {
                    category.addEntry(
                        entryBuilder
                            .startStringDropdownMenu(
                                Component.literal(key),
                                setting.getStringValue()
                            )
                            .setSelections(
                                Arrays.asList(setting.getPossibleValues())
                            )
                            .setDefaultValue(setting.getDefaultValue())
                            .setSaveConsumer(value -> setting.setValue(value))
                            .build()
                    );
                } else if (setting.getType().equals("integer")) {
                    category.addEntry(
                        entryBuilder
                            .startIntField(
                                Component.literal(key),
                                setting.getIntValue()
                            )
                            .setDefaultValue(
                                Integer.parseInt(setting.getDefaultValue())
                            )
                            .setSaveConsumer(value ->
                                setting.setValue(String.valueOf(value))
                            )
                            .build()
                    );
                } else if (setting.getType().equals("double")) {
                    category.addEntry(
                        entryBuilder
                            .startDoubleField(
                                Component.literal(key),
                                setting.getDoubleValue()
                            )
                            .setDefaultValue(
                                Double.parseDouble(setting.getDefaultValue())
                            )
                            .setSaveConsumer(value ->
                                setting.setValue(String.valueOf(value))
                            )
                            .build()
                    );
                } else if (setting.getType().equals("color")) {
                    category.addEntry(
                        entryBuilder
                            .startAlphaColorField(
                                Component.literal(key),
                                setting.getColorValue()
                            )
                            .setDefaultValue(
                                Integer.parseInt(setting.getDefaultValue())
                            )
                            .setSaveConsumer(value ->
                                setting.setValue(String.valueOf(value))
                            )
                            .build()
                    );
                }
            }
        }

        return builder.build();
    }
}
