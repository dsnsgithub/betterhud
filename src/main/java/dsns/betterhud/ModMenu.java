package dsns.betterhud;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dsns.betterhud.BetterHUD;
import dsns.betterhud.util.BaseMod;
import dsns.betterhud.util.Setting;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.math.Color;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;

public class ModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (!FabricLoader.getInstance().isModLoaded("cloth-config2")) {
            return parent -> null;
        }
        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.literal("BetterHUD Settings"));

            // same as builder.setSavingRunnable(() -> Config.serialize());
            builder.setSavingRunnable(Config::serialize);

            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            for (BaseMod mod : BetterHUD.mods) {
                ConfigCategory category = builder.getOrCreateCategory(
                    Text.literal(mod.getModID())
                );

                for (Map.Entry<String, Setting> entry : mod
                    .getModSettings()
                    .getSettings()
                    .entrySet()) {
                    String key = entry.getKey();
                    Setting setting = entry.getValue();

                    if (setting.getType().equals("boolean")) {
                        category.addEntry(
                            entryBuilder
                                .startBooleanToggle(
                                    Text.literal(key),
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
                                    Text.literal(key),
                                    setting.getStringValue()
                                )
                                .setSelections(
                                    Arrays.asList(setting.getPossibleValues())
                                )
                                .setDefaultValue(setting.getDefaultValue())
                                .setSaveConsumer(value ->
                                    setting.setValue(value)
                                )
                                .build()
                        );
                    } else if (setting.getType().equals("integer")) {
                        category.addEntry(
                            entryBuilder
                                .startIntField(
                                    Text.literal(key),
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
                                    Text.literal(key),
                                    setting.getDoubleValue()
                                )
                                .setDefaultValue(
                                    Double.parseDouble(
                                        setting.getDefaultValue()
                                    )
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
                                    Text.literal(key),
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
        };
    }
}
