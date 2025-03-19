package dev.corgitaco.blockswap.swappredicate;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.Optional;

public record IsBlockEntityBlockSwapPredicate(Optional<Either<ResourceKey<BlockEntityType<?>>, TagKey<BlockEntityType<?>>>> check,
                                              boolean useFiddle) implements BlockSwapPredicate {

    private static final ThreadLocal<BlockPos.MutableBlockPos> POS = ThreadLocal.withInitial(BlockPos.MutableBlockPos::new);

    @Override
    public boolean test(Level level, ChunkAccess chunk, int localX, int localY, int localZ, BlockState lastState) {
        BlockPos.MutableBlockPos mutableBlockPos = POS.get();
        mutableBlockPos.set(localX, localY, localZ);
        BlockEntity blockEntity = chunk.getBlockEntity(mutableBlockPos);
        if (blockEntity == null) {
            return false;
        }

        if (check().isPresent()) {
            Either<ResourceKey<BlockEntityType<?>>, TagKey<BlockEntityType<?>>> resourceKeyTagKeyEither = check.orElseThrow();

            Optional<ResourceKey<BlockEntityType<?>>> left = resourceKeyTagKeyEither.left();
            if (left.isPresent()) {
                return blockEntity.getType().builtInRegistryHolder().is(left.get());
            }
            Optional<TagKey<BlockEntityType<?>>> right = resourceKeyTagKeyEither.right();
            if (right.isPresent()) {
                return blockEntity.getType().builtInRegistryHolder().is(right.get());
            }
        }

        return true;
    }
}
