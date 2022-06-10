package corgitaco.blockswap.mixin;

import corgitaco.blockswap.BlockSwap;
import corgitaco.blockswap.config.BlockSwapConfig;
import corgitaco.blockswap.swapper.Swapper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelChunkSection.class)
public abstract class MixinChunkSection {

    @Shadow
    public abstract BlockState setBlockState(int x, int y, int z, BlockState state, boolean flag);

    @Inject(method = "setBlockState(IIILnet/minecraft/world/level/block/state/BlockState;Z)Lnet/minecraft/world/level/block/state/BlockState;", at = @At("HEAD"), cancellable = true)
    private void replaceState(int x, int y, int z, BlockState state, boolean flag, CallbackInfoReturnable<BlockState> cir) {
        if (!BlockSwap.retroGen) {
            if (BlockSwapConfig.getConfig(false).contains(state)) {
                cir.setReturnValue(setBlockState(x, y, z, Swapper.remapState(state), flag));
            }
        }
    }
}
