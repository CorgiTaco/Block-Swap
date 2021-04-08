package corgitaco.blockreplacer.network.packet;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corgitaco.blockreplacer.BlockReplacer;
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

    private final SimpleMapCodec codec;

    public ClientConfigSyncPacket(SimpleMapCodec codec) {
        this.codec = codec;
    }

    public static void writeToPacket(ClientConfigSyncPacket packet, PacketBuffer buf) {
        try {
            buf.writeWithCodec(SimpleMapCodec.PACKET_CODEC, packet.codec);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static ClientConfigSyncPacket readFromPacket(PacketBuffer buf) {
        try {
            return new ClientConfigSyncPacket(buf.readWithCodec(SimpleMapCodec.PACKET_CODEC));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void handle(ClientConfigSyncPacket message, Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection().getReceptionSide().isClient()) {
            ctx.get().enqueueWork(() -> {
                BlockReplacer.blockToBlockMap = message.codec.getBlockBlockMap();
            });
        }
        ctx.get().setPacketHandled(true);
    }


    public static class SimpleMapCodec {

        public static final Codec<SimpleMapCodec> PACKET_CODEC = RecordCodecBuilder.create((builder) -> {
            return builder.group(Codec.unboundedMap(ResourceLocation.CODEC, ResourceLocation.CODEC).fieldOf("currentYearTime").forGetter((seasonContext) -> {
                Map<ResourceLocation, ResourceLocation> newMap = new HashMap<>();
                seasonContext.blockBlockMap.forEach((key, value) -> newMap.put(Registry.BLOCK.getKey(key), Registry.BLOCK.getKey(value)));
                return newMap;
            })).apply(builder, (map) -> {
                Reference2ReferenceOpenHashMap<Block, Block> newMap = new Reference2ReferenceOpenHashMap<>();
                map.forEach((key, result) -> newMap.put(Registry.BLOCK.get(key), Registry.BLOCK.get(result)));
                return new SimpleMapCodec(newMap);
            });
        });

        private final Reference2ReferenceOpenHashMap<Block, Block> blockBlockMap;

        public SimpleMapCodec(Reference2ReferenceOpenHashMap<Block, Block> blockBlockMap) {
            this.blockBlockMap = blockBlockMap;
        }

        public Reference2ReferenceOpenHashMap<Block, Block> getBlockBlockMap() {
            return blockBlockMap;
        }
    }
}