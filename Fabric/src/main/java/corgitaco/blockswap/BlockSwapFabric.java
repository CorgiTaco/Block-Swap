package corgitaco.blockswap;

import corgitaco.blockswap.network.NetworkHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class BlockSwapFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        BlockSwap.init(FabricLoader.getInstance().getConfigDir());
        NetworkHandler.init();
    }
}
