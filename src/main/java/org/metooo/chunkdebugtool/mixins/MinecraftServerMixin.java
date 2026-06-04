package org.metooo.chunkdebugtool.mixins;

import org.metooo.chunkdebugtool.ChunkDebugToolLogger;
import net.minecraft.server.MinecraftServer;
import org.metooo.chunkdebugtool.fakes.MinecraftServerInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements MinecraftServerInterface {

	@Unique
    private boolean flag = false;

	@Inject(method="tick",at= @At(value = "INVOKE", target = "net/minecraft/server/MinecraftServer.saveWorlds (Z)V"))
	public void onReasonLoggingStart(CallbackInfo ci) {
		if(ChunkDebugToolLogger.logger.enabled)
			ChunkDebugToolLogger.setReason("Autosave queuing chunks for unloading");
	}
	@Inject(method="tick",at= @At(value = "INVOKE", target = "net/minecraft/server/MinecraftServer.saveWorlds (Z)V",shift = At.Shift.AFTER))
    public void onReasonLoggingEnd(CallbackInfo ci) {
		ChunkDebugToolLogger.resetReason();
	}
	@Inject(method="tick",at= @At(value = "INVOKE", target = "net/minecraft/util/profiler/Profiler.pop ()V",ordinal = 3, shift = At.Shift.AFTER))
	public void onReasonLoggingSend(CallbackInfo ci) {
		if(ChunkDebugToolLogger.logger.enabled)ChunkDebugToolLogger.logger.sendAll();
	}

	@Override
	public void chunkDebugTool$setFlag(boolean flag) {
		this.flag = flag;
	}

	@Override
	public boolean chunkDebugTool$getFlag() {
		return flag;
	}
}
