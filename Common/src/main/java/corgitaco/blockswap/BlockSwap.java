package corgitaco.blockswap;

import blue.endless.jankson.api.SyntaxError;
import corgitaco.blockswap.config.BlockSwapConfig;
import corgitaco.blockswap.util.jankson.JanksonJsonOps;
import corgitaco.blockswap.util.jankson.JanksonUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

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
            Path path = CONFIG_PATH.resolve("block_swap.json5");
            File configFile = path.toFile();
            try {
                if (!configFile.exists()) {
                    JanksonUtil.createConfig(path, BlockSwapConfig.CODEC, JanksonUtil.HEADER_CLOSED, new Object2ObjectOpenHashMap<>(), JanksonJsonOps.INSTANCE, BlockSwapConfig.DEFAULT);
                }
                CONFIG = JanksonUtil.readConfig(path, BlockSwapConfig.CODEC, JanksonJsonOps.INSTANCE);
            } catch (IOException | SyntaxError e) {
                e.printStackTrace();
            }
        }
        return CONFIG;
    }
}
