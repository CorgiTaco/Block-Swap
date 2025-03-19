package dev.corgitaco.blockswap.swappredicate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.function.Supplier;

public interface BlockSwapPredicate {


    boolean test(Level level, ChunkAccess chunk, int localX, int localY, int localZ, BlockState lastState);

    default Holder<Biome> getBiome(Level level, ChunkAccess chunk, int localX, int localY, int localZ, Supplier<BlockPos.MutableBlockPos> mutableBlockPosSupplier, boolean useFiddle) {
        if (!useFiddle) {
            return chunk.getNoiseBiome(localX, localY, localZ);
        } else {
            ChunkPos pos = chunk.getPos();
            BlockPos.MutableBlockPos mutableBlockPos = mutableBlockPosSupplier.get();
            mutableBlockPos.set(pos.getBlockX(localX), localY, pos.getBlockZ(localZ));
            return level.getBiome(mutableBlockPos);
        }
    }
}
