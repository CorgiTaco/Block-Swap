package corgitaco.blockswap.mixin;

import corgitaco.blockswap.swapper.Swapper;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkHolder.class)
public class MixinChunkHolder {

    @Inject(method = "broadcastChanges", at = @At("HEAD"))
    private void runChunkUpdates(LevelChunk chunk, CallbackInfo ci) {
        Swapper.runRetroGenerator(chunk);
    }
}
