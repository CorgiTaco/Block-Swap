package corgitaco.blockswap.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corgitaco.blockswap.BlockSwap;
import corgitaco.corgilib.serialization.codec.CodecUtil;
import corgitaco.corgilib.serialization.codec.CommentedCodec;
import corgitaco.corgilib.serialization.jankson.JanksonJsonOps;
import corgitaco.corgilib.serialization.jankson.JanksonUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.world.level.block.Block;

import java.io.File;
import java.nio.file.Path;
import java.util.IdentityHashMap;
import java.util.Map;

public record MissingBlockIDsConfig(Map<String, Block> idRemapper) {


    private static final String ID_REMAPPER_EXAMPLE = """
            	"swapper": {
            	          Broken ID             Valid ID
            		"minecraft:coarse_dirt": "minecraft:dirt",
            		"minecraft:diamond_block": "minecraft:emerald_block"
            	}
            """;

    private static final Codec<MissingBlockIDsConfig> CODEC = RecordCodecBuilder.create(builder ->
            builder.group(
                    CommentedCodec.of(Codec.unboundedMap(Codec.STRING, CodecUtil.BLOCK_CODEC), "id_remapper", "A map of blocks that specifies what the \"old\" broken block is and what its \"new\" functional block is.\nExample:\n" + ID_REMAPPER_EXAMPLE).forGetter(MissingBlockIDsConfig::idRemapper)
            ).apply(builder, MissingBlockIDsConfig::new)
    );

    private static MissingBlockIDsConfig CONFIG = null;

    public static MissingBlockIDsConfig getConfig(boolean reload) {
        return getConfig(reload, false);
    }

    public static MissingBlockIDsConfig getConfig(boolean reload, boolean genempty) {
        if (CONFIG == null || reload) {
            Path path = BlockSwap.CONFIG_PATH.resolve("missing_block_ids.json5");
            File configFile = path.toFile();
            if (!configFile.exists()) {
                JanksonUtil.createConfig(path, MissingBlockIDsConfig.CODEC, JanksonUtil.HEADER_CLOSED, new Object2ObjectOpenHashMap<>(), JanksonJsonOps.INSTANCE, new MissingBlockIDsConfig(new IdentityHashMap<>()));
            }

            if (genempty && !configFile.exists()) {
                JanksonUtil.createConfig(path, MissingBlockIDsConfig.CODEC, JanksonUtil.HEADER_CLOSED, new Object2ObjectOpenHashMap<>(), JanksonJsonOps.INSTANCE, new MissingBlockIDsConfig(new IdentityHashMap<>()));
                return null;
            }
            CONFIG = JanksonUtil.readConfig(path, MissingBlockIDsConfig.CODEC, JanksonJsonOps.INSTANCE);
        }
        return CONFIG;
    }
}
