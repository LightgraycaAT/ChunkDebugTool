package org.metooo.chunkdebugtool.mixins;

import org.metooo.chunkdebugtool.ChunkDebugToolLogger;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(Entity.class)
public abstract class EntityMixin {
	@Shadow
	public abstract String getName();

	@Inject(method = "checkWaterCollisions", at = @At("HEAD"))
	private void onReasonLoggingStart(CallbackInfoReturnable<Boolean> cir) {
		if(ChunkDebugToolLogger.logger.enabled)
			ChunkDebugToolLogger.setReason("Entity checking if pushed by water: " + getName());
	}
	@Inject(method = "checkWaterCollisions", at = @At("TAIL"))
	private void onReasonLoggingEnd(CallbackInfoReturnable<Boolean> cir) {
		ChunkDebugToolLogger.resetReason();
	}
	@Inject(method = "changeDimension", at = @At(value = "INVOKE", target = "net/minecraft/server/world/PortalForcer.findNetherPortal (Lnet/minecraft/entity/Entity;F)Z"))
	private void onReasonLoggingStart2(CallbackInfoReturnable<Boolean> cir) {
		if(ChunkDebugToolLogger.logger.enabled)
			ChunkDebugToolLogger.setReason("Entity going through nether portal: " + getName());
	}
	@Inject(method = "changeDimension", at = @At(value = "INVOKE", target = "net/minecraft/server/world/PortalForcer.findNetherPortal (Lnet/minecraft/entity/Entity;F)Z", shift = At.Shift.AFTER))
	private void onReasonLoggingEnd2(CallbackInfoReturnable<Boolean> cir) {
		ChunkDebugToolLogger.resetReason();
	}
}
