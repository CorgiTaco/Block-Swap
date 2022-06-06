package corgitaco.blockswap.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corgitaco.blockswap.util.CodecUtil;
import corgitaco.blockswap.util.CommentedCodec;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;

public record BlockSwapConfig(Map<Block, Block> blockBlockMap, Map<BlockState, BlockState> blockStateBlockStateMap,
                              boolean retroGen, boolean generateAllKnownStates) {

    public static final BlockSwapConfig DEFAULT = new BlockSwapConfig(new IdentityHashMap<>(), new IdentityHashMap<>(), false, true);

    private static final String SWAPPER_EXAMPLE = """
            	"swapper": {
            		"minecraft:coarse_dirt": "minecraft:dirt",
            		"minecraft:diamond_block": "minecraft:emerald_block"
            	}
            """;

    private static final String STATE_SWAPPER_EXAMPLE = """
             "state_swapper": [
             	{
             		"new": {
             			"Name": "minecraft:birch_log",
             			// Properties define the state of this block/fluid.
             			"Properties": {
             				"axis": "x"
             			}
             		},
             		"old": {
             			"Name": "minecraft:oak_log",
             			// Properties define the state of this block/fluid.
             			"Properties": {
             				"axis": "z"
             			}
             		}
             	},
             	{
             		"new": {
             			"Name": "minecraft:birch_leaves",
             			// Properties define the state of this block/fluid.
             			"Properties": {
             				"distance": "7",
             				"persistent": "true"
             			}
             		},
             		"old": {
             			"Name": "minecraft:acacia_log",
             			// Properties define the state of this block/fluid.
             			"Properties": {
             				"axis": "z"
             			}
             		}
             	},
             	{
             		"new": {
             			"Name": "minecraft:jungle_log",
             			// Properties define the state of this block/fluid.
             			"Properties": {
             				"axis": "x"
             			}
             		},
             		"old": {
             			"Name": "minecraft:birch_log",
             			// Properties define the state of this block/fluid.
             			"Properties": {
             				"axis": "z"
             			}
             		}
             	},
             	{
             		"new": {
             			"Name": "minecraft:jungle_planks",
             		},
             		"old": {
             			"Name": "minecraft:acacia_planks",
             			}
             		}
             	}
             ]
            """;

    private static final Codec<BlockSwapConfig> RAW_CODEC = RecordCodecBuilder.create(builder ->
            builder.group(
                    CommentedCodec.of(Codec.unboundedMap(CodecUtil.BLOCK_CODEC, CodecUtil.BLOCK_CODEC), "swapper", "A map of blocks that specifies what the \"old\" block is and what its \"new\" block is.\nExample:\n" + SWAPPER_EXAMPLE).forGetter(BlockSwapConfig::blockBlockMap),
                    CommentedCodec.of(CodecUtil.KEYABLE_BLOCKSTATE_CODEC, "state_swapper", "A map of states that specifies what the \"old\" block state is and what its \"new\" block state is.\nSee \"known_states\" folder(\"generate_all_known_states\" must be set to true in this config) to see all known block states available for all blocks available in the registry.\nExample:\n" + STATE_SWAPPER_EXAMPLE).forGetter(BlockSwapConfig::blockStateBlockStateMap),
                    CommentedCodec.of(Codec.BOOL, "retro_gen", "Whether blocks are replaced in existing chunks.").forGetter(BlockSwapConfig::retroGen),
                    CommentedCodec.of(Codec.BOOL, "generate_all_known_states", "Generates all block states for all blocks in the registry.").forGetter(BlockSwapConfig::generateAllKnownStates)
            ).apply(builder, BlockSwapConfig::new)
    );

    public static final Codec<BlockSwapConfig> CODEC = RAW_CODEC.flatXmap(verifyConfig(), verifyConfig());

    public boolean contains(BlockState state) {
        return blockBlockMap.containsKey(state.getBlock()) || blockStateBlockStateMap.containsKey(state);
    }

    private static Function<BlockSwapConfig, DataResult<BlockSwapConfig>> verifyConfig() {
        return blockSwapConfig -> {
            StringBuilder blockSwapperErrors = new StringBuilder();
            for (Block block : blockSwapConfig.blockBlockMap.values()) {
                if (blockSwapConfig.blockBlockMap.containsKey(block)) {
                    blockSwapperErrors.append(Registry.BLOCK.getKey(block)).append("\n");
                }
            }

            StringBuilder stateSwapperErrors = new StringBuilder();

            for (BlockState value : blockSwapConfig.blockStateBlockStateMap.values()) {
                if (blockSwapConfig.blockStateBlockStateMap.containsKey(value)) {
                    stateSwapperErrors.append(value.toString()).append("\n");
                }
            }


            String errorMessage = "";

            if (!blockSwapperErrors.isEmpty()) {
                errorMessage = errorMessage + String.format("Detected circular BLOCK reference(s) in the \"swapper\"! Blocks being swapped cannot be used as a block to swap into. Circular references found:\n%s\n", blockSwapperErrors.toString());
            }

            if (!stateSwapperErrors.isEmpty()) {
                errorMessage = errorMessage + String.format("Detected circular BLOCKSTATE reference(s) in the \"state_swapper\"! BlockStates being swapped cannot be used as a BlockState to swap into. Circular references found:\n%s", stateSwapperErrors.toString());
            }

            if (!errorMessage.isEmpty()) {
                return DataResult.error(errorMessage);
            }

            return DataResult.success(blockSwapConfig);
        };
    }
}
