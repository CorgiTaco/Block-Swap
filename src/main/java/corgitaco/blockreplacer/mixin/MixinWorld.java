package corgitaco.blockreplacer.mixin;

import corgitaco.blockreplacer.BlockReplacer;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public abstract class MixinWorld {

    @Shadow
    public abstract boolean setBlock(BlockPos pos, BlockState state, int i, int flags);

    @Inject(method = "setBlock(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z", at = @At("INVOKE"), cancellable = true)
    private void isIncompatibleBlock(BlockPos pos, BlockState state, int i, int flags, CallbackInfoReturnable<Boolean> cir) {
        if (BlockReplacer.blockToBlockMap.containsKey(state.getBlock())) {
            cir.setReturnValue(setBlock(pos, BlockReplacer.remapState(state), i, flags));
        }
    }
}
