package dev.corgitaco.blockswap.util;

import it.unimi.dsi.fastutil.ints.Int2IntMap;

public interface ChunkSectionBlockStateSwapper {

    void blockSwap$createSwapper(Int2IntMap swapper);
}
