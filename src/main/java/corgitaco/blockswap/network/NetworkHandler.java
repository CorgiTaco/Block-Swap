package corgitaco.blockswap.network;

import corgitaco.blockswap.network.packet.ClientConfigSyncPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class NetworkHandler {
    private static final Identifier CONFIG_PACKET = new Identifier("blockswap", "config");

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(CONFIG_PACKET, (client, handler, buf, responseSender) ->
                ClientConfigSyncPacket.handle(client, ClientConfigSyncPacket.readFromPacket(buf)));
    }

    public static void sendToClient(ServerPlayerEntity playerEntity, ClientConfigSyncPacket objectToSend) {
        PacketByteBuf buf = PacketByteBufs.create();
        ClientConfigSyncPacket.writeToPacket(objectToSend, buf);
        ServerPlayNetworking.send(playerEntity, CONFIG_PACKET, buf);
    }
}