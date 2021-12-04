package corgitaco.blockswap.mixin;

import corgitaco.blockswap.util.TickHelper;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LevelChunk.class)
public abstract class MixinLevelChunk implements TickHelper {
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
