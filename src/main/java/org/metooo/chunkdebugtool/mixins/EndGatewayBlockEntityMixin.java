package org.metooo.chunkdebugtool.mixins;

import org.metooo.chunkdebugtool.ChunkDebugToolLogger;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EndGatewayBlockEntity.class)
public class EndGatewayBlockEntityMixin {
	@Inject(method="findExitPos(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;IZ)Lnet/minecraft/util/math/BlockPos;", at = @At("HEAD"))
	private static void onReasonLoggingStart(CallbackInfoReturnable<BlockPos> cir) {
		if (ChunkDebugToolLogger.logger.enabled)ChunkDebugToolLogger.setReason("End gateway looking for highest block");
	}
	@Inject(method="findExitPos(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;IZ)Lnet/minecraft/util/math/BlockPos;", at = @At("RETURN"))
	private static void onReasonLoggingEnd(CallbackInfoReturnable<BlockPos> cir) {
		ChunkDebugToolLogger.resetReason();
	}
}
