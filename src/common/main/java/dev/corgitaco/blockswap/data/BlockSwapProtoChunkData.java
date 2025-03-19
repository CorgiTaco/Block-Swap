package dev.corgitaco.blockswap.data;

import dev.corgitaco.blockswap.util.ChunkSectionBlockStateSwapper;
import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.type.chunk.ChunkTrackedData;
import dev.corgitaco.dataanchor.data.type.chunk.ProtoChunkTrackedData;
import it.unimi.dsi.fastutil.ints.Int2IntMaps;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;

public class BlockSwapProtoChunkData extends ProtoChunkTrackedData {
    public BlockSwapProtoChunkData(TrackedDataKey<? extends ChunkTrackedData> trackedDataKey, ProtoChunk protoChunk) {
        super(trackedDataKey, protoChunk);

        for (LevelChunkSection section : protoChunk.getSections()) {
            if (section instanceof ChunkSectionBlockStateSwapper chunkSectionBlockStateSwapper) {
                chunkSectionBlockStateSwapper.blockSwap$createSwapper(Int2IntMaps.singleton(Block.BLOCK_STATE_REGISTRY.getId(Blocks.STONE.defaultBlockState()), Block.BLOCK_STATE_REGISTRY.getId(Blocks.WATER.defaultBlockState())));
            }
        }
    }
}
