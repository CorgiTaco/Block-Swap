package corgitaco.blockswap.network.packet;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corgitaco.blockswap.BlockSwap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.fml.network.NetworkEvent;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("InstantiationOfUtilityClass")
public class ClientConfigSyncPacket {

    private final ConfigCodec codec;

    public ClientConfigSyncPacket(ConfigCodec codec) {
        this.codec = codec;
    }

    public static void writeToPacket(ClientConfigSyncPacket packet, PacketBuffer buf) {
        try {
            buf.writeWithCodec(ConfigCodec.PACKET_CODEC, packet.codec);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static ClientConfigSyncPacket readFromPacket(PacketBuffer buf) {
        try {
            return new ClientConfigSyncPacket(buf.readWithCodec(ConfigCodec.PACKET_CODEC));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void handle(ClientConfigSyncPacket message, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection().getReceptionSide().isClient()) {
            ctx.get().enqueueWork(() -> {
                BlockSwap.blockToBlockMap = message.codec.getBlockBlockMap();
                BlockSwap.retroGen = message.codec.isRetroGen();
            });
        }
        ctx.get().setPacketHandled(true);
    }


    public static class ConfigCodec {

        public static final Codec<ConfigCodec> PACKET_CODEC = RecordCodecBuilder.create((builder) -> {
            return builder.group(Codec.unboundedMap(ResourceLocation.CODEC, ResourceLocation.CODEC).fieldOf("blockToBlockMap").forGetter((configCodec) -> {
                Map<ResourceLocation, ResourceLocation> newMap = new HashMap<>();
                configCodec.blockBlockMap.forEach((key, value) -> newMap.put(Registry.BLOCK.getKey(key), Registry.BLOCK.getKey(value)));
                return newMap;
            }), Codec.BOOL.fieldOf("retroGen").forGetter(configCodec -> {
                return configCodec.retroGen;
            })).apply(builder, (map, retroGen) -> {
                Reference2ReferenceOpenHashMap<Block, Block> newMap = new Reference2ReferenceOpenHashMap<>();
                map.forEach((key, result) -> newMap.put(Registry.BLOCK.get(key), Registry.BLOCK.get(result)));
                return new ConfigCodec(newMap, retroGen);
            });
        });

        private final Reference2ReferenceOpenHashMap<Block, Block> blockBlockMap;
        private final boolean retroGen;

        public ConfigCodec(Reference2ReferenceOpenHashMap<Block, Block> blockBlockMap, boolean retroGen) {
            this.blockBlockMap = blockBlockMap;
            this.retroGen = retroGen;
        }

        public Reference2ReferenceOpenHashMap<Block, Block> getBlockBlockMap() {
            return blockBlockMap;
        }

        public boolean isRetroGen() {
            return retroGen;
        }
    }
}