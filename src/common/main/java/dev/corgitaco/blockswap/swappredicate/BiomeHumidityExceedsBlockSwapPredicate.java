package dev.corgitaco.blockswap.swappredicate;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.function.Supplier;

public record BiomeHumidityExceedsBlockSwapPredicate(float humidity, boolean useFiddle) implements BlockSwapPredicate {

    private static final ThreadLocal<BlockPos.MutableBlockPos> POS = ThreadLocal.withInitial(BlockPos.MutableBlockPos::new);
    
    private static final Supplier<BlockPos.MutableBlockPos> POS_SUPPLIER = POS::get;

    @Override
    public boolean test(Level level, ChunkAccess chunk, int localX, int localY, int localZ, BlockState lastState) {
        return getBiome(level, chunk, localX, localY, localZ, POS_SUPPLIER, this.useFiddle).value().climateSettings.getDownFall() > humidity;
    }
}
