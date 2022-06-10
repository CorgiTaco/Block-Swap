package corgitaco.blockswap.mixin;

import com.mojang.serialization.Codec;
import corgitaco.blockswap.config.MissingBlockIDsConfig;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Mixin(ChunkSerializer.class)
public class MixinChunkSerializer {

    @Inject(method = "read", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/Codec;parse(Lcom/mojang/serialization/DynamicOps;Ljava/lang/Object;)Lcom/mojang/serialization/DataResult;", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void repairStates(ServerLevel $$0, PoiManager $$1, ChunkPos $$2, CompoundTag $$3, CallbackInfoReturnable<ProtoChunk> cir, ChunkPos $$4, UpgradeData $$5, boolean $$6, ListTag $$7, int $$8, LevelChunkSection[] $$9, boolean $$10, ChunkSource $$11, LevelLightEngine $$12, Registry $$13, Codec $$14, boolean $$15, int $$16, CompoundTag sectionTag, int $$18, int $$19) {
        if (sectionTag.contains("block_states", 10)) {
            CompoundTag states = sectionTag.getCompound("block_states");
            MissingBlockIDsConfig missingBlockIDsConfig = MissingBlockIDsConfig.getConfig(false);
            ListTag palette = states.getList("palette", Tag.TAG_COMPOUND);
            Map<String, Block> idRemapper = missingBlockIDsConfig.idRemapper();

            for (Tag tag : palette) {
                CompoundTag tag1 = (CompoundTag) tag;

                String name = tag1.getString("Name");
                if (idRemapper.containsKey(name)) {
                    tag1.remove("Name");
                    tag1.putString("Name", Registry.BLOCK.getKey(idRemapper.get(name)).toString());
                }
            }

        }
    }
}
