package org.metooo.chunkdebugtool.mixins;

import org.metooo.chunkdebugtool.ChunkDebugToolLogger;
import net.minecraft.network.packet.c2s.play.PlayerUseBlockC2SPacket;
import net.minecraft.server.network.handler.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
	@Inject(method = "handlePlayerUseBlock", at = @At(value = "FIELD", target = "net/minecraft/server/entity/living/player/ServerPlayerEntity.interactionManager : Lnet/minecraft/server/ServerPlayerInteractionManager;",ordinal = 0))
    private void onReasonLoggingStart(PlayerUseBlockC2SPacket packet, CallbackInfo ci) {
		if (ChunkDebugToolLogger.logger.enabled)
			ChunkDebugToolLogger.setReason("Player interacting with right click");
	}
	@Inject(method = "handlePlayerUseBlock", at = @At(value = "INVOKE",
		target = "net/minecraft/server/ServerPlayerInteractionManager.useBlock (Lnet/minecraft/entity/living/player/PlayerEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;FFF)Lnet/minecraft/world/InteractionResult;",shift = At.Shift.AFTER))
    private void onReasonLoggingEnd(CallbackInfo ci) {
		ChunkDebugToolLogger.resetReason();
	}
}
