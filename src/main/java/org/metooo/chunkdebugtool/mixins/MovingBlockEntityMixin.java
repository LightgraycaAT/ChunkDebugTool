package org.metooo.chunkdebugtool.mixins;

import org.metooo.chunkdebugtool.ChunkDebugToolLogger;
import net.minecraft.block.entity.MovingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MovingBlockEntity.class)
public class MovingBlockEntityMixin {
	@Inject(method = "tick", at = @At(value = "INVOKE", target = "net/minecraft/world/World.removeBlockEntity (Lnet/minecraft/util/math/BlockPos;)V"))
	public void onReasonLoggingStart(CallbackInfo ci) {
		if(ChunkDebugToolLogger.logger.enabled)ChunkDebugToolLogger.setReason("Piston block finishes moving");
	}
	@Inject(method = "tick", at = @At(value = "INVOKE", target = "net/minecraft/world/World.removeBlockEntity (Lnet/minecraft/util/math/BlockPos;)V",shift = At.Shift.AFTER))
	public void onReasonLoggingEnd(CallbackInfo ci) {
		ChunkDebugToolLogger.resetReason();
	}
}
