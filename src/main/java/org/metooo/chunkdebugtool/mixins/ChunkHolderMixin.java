package org.metooo.chunkdebugtool.mixins;

import org.metooo.chunkdebugtool.ChunkDebugToolLogger;
import net.minecraft.server.ChunkHolder;
import net.minecraft.server.ChunkMap;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.util.math.ChunkPos;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static org.objectweb.asm.Opcodes.PUTFIELD;

@Mixin(ChunkHolder.class)
public class ChunkHolderMixin {
	@Shadow
	@Final
	private ChunkMap chunkMap;

	@Shadow
	@Final
	private ChunkPos pos;

	@Inject(method="<init>",at= @At(value = "FIELD", target = "net/minecraft/server/ChunkHolder.pos : Lnet/minecraft/util/math/ChunkPos;", shift = At.Shift.AFTER, opcode = PUTFIELD))
	public void onReasonLoggingStart(ChunkMap chunkMap, int chunkX, int chunkZ, CallbackInfo ci) {
		if(ChunkDebugToolLogger.logger.enabled)ChunkDebugToolLogger.setReason("Player loading chunk");
	}
	@Inject(method="<init>",at= @At("TAIL"))
	public void onReasonLoggingEnd(ChunkMap chunkMap, int chunkX, int chunkZ, CallbackInfo ci) {
		ChunkDebugToolLogger.resetReason();
	}
	@Inject(method = "addPlayer",at= @At(value = "INVOKE", target = "java/util/List.isEmpty ()Z"))
	public void onReasonLogging2(ServerPlayerEntity player, CallbackInfo ci) {
		if(ChunkDebugToolLogger.logger.enabled){
			ChunkDebugToolLogger.setReason("Player added to chunk");
			ChunkDebugToolLogger.logger.log(chunkMap.getWorld(),pos.x,pos.z,ChunkDebugToolLogger.Event.PLAYER_ENTERS);
			ChunkDebugToolLogger.resetReason();
		}
	}
	@Inject(method = "removePlayer",at= @At(value = "INVOKE", target = "net/minecraft/server/ChunkMap.unload (Lnet/minecraft/server/ChunkHolder;)V"))
	public void onReasonLogging3(ServerPlayerEntity player, CallbackInfo ci) {
		if(ChunkDebugToolLogger.logger.enabled){
			ChunkDebugToolLogger.setReason("Player removed from chunk");
			ChunkDebugToolLogger.logger.log(chunkMap.getWorld(),pos.x,pos.z,ChunkDebugToolLogger.Event.PLAYER_LEAVES);
			ChunkDebugToolLogger.resetReason();
		}
	}
	@Inject(method="load",at= @At(value = "FIELD", target = "net/minecraft/server/ChunkHolder.chunkMap : Lnet/minecraft/server/ChunkMap;", ordinal = 0, opcode = Opcodes.GETFIELD))
	public void onReasonLoggingStart4(boolean generate, CallbackInfoReturnable<Boolean> cir) {
		if(ChunkDebugToolLogger.logger.enabled)ChunkDebugToolLogger.setReason("Player loading new chunks and generating");
	}
	@Inject(method="load",at= @At(value = "FIELD", target = "net/minecraft/server/ChunkHolder.chunk : Lnet/minecraft/world/chunk/WorldChunk;",opcode = PUTFIELD,shift = At.Shift.AFTER,ordinal = 0))
	public void onReasonLoggingEnd4(boolean generate, CallbackInfoReturnable<Boolean> cir) {
		ChunkDebugToolLogger.resetReason();
	}
}
