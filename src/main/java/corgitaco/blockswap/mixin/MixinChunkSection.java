package corgitaco.blockswap.mixin;

import corgitaco.blockswap.BlockSwap;
import net.minecraft.block.BlockState;
import net.minecraft.world.chunk.ChunkSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkSection.class)
public abstract class MixinChunkSection {

    @Shadow
    public abstract BlockState setBlockState(int x, int y, int z, BlockState state, boolean flag);

    @Inject(method = "setBlockState(IIILnet/minecraft/block/BlockState;Z)Lnet/minecraft/block/BlockState;", at = @At("HEAD"), cancellable = true)
    private void replaceState(int x, int y, int z, BlockState state, boolean flag, CallbackInfoReturnable<BlockState> cir) {
        if (BlockSwap.blockToBlockMap.containsKey(state.getBlock())) {
            cir.setReturnValue(setBlockState(x, y, z, BlockSwap.remapState(state), flag));
        }
    }
}
