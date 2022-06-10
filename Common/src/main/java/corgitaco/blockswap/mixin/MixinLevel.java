package corgitaco.blockswap.mixin;

import corgitaco.blockswap.config.BlockSwapConfig;
import corgitaco.blockswap.swapper.Swapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public abstract class MixinLevel {

    @Shadow
    public abstract boolean setBlock(BlockPos pos, BlockState state, int i);

    @Inject(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", at = @At("HEAD"), cancellable = true)
    private void isIncompatibleBlock(BlockPos pos, BlockState state, int i, CallbackInfoReturnable<Boolean> cir) {
        if (BlockSwapConfig.getConfig(false).contains(state)) {
            cir.setReturnValue(setBlock(pos, Swapper.remapState(state), i));
        }
    }
}
