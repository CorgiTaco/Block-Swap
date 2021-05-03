package corgitaco.blockswap.mixin;

import corgitaco.blockswap.BlockSwap;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ChunkHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkHolder.class)
public class MixinChunkHolder {

    @Inject(method = "broadcastChanges", at = @At("HEAD"))
    private void runChunkUpdates(Chunk chunk, CallbackInfo ci) {
        BlockSwap.runRetroGenerator(chunk.getLevel(), chunk.getSections(), chunk);
    }
}
