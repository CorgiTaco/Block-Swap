package dev.corgitaco.blockswap.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.corgitaco.blockswap.util.ChunkSectionBlockStateSwapper;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LevelChunkSection.class)
public class LevelChunkSectionMixin implements ChunkSectionBlockStateSwapper {

    @Unique
    @Nullable
    private Int2IntMap swapper;

    @WrapMethod(method = "setBlockState(IIILnet/minecraft/world/level/block/state/BlockState;Z)Lnet/minecraft/world/level/block/state/BlockState;")
    private BlockState blockSwap$SwapBlockState(int x, int y, int z, BlockState blockState, boolean b, Operation<BlockState> original) {
        if (swapper == null) {
            return original.call(x, y, z, blockState, b);
        }

        int id = swapper.getOrDefault(Block.BLOCK_STATE_REGISTRY.getId(blockState), -1);
        if (id == -1) {
            return original.call(x, y, z, blockState, b);
        } else {
            BlockState newState = Block.BLOCK_STATE_REGISTRY.byId(id);
            return original.call(x, y, z, newState, b);
        }
    }

    @Override
    public void blockSwap$createSwapper(Int2IntMap swapper) {
        this.swapper = swapper;
    }
}
