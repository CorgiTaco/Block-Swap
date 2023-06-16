package corgitaco.blockswap;

import corgitaco.blockswap.config.BlockSwapConfig;
import corgitaco.blockswap.config.MissingBlockIDsConfig;
import corgitaco.blockswap.network.NetworkHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(BlockSwap.MOD_ID)
public class BlockSwapForge {

    public BlockSwapForge() {
        BlockSwap.init(FMLPaths.CONFIGDIR.get().resolve(BlockSwap.MOD_ID));
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent commonSetupEvent) {
        BlockSwapConfig.getConfig(true);
        MissingBlockIDsConfig.getConfig(true);
        NetworkHandler.init();
    }
}