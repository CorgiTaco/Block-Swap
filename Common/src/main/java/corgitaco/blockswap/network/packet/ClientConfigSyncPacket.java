package corgitaco.blockswap.network.packet;

import corgitaco.blockswap.config.BlockSwapConfig;
import corgitaco.blockswap.network.packet.util.PacketHandle;
import net.minecraft.network.FriendlyByteBuf;

public class ClientConfigSyncPacket implements PacketHandle {

    private final BlockSwapConfig blockSwapConfig;

    public ClientConfigSyncPacket(BlockSwapConfig blockSwapConfig) {
        this.blockSwapConfig = blockSwapConfig;
    }

    public static void writeToPacket(ClientConfigSyncPacket packet, FriendlyByteBuf buf) {
        buf.writeWithCodec(BlockSwapConfig.CODEC, packet.blockSwapConfig);
    }

    public static ClientConfigSyncPacket readFromPacket(FriendlyByteBuf buf) {
        return new ClientConfigSyncPacket(buf.readWithCodec(BlockSwapConfig.CODEC));
    }

    @Override
    public void handle() {
        BlockSwapConfig.getConfig(blockSwapConfig);
    }
}