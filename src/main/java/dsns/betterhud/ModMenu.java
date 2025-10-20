// package dsns.betterhud;

// import net.fabricmc.loader.api.FabricLoader;
// import net.minecraft.text.Text;

// import java.util.Arrays;

// import com.terraformersmc.modmenu.api.ConfigScreenFactory;
// import com.terraformersmc.modmenu.api.ModMenuApi;
// import me.shedaniel.clothconfig2.api.ConfigBuilder;
// import me.shedaniel.clothconfig2.api.ConfigCategory;
// import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;

// public class ModMenu implements ModMenuApi {
// 	@Override
// 	public ConfigScreenFactory<?> getModConfigScreenFactory() {
// 		if (!FabricLoader.getInstance().isModLoaded("cloth-config2")) {
// 			return parent -> null;
// 		}
// 		return parent -> {
// 			ConfigBuilder builder = ConfigBuilder.create()
// 					.setParentScreen(parent)
// 					.setTitle(Text.literal("BetterHUD Settings"));

// 			// same as builder.setSavingRunnable(() -> Config.serialize());
// 			builder.setSavingRunnable(Config::serialize);

// 			ConfigEntryBuilder entryBuilder = builder.entryBuilder();

// 			ConfigCategory general = builder.getOrCreateCategory(Text.literal("General"));
// 			general.addEntry(entryBuilder
// 					.startIntSlider(Text.literal("Vertical Padding"), Config.verticalPadding, 0, 40)
// 					.setDefaultValue(4)
// 					.setSaveConsumer(newValue -> Config.verticalPadding = newValue)
// 					.build());

// 			general.addEntry(entryBuilder
// 					.startIntSlider(Text.literal("Horizontal Padding"), Config.horizontalPadding, 0, 40)
// 					.setDefaultValue(4)
// 					.setSaveConsumer(newValue -> Config.horizontalPadding = newValue)
// 					.build());

// 			general.addEntry(entryBuilder
// 					.startIntSlider(Text.literal("Vertical Margin"), Config.verticalMargin, 0, 40)
// 					.setDefaultValue(1)
// 					.setSaveConsumer(newValue -> Config.verticalMargin = newValue)
// 					.build());

// 			general.addEntry(entryBuilder
// 					.startIntSlider(Text.literal("Horizontal Margin"), Config.horizontalMargin, 0, 40)
// 					.setDefaultValue(1)
// 					.setSaveConsumer(newValue -> Config.horizontalMargin = newValue)
// 					.build());

// 			general.addEntry(entryBuilder
// 					.startIntSlider(Text.literal("Line Height"), Config.lineHeight, 0, 40)
// 					.setDefaultValue(1)
// 					.setSaveConsumer(newValue -> Config.lineHeight = newValue)
// 					.build());

// 			general.addEntry(entryBuilder
// 					.startAlphaColorField(Text.literal("Text Color"), Config.textColor)
// 					.setDefaultValue(0xffffffff)
// 					.setSaveConsumer(newValue -> Config.textColor = newValue)
// 					.build());

// 			general.addEntry(entryBuilder
// 					.startAlphaColorField(Text.literal("Background Color"), Config.backgroundColor)
// 					.setDefaultValue(0x88000000)
// 					.setSaveConsumer(newValue -> Config.backgroundColor = newValue)
// 					.build());

// 			for (String modID : Config.getDefaults().keySet()) {
// 				ConfigCategory category = builder.getOrCreateCategory(Text.literal(modID));

// 				category.addEntry(entryBuilder
// 						.startBooleanToggle(Text.literal("Enabled"), Config.settings.get(modID).enabled)
// 						.setDefaultValue(Config.getDefaults().get(modID).enabled)
// 						.setSaveConsumer(newValue -> Config.settings.get(modID).enabled = newValue)
// 						.build());

// 				category.addEntry(entryBuilder
// 						.startStringDropdownMenu(Text.literal("Orientation"), Config.settings.get(modID).orientation)
// 						.setDefaultValue(Config.getDefaults().get(modID).orientation)
// 						.setSelections(Arrays.asList("top-left", "top-right", "bottom-left", "bottom-right"))
// 						.setSuggestionMode(false)
// 						.setSaveConsumer(newValue -> Config.settings.get(modID).orientation = newValue)
// 						.build());

// 				category.addEntry(entryBuilder
// 						.startBooleanToggle(Text.literal("Custom Position"), Config.settings.get(modID).customPosition)
// 						.setDefaultValue(Config.getDefaults().get(modID).customPosition)
// 						.setSaveConsumer(newValue -> Config.settings.get(modID).customPosition = newValue)
// 						.build());

// 				category.addEntry(entryBuilder
// 						.startIntSlider(Text.literal("Custom X"), Config.settings.get(modID).customX, 0, 100)
// 						.setDefaultValue(Config.getDefaults().get(modID).customX)
// 						.setSaveConsumer(newValue -> Config.settings.get(modID).customX = newValue)
// 						.build());

// 				category.addEntry(entryBuilder
// 						.startIntSlider(Text.literal("Custom Y"), Config.settings.get(modID).customY, 0, 100)
// 						.setDefaultValue(Config.getDefaults().get(modID).customY)
// 						.setSaveConsumer(newValue -> Config.settings.get(modID).customY = newValue)
// 						.build());
// 			}

// 			return builder.build();
// 		};
// 	}
// }