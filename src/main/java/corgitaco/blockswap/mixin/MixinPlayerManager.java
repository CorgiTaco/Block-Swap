package corgitaco.blockswap.mixin;

import corgitaco.blockswap.BlockSwap;
import corgitaco.blockswap.network.NetworkHandler;
import corgitaco.blockswap.network.packet.ClientConfigSyncPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public abstract class MixinPlayerManager {
    @Inject(method = "sendWorldInfo", at = @At(value = "HEAD"))
    private void sendServerConfig(ServerPlayerEntity playerIn, ServerWorld worldIn, CallbackInfo ci) {
        NetworkHandler.sendToClient(playerIn, new ClientConfigSyncPacket(new ClientConfigSyncPacket.ConfigCodec(BlockSwap.blockToBlockMap, BlockSwap.retroGen)));
    }
}