package org.metooo.chunkdebugtool.mixins;

import org.metooo.chunkdebugtool.ChunkDebugToolLogger;
import net.minecraft.block.EndPortalBlock;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndPortalBlock.class)
public class EndPortalBlockMixin {
	@Inject(method = "onEntityCollision", at = @At(value = "INVOKE", target = "net/minecraft/entity/Entity.changeDimension (I)Lnet/minecraft/entity/Entity;"))
	public void onReasonLoggingStart(World world, BlockPos pos, BlockState state, Entity entity, CallbackInfo ci) {
		if(ChunkDebugToolLogger.logger.enabled)
			ChunkDebugToolLogger.setReason("Entity going through end portal");
	}
	@Inject(method = "onEntityCollision", at = @At(value = "INVOKE", target = "net/minecraft/entity/Entity.changeDimension (I)Lnet/minecraft/entity/Entity;", shift = At.Shift.AFTER))
	public void onReasonLoggingEnd(World world, BlockPos pos, BlockState state, Entity entity, CallbackInfo ci) {
		ChunkDebugToolLogger.resetReason();
	}
}
