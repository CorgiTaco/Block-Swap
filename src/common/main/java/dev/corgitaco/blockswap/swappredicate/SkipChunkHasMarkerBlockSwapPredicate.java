package dev.corgitaco.blockswap.swappredicate;

import dev.corgitaco.blockswap.BlockSwap;
import dev.corgitaco.blockswap.data.BlockSwapLevelChunkData;
import dev.corgitaco.blockswap.data.BlockSwapProtoChunkData;
import dev.corgitaco.dataanchor.data.registry.TrackedDataRegistries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.Optional;

public record SkipChunkHasMarkerBlockSwapPredicate(String s) implements BlockSwapPredicate {
    @Override
    public boolean test(Level level, ChunkAccess chunk, int localX, int localY, int localZ, BlockState lastState) {
        Optional<BlockSwapLevelChunkData> blockSwapLevelChunkData = TrackedDataRegistries.CHUNK.get(BlockSwap.BLOCK_SWAP_CHUNK_DATA, chunk);
        if (blockSwapLevelChunkData.isPresent()) {
            BlockSwapLevelChunkData chunkData = blockSwapLevelChunkData.get();
            return   chunkData.getPlaceHolders().contains(s);


        }
        Optional<BlockSwapProtoChunkData> blockSwapProtoChunkData = TrackedDataRegistries.CHUNK.get(BlockSwap.BLOCK_SWAP_WORLD_GEN_DATA, chunk);

        if (blockSwapProtoChunkData.isPresent()){
            BlockSwapProtoChunkData protoChunkData = blockSwapProtoChunkData.get();
            return protoChunkData.getPlaceHolders().contains(s);
        }

        return false;
    }
}
