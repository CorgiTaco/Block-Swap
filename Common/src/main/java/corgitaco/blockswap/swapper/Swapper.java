package corgitaco.blockswap.swapper;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corgitaco.blockswap.config.BlockSwapConfig;
import corgitaco.blockswap.mixin.access.StateHolderAccess;
import corgitaco.blockswap.util.TickHelper;
import corgitaco.corgilib.serialization.codec.CodecUtil;
import corgitaco.corgilib.serialization.codec.CommentedCodec;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Swapper {
    public static final Codec<BlockState> COMMENTED_STATE_CODEC = codec(CodecUtil.BLOCK_CODEC, Block::defaultBlockState);

    protected static <O, S extends StateHolder<O, S>> Codec<S> codec(Codec<O> object, Function<O, S> defaultVal) {
        return object.dispatch("Name", (stateHolder) -> ((StateHolderAccess<O, S>) stateHolder).blockSwap_GetOwner(), (o) -> {
            S stateProperty = defaultVal.apply(o);
            return stateProperty.getValues().isEmpty() ? Codec.unit(stateProperty) : CommentedCodec.optionalOf(((StateHolderAccess<O, S>) stateProperty).blockSwap_getPropertiesCodec().codec(), "Properties", "Properties define the state of this block/fluid.", stateProperty).codec();
        });
    }

    public static Codec<Pair<BlockState, BlockState>> PAIR_STATE_CODEC = RecordCodecBuilder.create(builder -> builder.group(
            COMMENTED_STATE_CODEC.fieldOf("old").forGetter(Pair::getFirst),
            COMMENTED_STATE_CODEC.fieldOf("new").forGetter(Pair::getSecond)
    ).apply(builder, Pair::new));

    public static Codec<Map<BlockState, BlockState>> KEYABLE_BLOCKSTATE_CODEC = PAIR_STATE_CODEC.listOf().xmap(s -> {
        Map<BlockState, BlockState> map = new IdentityHashMap<>();
        for (Pair<BlockState, BlockState> blockStateBlockStatePair : s) {
            map.put(blockStateBlockStatePair.getFirst(), blockStateBlockStatePair.getSecond());
        }
        return map;
    }, map -> {
        List<Pair<BlockState, BlockState>> pairs = new ArrayList<>();
        map.forEach((state, state2) -> pairs.add(new Pair<>(state, state2)));
        return pairs;
    });

    public static Reference2ReferenceOpenHashMap<Block, Int2ObjectOpenHashMap<Property<?>>> cache = new Reference2ReferenceOpenHashMap<>();

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static BlockState remapState(BlockState incomingState) {
        BlockSwapConfig config = BlockSwapConfig.getConfig(false);

        if (config.blockStateBlockStateMap().containsKey(incomingState)) {
            return config.blockStateBlockStateMap().get(incomingState);
        } else {
            BlockState newState = config.blockBlockMap().get(incomingState.getBlock()).defaultBlockState();

            BlockState finalNewState = newState;
            Int2ObjectOpenHashMap<Property<?>> newStateProperties = cache.computeIfAbsent(newState.getBlock(), (block1) -> Util.make(new Int2ObjectOpenHashMap<>(), (set) -> {
                for (Property<?> property : finalNewState.getProperties()) {
                    set.put(property.generateHashCode(), property);
                }
            }));

            for (Property<?> property : incomingState.getProperties()) {
                Property newProperty = newStateProperties.get(property.generateHashCode());
                if (newProperty != null) {
                    newState = newState.setValue(newProperty, incomingState.getValue(newProperty));
                }
            }
            return newState;
        }
    }

    public static void runRetroGenerator(LevelChunk chunk) {
        BlockSwapConfig config = BlockSwapConfig.getConfig(false);
        if (config.retroGen()) {
            if (!((TickHelper) chunk).markTickDirty()) {
                LevelChunkSection[] sections = chunk.getSections();
                boolean chunkModified = false;

                // Check if config targets Air
                boolean configReplacesAir = config.blockBlockMap().containsKey(Blocks.AIR) 
                                            || config.blockStateBlockStateMap().containsKey(Blocks.AIR.defaultBlockState());
    
                for (int i = 0; i < sections.length; i++) {
                    LevelChunkSection section = sections[i];

                    // If exists and isn't only air unless the target itself is air - Skips a lot of unneeded cycles
                    if (section != null && (!section.hasOnlyAir() || configReplacesAir)) {
                        for (int x = 0; x < 16; x++) {
                            for (int y = 0; y < 16; y++) {
                                for (int z = 0; z < 16; z++) {
                                    // State directly from the local section array
                                    BlockState state = section.getBlockState(x, y, z);
    
                                    BlockState newState = null;
    
                                    if (config.blockBlockMap().containsKey(state.getBlock())) {
                                        newState = remapState(state);
                                    } else if (config.blockStateBlockStateMap().containsKey(state)) {
                                        newState = config.blockStateBlockStateMap().get(state);
                                    }
    
                                    if (newState != null && newState != state) {
                                        section.setBlockState(x, y, z, newState, false);
                                        chunkModified = true;
                                    }
                                }
                            }
                        }
                    }
                }
    
                // Mark chunk as dirty so it saves the swap
                if (chunkModified) {
                    chunk.setUnsaved(true);
                }
    
                ((TickHelper) chunk).setTickDirty();
            }
        }
    }


}