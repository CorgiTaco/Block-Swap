package corgitaco.blockswap.mixin;

import corgitaco.blockswap.BlockSwap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.palette.UpgradeData;
import net.minecraft.world.ITickList;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.function.Consumer;

@Mixin(Chunk.class)
public abstract class MixinChunk {
    @Shadow
    @Nullable
    public abstract BlockState setBlockState(BlockPos blockPos, BlockState state, boolean flag);

    @Shadow
    @Final
    private ChunkSection[] sections;

    @Shadow
    public abstract BlockState getBlockState(BlockPos pos);

    @Inject(method = "<init>(Lnet/minecraft/world/World;Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/world/biome/BiomeContainer;Lnet/minecraft/util/palette/UpgradeData;Lnet/minecraft/world/ITickList;Lnet/minecraft/world/ITickList;J[Lnet/minecraft/world/chunk/ChunkSection;Ljava/util/function/Consumer;)V", at = @At("RETURN"))
    private void retroGen(World world, ChunkPos chunkPos, BiomeContainer biomeContainer, UpgradeData upgradeData, ITickList<Block> tickList, ITickList<Fluid> tickList2, long seed, ChunkSection[] sections, Consumer<Chunk> chunkConsumer, CallbackInfo ci) {
        if (world instanceof ServerWorld) {
            if (BlockSwap.retroGen) {
                for (ChunkSection section : this.sections) {
                    if (section != null) {
                        int bottomY = section.bottomBlockY();
                        for (int x = 0; x < 16; x++) {
                            for (int y = 0; y < 16; y++) {
                                for (int z = 0; z < 16; z++) {
                                    BlockPos blockPos = new BlockPos(x, bottomY + y, z);
                                    BlockState state = this.getBlockState(blockPos);
                                    if (BlockSwap.blockToBlockMap.containsKey(state.getBlock())) {
                                        this.setBlockState(blockPos, BlockSwap.remapState(state), false);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
