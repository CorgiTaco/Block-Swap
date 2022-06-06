package corgitaco.blockswap;

import corgitaco.blockswap.network.NetworkHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(BlockSwap.MOD_ID)
public class BlockSwapForge {

    public BlockSwapForge() {
        BlockSwap.init(FMLPaths.CONFIGDIR.get().resolve(BlockSwap.MOD_ID));
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent commonSetupEvent) {
        NetworkHandler.init();
    }
}