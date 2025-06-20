package org.metooo.chunkdebugtool.mixins;

import org.metooo.chunkdebugtool.ChunkDebugToolLogger;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(World.class)
public class WorldMixin {
	@Redirect(method = "tickEntities", at = @At(value = "INVOKE", target = "net/minecraft/world/chunk/WorldChunk.removeEntity (Lnet/minecraft/entity/Entity;)V",ordinal = 1))
	private void onReasonLoggingStart(WorldChunk instance, net.minecraft.entity.Entity entity) {
		if(ChunkDebugToolLogger.logger.enabled)ChunkDebugToolLogger.setReason("Removing entity from chunk: " + entity.getName());
		instance.removeEntity(entity);
	}
	@Inject(method = "tickEntities", at = @At(value = "INVOKE", target = "net/minecraft/world/chunk/WorldChunk.removeEntity (Lnet/minecraft/entity/Entity;)V",ordinal = 1,shift = At.Shift.AFTER))
	private void onReasonLoggingEnd(CallbackInfo ci) {
		ChunkDebugToolLogger.resetReason();
	}
	@Inject(method = "updateNeighborComparators", at = @At("HEAD"))
	private void onReasonLoggingStart2(BlockPos pos, Block block, CallbackInfo ci) {
		if(ChunkDebugToolLogger.logger.enabled)ChunkDebugToolLogger.setReason("Comperator updates for inventory changes");
	}
	@Inject(method = "updateNeighborComparators", at = @At("TAIL"))
	private void onReasonLoggingEnd2(CallbackInfo ci) {
		ChunkDebugToolLogger.resetReason();
	}
}
