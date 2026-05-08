package dsns.betterhud.mixin;

//? if mc < "26" {
/*import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Minecraft.class)
public interface MinecraftClientAccessor {
    @Accessor("currentFps")
    static int getCurrentFPS() {
        return 0;
    }
}*/
//?}
//? if mc >= "26" {
// On 26.1+ Minecraft#getFps() is publicly accessible, so this mixin is not needed
// and not registered in betterhud.mixins.json. Keep this file as a Stonecutter-gated
// stub so it exists in source for 1.21.x active variants.
final class MinecraftClientAccessor_Disabled {
    private MinecraftClientAccessor_Disabled() {}
}
//?}
