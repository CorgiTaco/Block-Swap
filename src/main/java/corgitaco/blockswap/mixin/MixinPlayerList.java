package corgitaco.blockswap.mixin;

import corgitaco.blockswap.BlockSwap;
import corgitaco.blockswap.network.NetworkHandler;
import corgitaco.blockswap.network.packet.ClientConfigSyncPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.management.PlayerList;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public abstract class MixinPlayerList {

    @Inject(method = "sendLevelInfo", at = @At(value = "HEAD"))
    private void sendServerConfig(ServerPlayerEntity playerIn, ServerWorld worldIn, CallbackInfo ci) {
        NetworkHandler.sendToClient(playerIn, new ClientConfigSyncPacket(new ClientConfigSyncPacket.SimpleMapCodec(BlockSwap.blockToBlockMap)));
    }
}