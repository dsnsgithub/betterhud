package dsns.betterhud.mods;

import dsns.betterhud.util.BaseMod;
import dsns.betterhud.util.CustomText;
import dsns.betterhud.util.ModSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class Ping implements BaseMod {

    private static final ModSettings SETTINGS = new ModSettings("top-left");

    private static long lastPingSent = 0;
    private static long lastPingValue = -1;
    private static final long PING_INTERVAL_MS = 10000;

    @Override
    public String getModID() {
        return "Ping";
    }

    @Override
    public ModSettings getModSettings() {
        return SETTINGS;
    }

    @Override
    public CustomText onStartTick(MinecraftClient client) {
        if (client.player == null || client.getNetworkHandler() == null)
            return null;

        long currentTime = System.currentTimeMillis();

        if (currentTime - lastPingSent >= PING_INTERVAL_MS) {
            lastPingSent = now;
            client.getNetworkHandler().sendPacket(
                    new net.minecraft.network.packet.c2s.query.PingRequestC2SPacket(currentTime));
        }

        if (lastPingValue == -1) {
            return new CustomText("... ms", getModSettings());
        }

        return new CustomText(lastPingValue + " ms", getModSettings());
    }

    // Called when server responds
    public static void handlePingResponse(long sentTime) {
        lastPingValue = System.currentTimeMillis() - sentTime;
    }
}
