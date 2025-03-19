package dev.corgitaco.blockswap.swappredicate;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.List;

public record AllOfBlockSwapPredicate(List<BlockSwapPredicate> predicates) implements BlockSwapPredicate {


    @Override
    public boolean test(Level level, ChunkAccess chunk, int localX, int localY, int localZ, BlockState lastState) {
        for (BlockSwapPredicate predicate : this.predicates) {
            if (!predicate.test(level, chunk, localX, localY, localZ, lastState)) {
                return false;
            }
        }

        return true;
    }
}
