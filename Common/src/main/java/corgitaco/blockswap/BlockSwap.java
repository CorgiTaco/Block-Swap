package corgitaco.blockswap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

public class BlockSwap {
    public static final String MOD_ID = "blockswap";
    public static Logger LOGGER = LogManager.getLogger();
    public static Path CONFIG_PATH = null;

    public BlockSwap() {
    }

    public static void init(Path configPath) {
        CONFIG_PATH = configPath;
    }
}
