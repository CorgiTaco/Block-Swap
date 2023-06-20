package corgitaco.blockswap.mixin;

import corgitaco.blockswap.BlockSwap;
import corgitaco.blockswap.swapper.Swapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Level.class)
public abstract class MixinLevel {

    @Shadow
    public abstract boolean setBlock(BlockPos pos, BlockState state, int i, int flags);

    @Inject(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z", at = @At("HEAD"), cancellable = true)
    private void isIncompatibleBlock(BlockPos pos, BlockState state, int i, int flags, CallbackInfoReturnable<Boolean> cir) {
        if (BlockSwap.getConfig(false).contains(state)) {
            cir.setReturnValue(setBlock(pos, Swapper.remapState(state), i, flags));
        }
    }
}
