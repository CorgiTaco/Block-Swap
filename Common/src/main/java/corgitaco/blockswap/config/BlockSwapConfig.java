package corgitaco.blockswap.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corgitaco.blockswap.util.CodecUtil;
import net.minecraft.Util;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.IdentityHashMap;
import java.util.Map;

public record BlockSwapConfig(Map<Block, Block> blockBlockMap, Map<BlockState, BlockState> blockStateBlockStateMap,
                              boolean retroGen) {

    public static final BlockSwapConfig DEFAULT = new BlockSwapConfig(Util.make(new IdentityHashMap<>(), map -> map.put(Blocks.DIRT, Blocks.DIAMOND_BLOCK)), Util.make(new IdentityHashMap<>(), map -> map.put(Blocks.BIRCH_LEAVES.defaultBlockState(), Blocks.BIRCH_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true))), false);

    public static final Codec<BlockSwapConfig> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(
                Codec.unboundedMap(CodecUtil.BLOCK_CODEC, CodecUtil.BLOCK_CODEC).fieldOf("swapper").forGetter(BlockSwapConfig::blockBlockMap),
                CodecUtil.KEYABLE_BLOCKSTATE_CODEC.fieldOf("state_swapper").forGetter(BlockSwapConfig::blockStateBlockStateMap),
                Codec.BOOL.fieldOf("retroGen").forGetter(BlockSwapConfig::retroGen)
        ).apply(builder, BlockSwapConfig::new);
    });

    public boolean contains(BlockState state) {
        return blockBlockMap.containsKey(state.getBlock()) || blockStateBlockStateMap.containsKey(state);
    }
}
