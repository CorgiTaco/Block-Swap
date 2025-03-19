package dev.corgitaco.blockswap.swappredicate;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.ProtoChunk;

public class IsWorldGenerationBlockSwapPredicate implements BlockSwapPredicate {
    @Override
    public boolean test(Level level, ChunkAccess chunk, int localX, int localY, int localZ, BlockState lastState) {
        return chunk instanceof ProtoChunk && !(chunk instanceof ImposterProtoChunk);
    }
}
