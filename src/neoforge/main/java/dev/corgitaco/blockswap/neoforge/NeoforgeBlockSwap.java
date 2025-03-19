package dev.corgitaco.blockswap.neoforge;

import net.neoforged.fml.common.Mod;
import dev.corgitaco.blockswap.BlockSwap;

@Mod(BlockSwap.MOD_ID)
public class NeoforgeBlockSwap {
    public NeoforgeBlockSwap() {
        BlockSwap.initialize();
    }
}
