package org.metooo.chunkdebugtool.mixins;

import org.metooo.chunkdebugtool.ChunkDebugToolLogger;
import net.minecraft.block.PistonBaseBlock;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PistonBaseBlock.class)
public class PistonBaseBlockMixin {
	@Inject(method = "checkExtended", at = @At("HEAD"))
	private void onReasonLoggingStart(World world, BlockPos pos, BlockState state, CallbackInfo ci) {
		if (ChunkDebugToolLogger.logger.enabled)
			ChunkDebugToolLogger.setReason("Piston scheduled by power source");
	}

	@Inject(method = "checkExtended", at = @At("TAIL"))
	private void onReasonLoggingEnd(World world, BlockPos pos, BlockState state, CallbackInfo ci){
		ChunkDebugToolLogger.resetReason();
	}
}
