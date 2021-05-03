package corgitaco.blockswap.mixin;

import corgitaco.blockswap.helpers.TickHelper;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Chunk.class)
public abstract class MixinChunk implements TickHelper {
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
