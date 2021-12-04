package corgitaco.blockswap;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import corgitaco.blockswap.config.BlockSwapConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class BlockSwap {
    public static final String MOD_ID = "blockswap";
    public static Logger LOGGER = LogManager.getLogger();

    private static BlockSwapConfig CONFIG = null;
    public static boolean retroGen = false;
    public static Path CONFIG_PATH = null;

    public BlockSwap() {
    }

    public static void init(Path configPath) {
        CONFIG_PATH = configPath;
    }

    public static BlockSwapConfig getConfig(BlockSwapConfig server) {
        CONFIG = server;
        return CONFIG;
    }

    public static BlockSwapConfig getConfig(boolean reload) {
        if (CONFIG == null || reload) {
            File configFile = CONFIG_PATH.resolve("block_swap.json").toFile();
            try {
                if (!configFile.exists()) {
                    final Optional<JsonElement> result = BlockSwapConfig.CODEC.encodeStart(JsonOps.INSTANCE, BlockSwapConfig.DEFAULT).result();
                    Files.write(configFile.toPath(), new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(result.get()).getBytes());
                }
                CONFIG = BlockSwapConfig.CODEC.decode(JsonOps.INSTANCE, new JsonParser().parse(new FileReader(configFile))).resultOrPartial(BlockSwap.LOGGER::error).get().getFirst();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return CONFIG;
    }
}
