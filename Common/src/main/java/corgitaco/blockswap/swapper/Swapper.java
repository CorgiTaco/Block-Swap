package corgitaco.blockswap.swapper;

import com.mojang.serialization.Codec;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;

import java.util.function.Function;

public class Swapper {
    public static final Codec<BlockState> COMMENTED_STATE_CODEC = codec(CodecUtil.BLOCK_CODEC, Block::defaultBlockState);

    protected static <O, S extends StateHolder<O, S>> Codec<S> codec(Codec<O> object, Function<O, S> defaultVal) {
        return object.dispatch("Name", (stateHolder) -> ((StateHolderAccess<O, S>) stateHolder).blockSwap_GetOwner(), (o) -> {
            S stateProperty = defaultVal.apply(o);
            return stateProperty.getValues().isEmpty() ? Codec.unit(stateProperty) : CommentedCodec.optionalOf(((StateHolderAccess<O, S>) stateProperty).blockSwap_getPropertiesCodec().codec(), "Properties", "Properties define the state of this block/fluid.", stateProperty).codec();
        });
    }

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
                Level world = chunk.getLevel();
                for (LevelChunkSection section : chunk.getSections()) {
                    if (section != null) {
                        int bottomY = section.bottomBlockY();
                        for (int x = 0; x < 16; x++) {
                            for (int y = 0; y < 16; y++) {
                                for (int z = 0; z < 16; z++) {
                                    BlockPos blockPos = new BlockPos(SectionPos.sectionToBlockCoord(chunk.getPos().x) + x, bottomY + y, SectionPos.sectionToBlockCoord(chunk.getPos().z) + z);
                                    BlockState state = world.getBlockState(blockPos);
                                    if (config.blockBlockMap().containsKey(state.getBlock())) {
                                        world.setBlock(blockPos, remapState(state), 2);
                                    }

                                    if (config.blockStateBlockStateMap().containsKey(state)) {
                                        world.setBlock(blockPos, config.blockStateBlockStateMap().get(state), 2);
                                    }
                                }
                            }
                        }
                    }
                }
                ((TickHelper) chunk).setTickDirty();
            }
        }
    }


}
