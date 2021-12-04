package corgitaco.blockswap.mixin;

import corgitaco.blockswap.BlockSwap;
import corgitaco.blockswap.network.NetworkHandler;
import corgitaco.blockswap.network.packet.ClientConfigSyncPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public abstract class MixinPlayerList {

    @Inject(method = "sendLevelInfo", at = @At(value = "HEAD"))
    private void sendServerConfig(ServerPlayer playerIn, ServerLevel worldIn, CallbackInfo ci) {
        NetworkHandler.sendToClient(playerIn, new ClientConfigSyncPacket(BlockSwap.getConfig(true)));
    }
}