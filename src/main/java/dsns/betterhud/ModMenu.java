// package dsns.betterhud;

// import net.fabricmc.loader.api.FabricLoader;
// import net.minecraft.client.gui.screen.Screen;
// import net.minecraft.text.Text;

// import com.terraformersmc.modmenu.api.ConfigScreenFactory;
// import com.terraformersmc.modmenu.api.ModMenuApi;
// import me.shedaniel.clothconfig2.api.ConfigBuilder;
// import me.shedaniel.clothconfig2.api.ConfigCategory;
// import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;

// public class ModMenu implements ModMenuApi {
//     @Override
//     public ConfigScreenFactory<?> getModConfigScreenFactory() {
//         if (!FabricLoader.getInstance().isModLoaded("cloth-config2")) {
//             return parent -> null;
//         }
//         return parent -> {
//             ConfigBuilder builder = ConfigBuilder.create()
//                     .setParentScreen(parent)
//                     .setTitle(Text.literal(null));

//             ConfigCategory category = builder.getOrCreateCategory(Text.translatable("config.betterhud.category"));
//             ConfigEntryBuilder entryBuilder = builder.entryBuilder();
            
//             return builder.build();
//         };
//     }
// }