package dev.corgitaco.blockswap.data;

import dev.corgitaco.dataanchor.data.registry.TrackedDataKey;
import dev.corgitaco.dataanchor.data.type.chunk.ChunkBlockStateInterceptor;
import dev.corgitaco.dataanchor.data.type.chunk.LevelChunkTrackedData;
import dev.corgitaco.dataanchor.data.type.chunk.ServerLevelChunkTrackedData;
import dev.corgitaco.dataanchor.data.type.chunk.SyncedLevelChunkTrackedData;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BlockSwapLevelChunkData extends ServerLevelChunkTrackedData implements ChunkBlockStateInterceptor {

    private final Set<String> placeHolders = new HashSet<>();

    Map<BlockState, BlockState> blockStateMap = Util.make(new Reference2ReferenceOpenHashMap<>(), map -> map.put(Blocks.STONE.defaultBlockState(), Blocks.WATER.defaultBlockState()));


    public BlockSwapLevelChunkData(TrackedDataKey<? extends LevelChunkTrackedData> trackedDataKey, LevelChunk levelChunk) {
        super(trackedDataKey, levelChunk);
    }


    @Override
    public CompoundTag save() {
        return super.save();
    }

    @Override
    public void load(CompoundTag compoundTag) {
    }

    @Override
    public BlockState getNewState(BlockPos blockPos, BlockState blockState, BlockState blockState1, boolean b) {
        return blockStateMap.getOrDefault(blockState, blockState1);
    }
}
