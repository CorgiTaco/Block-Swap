package corgitaco.blockswap.mixin;

import corgitaco.blockswap.BlockSwap;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkHolder.class)
public class MixinChunkHolder {
    @Inject(method = "flushUpdates", at = @At("HEAD"))
    private void runChunkUpdates(WorldChunk chunk, CallbackInfo ci) {
        BlockSwap.runRetroGenerator(chunk.getWorld(), chunk.getSectionArray(), chunk);
    }
}
