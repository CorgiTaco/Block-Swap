package dev.corgitaco.blockswap;

import dev.corgitaco.blockswap.data.BlockSwapLevelChunkData;
import dev.corgitaco.blockswap.data.BlockSwapProtoChunkData;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.registry.TrackedDataRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.ProtoChunk;

public class BlockSwap {
    public static final String MOD_ID = "blockswap";

    public static final TrackedDataKey<BlockSwapLevelChunkData> BLOCK_SWAP_CHUNK_DATA = TrackedDataRegistries.CHUNK.register(
            id("block_swap_level_data"),
            BlockSwapLevelChunkData.class,
            (trackedDataKey, chunkAccess) -> {
                if (chunkAccess instanceof LevelChunk chunk) {
                    return new BlockSwapLevelChunkData(trackedDataKey, chunk);
                }
                return null;
            }
    );

    public static final TrackedDataKey<BlockSwapProtoChunkData> BLOCK_SWAP_WORLD_GEN_DATA = TrackedDataRegistries.CHUNK.register(
            id("block_swap_world_gen_data"),
            BlockSwapProtoChunkData.class,
            (trackedDataKey, chunkAccess) -> {
                if (chunkAccess instanceof ProtoChunk chunk && !(chunkAccess instanceof ImposterProtoChunk)) {
                    return new BlockSwapProtoChunkData(trackedDataKey, chunk);
                }
                return null;
            }
    );

    public static void initialize() {
        System.out.println("Hello World!");
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
