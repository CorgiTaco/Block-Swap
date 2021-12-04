package corgitaco.blockswap.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class CodecUtil {


    public static Codec<Block> BLOCK_CODEC = ResourceLocation.CODEC.xmap(Registry.BLOCK::get, Registry.BLOCK::getKey);

    public static Codec<Pair<BlockState, BlockState>> PAIR_STATE_CODEC = RecordCodecBuilder.create(builder -> builder.group(
            BlockState.CODEC.fieldOf("old").forGetter(Pair::getFirst),
            BlockState.CODEC.fieldOf("new").forGetter(Pair::getSecond)
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


}
