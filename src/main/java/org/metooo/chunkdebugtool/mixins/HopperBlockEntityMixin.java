package org.metooo.chunkdebugtool.mixins;

import org.metooo.chunkdebugtool.ChunkDebugToolLogger;
import net.minecraft.block.entity.HopperBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HopperBlockEntity.class)
public class HopperBlockEntityMixin {
	@Unique
	private static String prevReason = "";
	@Inject(method = "pushItem()Z", at = @At("HEAD"))
	private void onReasonLogging1(CallbackInfoReturnable<Boolean> cir) {
		prevReason = ChunkDebugToolLogger.reason;
		ChunkDebugToolLogger.reason="Hopper loading";
	}
	@Inject(method = "pushItem()Z",at = @At(value = "INVOKE", target = "net/minecraft/block/entity/HopperBlockEntity.getTargetInventory ()Lnet/minecraft/inventory/Inventory;",shift = At.Shift.AFTER))
	private void onReasonLogging2(CallbackInfoReturnable<Boolean> cir) {
		ChunkDebugToolLogger.reason=prevReason;
	}
	@Inject(method = "pullItem(Lnet/minecraft/inventory/Hopper;)Z", at = @At("HEAD"))
	private static void onReasonLogging3(CallbackInfoReturnable<Boolean> cir) {
		prevReason = ChunkDebugToolLogger.reason;
		ChunkDebugToolLogger.reason="Hopper self-loading";

	}
	@Inject(method = "pullItem(Lnet/minecraft/inventory/Hopper;)Z", at = @At(value = "INVOKE", target = "net/minecraft/block/entity/HopperBlockEntity.getInventoryAbove (Lnet/minecraft/inventory/Hopper;)Lnet/minecraft/inventory/Inventory;",shift = At.Shift.AFTER))
	private static void onReasonLogging4(CallbackInfoReturnable<Boolean> cir) {
		ChunkDebugToolLogger.reason=prevReason;
	}
}
