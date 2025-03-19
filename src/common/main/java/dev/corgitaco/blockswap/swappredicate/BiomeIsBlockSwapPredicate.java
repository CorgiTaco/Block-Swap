package dev.corgitaco.blockswap.swappredicate;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.function.Supplier;

public record BiomeIsBlockSwapPredicate(Either<ResourceKey<Biome>, TagKey<Biome>> check, boolean useFiddle) implements BlockSwapPredicate {
    private static final ThreadLocal<BlockPos.MutableBlockPos> POS = ThreadLocal.withInitial(BlockPos.MutableBlockPos::new);
    private static final Supplier<BlockPos.MutableBlockPos> POS_SUPPLIER = POS::get;


    @Override
    public boolean test(Level level, ChunkAccess chunk, int localX, int localY, int localZ, BlockState lastState) {
        Holder<Biome> biome = getBiome(level, chunk, localX, localY, localZ, POS_SUPPLIER, this.useFiddle);

        if (check.left().isPresent()) {
            biome.is(check.left().get());
        }
        if (check.right().isPresent()) {
            return biome.is(check.right().get());
        }

        return false;
    }
}
