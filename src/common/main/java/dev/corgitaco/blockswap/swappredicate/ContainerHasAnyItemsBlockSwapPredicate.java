package dev.corgitaco.blockswap.swappredicate;

import dev.corgitaco.blockswap.itempredicate.ItemPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.List;
import java.util.function.Supplier;

public record ContainerHasAnyItemsBlockSwapPredicate(List<ItemPredicate> itemChecks) implements BlockSwapPredicate {
    private static final ThreadLocal<BlockPos.MutableBlockPos> POS = ThreadLocal.withInitial(BlockPos.MutableBlockPos::new);

    private static final Supplier<BlockPos.MutableBlockPos> POS_SUPPLIER = POS::get;


    @Override
    public boolean test(Level level, ChunkAccess chunk, int localX, int localY, int localZ, BlockState lastState) {
        BlockPos.MutableBlockPos mutableBlockPos = POS.get();
        mutableBlockPos.set(localX, localY, localZ);
        BlockEntity blockEntity = chunk.getBlockEntity(mutableBlockPos);
        if (blockEntity instanceof Container container) {
            for (ItemPredicate itemCheck : this.itemChecks) {
                if (container.hasAnyMatching(itemCheck::test)) {
                    return true;
                }
            }
        }
        return false;
    }
}
