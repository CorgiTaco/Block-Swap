package corgitaco.blockswap.mixin;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import corgitaco.blockswap.BlockSwap;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerResources;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.Proxy;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer {

    @Inject(at = @At("RETURN"), method = "<init>")
    private void loadConfig(Thread $$0, RegistryAccess.RegistryHolder $$1, LevelStorageSource.LevelStorageAccess $$2, WorldData $$3, PackRepository $$4, Proxy $$5, DataFixer $$6, ServerResources $$7, MinecraftSessionService $$8, GameProfileRepository $$9, GameProfileCache $$10, ChunkProgressListenerFactory $$11, CallbackInfo ci) {
        BlockSwap.getConfig(true);
    }
}
