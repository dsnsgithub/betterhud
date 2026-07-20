package dsns.betterhud;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

// Optional integration: this entrypoint is only loaded when Mod Menu is
// installed. Nothing else in the mod may reference this class, or the mod
// would crash without Mod Menu.
public class ModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return HudEditorScreen::new;
    }
}
