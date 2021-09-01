package corgitaco.blockswap.mixin;

import corgitaco.blockswap.helpers.TickHelper;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(WorldChunk.class)
public abstract class MixinWorldChunk implements TickHelper {
    boolean isTickDirty;

    @Override
    public boolean markTickDirty() {
        return isTickDirty;
    }

    @Override
    public void setTickDirty() {
        this.isTickDirty = true;
    }
}
