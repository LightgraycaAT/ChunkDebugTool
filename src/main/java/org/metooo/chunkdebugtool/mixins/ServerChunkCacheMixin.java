package org.metooo.chunkdebugtool.mixins;

import org.metooo.chunkdebugtool.ChunkDebugToolLogger;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.chunk.ServerChunkCache;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(ServerChunkCache.class)
public class ServerChunkCacheMixin {
	@Shadow
	@Final
	private ServerWorld world;

	@Shadow
	@Final
	private Set<Long> chunksToUnload;
	@Inject(method = "unloadChunk", at = @At(value = "FIELD", target = "net/minecraft/server/world/chunk/ServerChunkCache.chunksToUnload : Ljava/util/Set;"))
	public void onReasonLogging1(WorldChunk chunk, CallbackInfo ci) {
		if(ChunkDebugToolLogger.logger.enabled) {
			ChunkDebugToolLogger.logger.log(world,chunk.chunkX,chunk.chunkZ,ChunkDebugToolLogger.Event.QUEUE_UNLOAD);
		}
	}
	@Inject(method ="getLoadedChunk",at= @At(value = "FIELD", target = "net/minecraft/world/chunk/WorldChunk.removed : Z"))
	public void onReasonLogging2(int chunkX, int chunkZ, CallbackInfoReturnable<WorldChunk> cir,@Local WorldChunk chunk) {
		if(ChunkDebugToolLogger.logger.enabled&&chunk.removed) {
			ChunkDebugToolLogger.logger.log(world,chunkX,chunkZ,ChunkDebugToolLogger.Event.CANCEL_UNLOAD);
		}
	}
	@Inject(method = "getGeneratedChunk",at= @At(value = "FIELD", target = "net/minecraft/server/world/chunk/ServerChunkCache.chunks : Lit/unimi/dsi/fastutil/longs/Long2ObjectMap;"))
	public void onReasonLogging3(int chunkX, int chunkZ, CallbackInfoReturnable<WorldChunk> cir) {
		if(ChunkDebugToolLogger.logger.enabled) {
			ChunkDebugToolLogger.logger.log(world,chunkX,chunkZ,ChunkDebugToolLogger.Event.LOADING);
		}
	}

	@Inject(method = "getGeneratedChunk",at= @At(value = "INVOKE", target = "net/minecraft/world/chunk/WorldChunk.populate (Lnet/minecraft/world/chunk/ChunkSource;Lnet/minecraft/world/chunk/ChunkGenerator;)V"))
	public void onReasonLoggingStart4(int chunkX, int chunkZ, CallbackInfoReturnable<WorldChunk> cir) {
		if(ChunkDebugToolLogger.logger.enabled) ChunkDebugToolLogger.setReason("Population triggering neighbouring chunks to cancel unload");
	}
	@Inject(method = "getGeneratedChunk",at= @At(value = "INVOKE", target = "net/minecraft/world/chunk/WorldChunk.populate (Lnet/minecraft/world/chunk/ChunkSource;Lnet/minecraft/world/chunk/ChunkGenerator;)V",shift = At.Shift.AFTER))
	public void onReasonLoggingEnd4(int chunkX, int chunkZ, CallbackInfoReturnable<WorldChunk> cir) {
		ChunkDebugToolLogger.resetToOldReason();
	}

	@Inject(method = "getChunk",at= @At(value = "INVOKE", target = "net/minecraft/world/chunk/ChunkGenerator.getChunk (II)Lnet/minecraft/world/chunk/WorldChunk;",shift = At.Shift.AFTER))
	public void onReasonLogging5(int chunkX, int chunkZ, CallbackInfoReturnable<WorldChunk> cir) {
		if(ChunkDebugToolLogger.logger.enabled) ChunkDebugToolLogger.logger.log(world,chunkX,chunkZ,ChunkDebugToolLogger.Event.GENERATING);
	}
	@Inject(method = "tick",at= @At(value = "FIELD", target = "net/minecraft/server/world/chunk/ServerChunkCache.chunksToUnload : Ljava/util/Set;",ordinal = 1))
	public void onReasonLoggingStart6(CallbackInfoReturnable<Boolean> cir) {
		if(ChunkDebugToolLogger.logger.enabled) ChunkDebugToolLogger.setReason("Unloading chunk and writing to disk");
	}
	@Inject(method = "tick",at= @At(value = "INVOKE", target = "net/minecraft/server/world/chunk/ServerChunkCache.saveEntities (Lnet/minecraft/world/chunk/WorldChunk;)V",shift = At.Shift.AFTER))
	public void onReasonLogging7(CallbackInfoReturnable<Boolean> cir, @Local WorldChunk chunk) {
		if(ChunkDebugToolLogger.logger.enabled) ChunkDebugToolLogger.logger.log(world,chunk.chunkX,chunk.chunkZ,ChunkDebugToolLogger.Event.UNLOADING);
	}
	@Inject(method = "tick",at= @At(value = "INVOKE", target = "net/minecraft/world/chunk/storage/ChunkStorage.tick ()V"))
	public void onReasonLoggingEnd6(CallbackInfoReturnable<Boolean> cir) {
		if (!chunksToUnload.isEmpty()) {ChunkDebugToolLogger.resetReason();}
	}
}
