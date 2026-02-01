package dsns.betterhud.mods;

import dsns.betterhud.util.BaseMod;
import dsns.betterhud.util.CustomText;
import dsns.betterhud.util.ModSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;

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
            lastPingSent = currentTime;

            ServerInfo serverData = client.getCurrentServerEntry();
            if (serverData != null) {
                fetchManualPing(serverData.address);
            }
        }

        if (lastPingValue == -1) {
            return new CustomText("... ms", getModSettings());
        }

        return new CustomText(lastPingValue + " ms", getModSettings());
    }

    private void fetchManualPing(String addressStr) {
        CompletableFuture.runAsync(() -> {
            try {
                ServerAddress address = ServerAddress.parse(addressStr);
                try (Socket socket = new Socket()) {
                    socket.connect(new InetSocketAddress(address.getAddress(), address.getPort()), 3000);
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    DataInputStream in = new DataInputStream(socket.getInputStream());

                    // 1. Handshake Packet (ID 0x00, State 1)
                    ByteArrayOutputStream b = new ByteArrayOutputStream();
                    DataOutputStream handshake = new DataOutputStream(b);
                    writeVarInt(handshake, 0x00);
                    writeVarInt(handshake, 765); // Protocol version for 1.20.4+
                    writeString(handshake, address.getAddress());
                    handshake.writeShort(address.getPort());
                    writeVarInt(handshake, 1);
                    writePacket(out, b);

                    // 2. Status Request Packet (ID 0x00)
                    b.reset();
                    writeVarInt(handshake, 0x00);
                    writePacket(out, b);

                    // 3. Ping Packet (ID 0x01)
                    long startTime = System.currentTimeMillis();
                    b.reset();
                    writeVarInt(handshake, 0x01);
                    handshake.writeLong(startTime);
                    writePacket(out, b);

                    // 4. Read Response
                    readVarInt(in); // Size of packet
                    int packetId = readVarInt(in);
                    if (packetId == 0x01) {
                        long echoedTime = in.readLong();
                        lastPingValue = System.currentTimeMillis() - echoedTime;
                    }
                }
            } catch (Exception e) {
                // Server might be blocking rapid pings or connection timed out
                lastPingValue = -2; // Indicator for "Timed Out/Error"
            }
        });
    }

    private void writeVarInt(DataOutputStream out, int value) throws IOException {
        while ((value & -128) != 0) {
            out.writeByte(value & 127 | 128);
            value >>>= 7;
        }
        out.writeByte(value);
    }

    private int readVarInt(DataInputStream in) throws IOException {
        int i = 0;
        int j = 0;
        while (true) {
            byte b = in.readByte();
            i |= (b & 127) << j++ * 7;
            if (j > 5)
                throw new RuntimeException("VarInt too big");
            if ((b & 128) != 128)
                break;
        }
        return i;
    }

    private void writeString(DataOutputStream out, String s) throws IOException {
        byte[] bytes = s.getBytes("UTF-8");
        writeVarInt(out, bytes.length);
        out.write(bytes);
    }

    private void writePacket(DataOutputStream out, ByteArrayOutputStream b) throws IOException {
        writeVarInt(out, b.size());
        out.write(b.toByteArray());
    }
}