package corgitaco.blockswap.mixin;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DataFixer;
import corgitaco.blockswap.BlockSwap;
import corgitaco.blockswap.config.BlockSwapConfig;
import corgitaco.blockswap.config.MissingBlockIDsConfig;
import corgitaco.blockswap.swapper.Swapper;
import corgitaco.blockswap.util.jankson.JanksonJsonOps;
import corgitaco.blockswap.util.jankson.JanksonUtil;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.Proxy;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer {

    @Inject(at = @At("RETURN"), method = "<init>")
    private void blockSwap_loadConfig(Thread $$0, LevelStorageSource.LevelStorageAccess $$1, PackRepository $$2, WorldStem $$3, Proxy $$4, DataFixer $$5, Services $$6, ChunkProgressListenerFactory $$7, CallbackInfo ci) {
        BlockSwapConfig config = BlockSwapConfig.getConfig(true);
        MissingBlockIDsConfig missingBlockIDsConfig = MissingBlockIDsConfig.getConfig(true);

        if (config.generateAllKnownStates()) {
            Map<Block, List<BlockState>> allKnownStates = new TreeMap<>(Comparator.comparing(block -> Registry.BLOCK.getKey(block).toString()));
            for (Block block : Registry.BLOCK) {
                allKnownStates.computeIfAbsent(block, key -> key.getStateDefinition().getPossibleStates());
            }

//        Map<Fluid, List<FluidState>> allKnownFluidStates = new TreeMap<>(Comparator.comparing(block -> Registry.FLUID.getKey(block).toString()));
//        for (Fluid block : Registry.FLUID) {
//            allKnownFluidStates.computeIfAbsent(block, key -> key.getStateDefinition().getPossibleStates());
//        }


            allKnownStates.forEach((block, blockStates) -> {
                ResourceLocation blockKey = Registry.BLOCK.getKey(block);
                JanksonUtil.createConfig(BlockSwap.CONFIG_PATH.resolve("known_states").resolve(blockKey.getNamespace()).resolve(blockKey.getPath() + ".json5"), Swapper.COMMENTED_STATE_CODEC.listOf(), JanksonUtil.HEADER_CLOSED, ImmutableMap.of(), JanksonJsonOps.INSTANCE, blockStates);
            });
        }
    }
}
