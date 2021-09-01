package corgitaco.blockswap.network.packet;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corgitaco.blockswap.BlockSwap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("InstantiationOfUtilityClass")
public class ClientConfigSyncPacket {

    private final ConfigCodec codec;

    public ClientConfigSyncPacket(ConfigCodec codec) {
        this.codec = codec;
    }

    public static void writeToPacket(ClientConfigSyncPacket packet, PacketByteBuf buf) {
        buf.encode(ConfigCodec.PACKET_CODEC, packet.codec);
    }

    public static ClientConfigSyncPacket readFromPacket(PacketByteBuf buf) {
        return new ClientConfigSyncPacket(buf.decode(ConfigCodec.PACKET_CODEC));
    }

    public static void handle(MinecraftClient client, ClientConfigSyncPacket message) {
        client.execute(() -> {
            BlockSwap.blockToBlockMap = message.codec.getBlockBlockMap();
            BlockSwap.retroGen = message.codec.isRetroGen();
        });
    }


    public static class ConfigCodec {

        public static final Codec<ConfigCodec> PACKET_CODEC = RecordCodecBuilder.create((builder) -> builder.group(Codec.unboundedMap(Identifier.CODEC, Identifier.CODEC).fieldOf("blockToBlockMap").forGetter((configCodec) -> {
            Map<Identifier, Identifier> newMap = new HashMap<>();
            configCodec.blockBlockMap.forEach((key, value) -> newMap.put(Registry.BLOCK.getKey(key).map(RegistryKey::getValue).orElse(null), Registry.BLOCK.getKey(value).map(RegistryKey::getValue).orElse(null)));
            return newMap;
        }), Codec.BOOL.fieldOf("retroGen").forGetter(configCodec -> configCodec.retroGen)).apply(builder, (map, retroGen) -> {
            Reference2ReferenceOpenHashMap<Block, Block> newMap = new Reference2ReferenceOpenHashMap<>();
            map.forEach((key, result) -> newMap.put(Registry.BLOCK.get(key), Registry.BLOCK.get(result)));
            return new ConfigCodec(newMap, retroGen);
        }));

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