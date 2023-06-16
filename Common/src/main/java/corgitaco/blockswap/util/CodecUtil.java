package corgitaco.blockswap.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corgitaco.blockswap.swapper.Swapper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

import java.util.*;

public class CodecUtil {


    public static Codec<Block> BLOCK_CODEC = createLoggedExceptionRegistryCodec(BuiltInRegistries.BLOCK);
    public static Codec<Fluid> FLUID_CODEC = createLoggedExceptionRegistryCodec(BuiltInRegistries.FLUID);

    public static Codec<Pair<BlockState, BlockState>> PAIR_STATE_CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Swapper.COMMENTED_STATE_CODEC.fieldOf("old").forGetter(Pair::getFirst),
            Swapper.COMMENTED_STATE_CODEC.fieldOf("new").forGetter(Pair::getSecond)
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

    public static <T> Codec<T> createLoggedExceptionRegistryCodec(Registry<T> registry) {
        return ResourceLocation.CODEC.comapFlatMap(location -> {
            final Optional<T> result = registry.getOptional(location);

            if (result.isEmpty()) {
                StringBuilder registryElements = new StringBuilder();
                for (int i = 0; i < registry.entrySet().size(); i++) {
                    final T object = registry.byId(i);
                    registryElements.append(i).append(". \"").append(registry.getKey(object).toString()).append("\"\n");
                }

                return DataResult.error(() -> String.format("\"%s\" is not a valid id in registry: %s.\nCurrent Registry Values:\n\n%s\n", location.toString(), registry, registryElements));
            }
            return DataResult.success(result.get());
        }, registry::getKey);
    }
}
