package dsns.betterhud.util;

import net.minecraft.client.Minecraft;

public interface BaseMod {
    public String getModID();

    public ModSettings getModSettings();

    public CustomText onStartTick(Minecraft client);
}
