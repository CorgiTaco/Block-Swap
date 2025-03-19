package dev.corgitaco.blockswap.swappredicate;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.List;

public record ConditionsPassBlockSwapPredicate(List<BlockSwapPredicate> predicates, int passedConditions) implements BlockSwapPredicate {

    @Override
    public boolean test(Level level, ChunkAccess chunk, int localX, int localY, int localZ, BlockState lastState) {
        int passedConditions = 0;

        for (BlockSwapPredicate predicate : this.predicates) {
            if (predicate.test(level, chunk, localX, localY, localZ, lastState)) {
                passedConditions++;
                if (passedConditions >= this.passedConditions) {
                    return true;
                }
            }
        }

        return false;
    }
}
