package org.metooo.chunkdebugtool.mixins;

import org.metooo.chunkdebugtool.ChunkDebugToolLogger;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkGenerator;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldChunk.class)
public class WorldChunkMixin {
	@Shadow
	@Final
	private World world;

	@Shadow
	@Final
	public int chunkX;

	@Shadow
	@Final
	public int chunkZ;

	@Inject(method = "populate(Lnet/minecraft/world/chunk/ChunkGenerator;)V", at = @At(value = "INVOKE", target = "net/minecraft/world/chunk/WorldChunk.markDirty ()V",ordinal = 0))
	public void onReasonLogging1(ChunkGenerator generator, CallbackInfo ci) {
		if(ChunkDebugToolLogger.logger.enabled) {
			ChunkDebugToolLogger.setReason("Generating structure");
			ChunkDebugToolLogger.logger.log(world,chunkX,chunkZ,ChunkDebugToolLogger.Event.GENERATING_STRUCTURES);
			ChunkDebugToolLogger.resetReason();
		}
	}
	@Inject(method = "populate(Lnet/minecraft/world/chunk/ChunkGenerator;)V", at = @At(value = "INVOKE", target = "net/minecraft/world/chunk/ChunkGenerator.populateChunk (II)V"))
    public void onReasonLogging2(ChunkGenerator generator, CallbackInfo ci) {
		if(ChunkDebugToolLogger.logger.enabled) {
			ChunkDebugToolLogger.setReason("Populating chunk");
			ChunkDebugToolLogger.logger.log(world,chunkX,chunkZ,ChunkDebugToolLogger.Event.POPULATING);
			ChunkDebugToolLogger.resetReason();
		}
	}
}
