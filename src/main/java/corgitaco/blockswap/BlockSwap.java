package corgitaco.blockswap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import corgitaco.blockswap.network.NetworkHandler;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.Property;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Mod("blockswap")
public class BlockSwap {
    public static final String MOD_ID = "blockswap";

    public static Logger LOGGER = LogManager.getLogger();
    public static Reference2ReferenceOpenHashMap<Block, Block> blockToBlockMap = new Reference2ReferenceOpenHashMap<>();
    public static boolean retroGen = false;
    public static final Path CONFIG_PATH = new File(String.valueOf(FMLPaths.CONFIGDIR.get().resolve(MOD_ID))).toPath();

    public BlockSwap() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
    }


    public static void handleBlockBlockConfig() {
        HashMap<String, String> blockBlockMap = new HashMap<>();
        blockBlockMap.put("dummymodid:dummyblocktoreplace", "dummymodid2:replacer");
        blockBlockMap.put("dummymodid:dummyblocktoreplace2", "dummymodid2:replacer2");
        blockBlockMap.put("dummymodid:dummyblocktoreplace3", "dummymodid2:replacer3");

        BlockSwap.handleBlockBlockConfig(CONFIG_PATH.resolve("block_swap.json"), blockBlockMap);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        NetworkHandler.init();
    }

    public static Reference2ReferenceOpenHashMap<Block, Int2ObjectOpenHashMap<Property<?>>> cache = new Reference2ReferenceOpenHashMap<>();

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static BlockState remapState(BlockState incomingState) {
        Block block = BlockSwap.blockToBlockMap.get(incomingState.getBlock());

        BlockState newState = block.defaultBlockState();

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

    @SuppressWarnings({"ConstantConditions", "unchecked", "deprecation"})
    public static void handleBlockBlockConfig(Path path, Map<String, String> defaultBlockBlockMap) {
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().setLenient().create();
        Map<String, String> sortedMap = new TreeMap<>(Comparator.comparing(String::toString));
        sortedMap.putAll(defaultBlockBlockMap);
        final File CONFIG_FILE = new File(String.valueOf(path));

        if (!CONFIG_FILE.exists()) {
            createBlockBlockConfig(path, sortedMap, gson);
        }
        try (Reader reader = new FileReader(path.toString())) {
            Config blockDataListHolder = gson.fromJson(reader, Config.class);
            if (blockDataListHolder != null) {
                Reference2ReferenceOpenHashMap<Block, Block> blockBlockMap = new Reference2ReferenceOpenHashMap<>();
                Reference2ReferenceOpenHashMap<Block, Block> reversedBlockBlockMap = new Reference2ReferenceOpenHashMap<>();
                if (blockDataListHolder.getSwapper() != null) {
                    for (Map.Entry<String, String> entry : blockDataListHolder.getSwapper().entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();

                        ResourceLocation keyLocation = new ResourceLocation(key);
                        ResourceLocation valueLocation = new ResourceLocation(value);

                        boolean containsKey = Registry.BLOCK.keySet().contains(keyLocation);
                        boolean containsValue = Registry.BLOCK.keySet().contains(valueLocation);

                        if (!containsKey || !containsValue) {
                            if (!containsKey) {
                                LOGGER.error("Key: " + key + " is not a block in the block registry for entry: \"" + key + "\":\"" + value + "\". . Skipping entry...");
                            }
                            if (!containsValue) {
                                LOGGER.error("Value: " + value + " is not a block in the block registry for entry: \"" + key + "\":\"" + value + "\". Skipping entry...");
                            }
                            continue;
                        }

                        Block oldBlock = Registry.BLOCK.get(keyLocation);
                        Block newBlock = Registry.BLOCK.get(valueLocation);

                        if (oldBlock != newBlock) {
                            blockBlockMap.put(oldBlock, newBlock);
                            reversedBlockBlockMap.put(newBlock, oldBlock);

                            if ((reversedBlockBlockMap.containsKey(oldBlock) && blockBlockMap.containsValue(oldBlock))) {
                                LOGGER.error("Circular reference found for entry: \"" + key + "\":\"" + value + "\". Removing this entry...");
                                blockBlockMap.remove(oldBlock);
                                reversedBlockBlockMap.remove(newBlock);
                            }

                        } else {
                            LOGGER.error(key + " should not equal " + value);
                        }
                    }
                }

                BlockSwap.blockToBlockMap = blockBlockMap;
                BlockSwap.retroGen = blockDataListHolder.isRetroGen();
            } else
                LOGGER.error(CONFIG_FILE.getAbsolutePath() + " could not be read!");

        } catch (IOException e) {
            LOGGER.error(CONFIG_FILE.getAbsolutePath() + " could not be read!");
        }
    }

    public static void createBlockBlockConfig(Path path, Map<String, String> biomeRiverMap, Gson gson) {
        String jsonString = gson.toJson(new Config(biomeRiverMap, true));

        try {
            Files.createDirectories(path.getParent());
            Files.write(path, jsonString.getBytes());
        } catch (IOException e) {
            LOGGER.error(path.toString() + " could not be created!");
        }
    }

    public static class Config {

        private final boolean retroGen;
        private final Map<String, String> swapper;

        private Config(Map<String, String> swapper, boolean retroGen) {
            this.retroGen = retroGen;
            this.swapper = swapper;
        }

        public boolean isRetroGen() {
            return retroGen;
        }

        public Map<String, String> getSwapper() {
            return swapper;
        }
    }
}
