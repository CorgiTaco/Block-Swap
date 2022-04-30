package corgitaco.blockswap.mixin;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import corgitaco.blockswap.BlockSwap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.Proxy;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer {

    @Inject(at = @At("RETURN"), method = "<init>")
    private void blockSwap_loadConfig(Thread $$0, LevelStorageSource.LevelStorageAccess $$1, PackRepository $$2, WorldStem $$3, Proxy $$4, DataFixer $$5, MinecraftSessionService $$6, GameProfileRepository $$7, GameProfileCache $$8, ChunkProgressListenerFactory $$9, CallbackInfo ci) {
        BlockSwap.getConfig(true);
    }
}
