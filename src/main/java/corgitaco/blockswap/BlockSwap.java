package corgitaco.blockswap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import corgitaco.blockswap.network.NetworkHandler;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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
    public static final Path CONFIG_PATH = new File(String.valueOf(FMLPaths.CONFIGDIR.get().resolve(MOD_ID))).toPath();

    public BlockSwap() {
        File dir = new File(CONFIG_PATH.toString());
        if (!dir.exists())
            dir.mkdirs();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
    }


    public static void handleBlockBlockConfig() {
        HashMap<String, String> blockBlockMap = new HashMap<>();
        blockBlockMap.put("dummymodid:dummyblocktoreplace", "replacer");
        blockBlockMap.put("dummymodid:dummyblocktoreplace2", "replacer2");
        blockBlockMap.put("dummymodid:dummyblocktoreplace3", "replacer3");

        BlockSwap.handleBlockBlockConfig(CONFIG_PATH.resolve("block_swap.json"), blockBlockMap);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        NetworkHandler.init();
    }

    public static Reference2ReferenceOpenHashMap<Block, Int2ObjectOpenHashMap<Property<?>>> cache = new Reference2ReferenceOpenHashMap<>();

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static BlockState remapState(BlockState incomingState) {
        Block block = BlockSwap.blockToBlockMap.get(incomingState.getBlock());

        final BlockState[] newState = {block.defaultBlockState()};

        Int2ObjectOpenHashMap<Property<?>> newStateProperties = cache.computeIfAbsent(newState[0].getBlock(), (block1) -> Util.make(new Int2ObjectOpenHashMap<>(),
                (set) -> newState[0].getProperties().parallelStream().forEach(property -> set.put(property.generateHashCode(), property))));

        incomingState.getProperties().parallelStream().map(Property::generateHashCode).map(value -> newStateProperties.get((int) value)).forEach((property1) -> {
            newState[0] = newState[0].setValue((Property) property1, incomingState.getValue((Property) property1));
        });

        return newState[0];
    }

    @SuppressWarnings("ConstantConditions")
    public static void handleBlockBlockConfig(Path path, Map<String, String> defaultBlockBlockMap) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        gsonBuilder.disableHtmlEscaping();
        Gson gson = gsonBuilder.create();
        Map<String, String> sortedMap = new TreeMap<>(Comparator.comparing(String::toString));
        sortedMap.putAll(defaultBlockBlockMap);
        final File CONFIG_FILE = new File(String.valueOf(path));

        if (!CONFIG_FILE.exists()) {
            createRiverJson(path, sortedMap);
        }
        try (Reader reader = new FileReader(path.toString())) {
            Map<String, String> blockDataListHolder = gson.fromJson(reader, Map.class);
            if (blockDataListHolder != null) {
                Reference2ReferenceOpenHashMap<Block, Block> blockBlockMap = new Reference2ReferenceOpenHashMap<>();
                blockDataListHolder.forEach((key, value) -> {
                    Block oldBlock = Registry.BLOCK.get(new ResourceLocation(key));
                    Block newBlock = Registry.BLOCK.get(new ResourceLocation(value));


                    boolean oldBlockPassed = oldBlock != null || oldBlock != Blocks.AIR;
                    boolean newBlockPassed = oldBlock != null || oldBlock != Blocks.AIR;

                    if (oldBlock != newBlock) {
                        if (oldBlockPassed || newBlockPassed) {
                            blockBlockMap.put(oldBlock, newBlock);
                        } else {
                            if (oldBlockPassed)
                                LOGGER.error(key + " is not a block in the block registry.");
                            if (newBlockPassed)
                                LOGGER.error(value + " is not a block in the block registry.");
                        }
                    } else {
                        LOGGER.error(key + " should not equal " + value);
                    }
                });

                blockBlockMap.remove(Blocks.AIR, Blocks.AIR);
                BlockSwap.blockToBlockMap = blockBlockMap;
            } else
                LOGGER.error(MOD_ID + "-rivers.json could not be read");

        } catch (IOException e) {
            LOGGER.error(MOD_ID + "-rivers.json could not be read");
        }
    }

    public static void createRiverJson(Path path, Map<String, String> biomeRiverMap) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        gsonBuilder.disableHtmlEscaping();
        Gson gson = gsonBuilder.create();

        String jsonString = gson.toJson(biomeRiverMap);

        try {
            Files.write(path, jsonString.getBytes());
        } catch (IOException e) {
            LOGGER.error(MOD_ID + "-rivers.json could not be created");
        }
    }
}
