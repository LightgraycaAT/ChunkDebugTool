package org.metooo.chunkdebugtool.mixins;

import org.metooo.chunkdebugtool.ChunkDebugToolLogger;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.ai.goal.GoalSelector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.objectweb.asm.Opcodes.GOTO;

@Mixin(GoalSelector.class)
public class GoalSelectorMixin {
	@Inject(method = "tick",at= @At(value = "FIELD", target = "net/minecraft/entity/ai/goal/GoalSelector$Entry.f_3327432 : Z",ordinal = 0))
	public void onReasonLoggingStart(CallbackInfo ci, @Local GoalSelector.Entry entry) {
		if(ChunkDebugToolLogger.logger.enabled)
			ChunkDebugToolLogger.setReason("Entity" + "lazy to do");
	}
	@Inject(method = "tick",at= @At(value = "JUMP", opcode = GOTO, ordinal = 3))
	public void onReasonLoggingEnd(CallbackInfo ci) {
		ChunkDebugToolLogger.resetReason();
	}
	@Inject(method = "tick",at= @At(value = "INVOKE", target = "net/minecraft/entity/ai/goal/Goal.tick ()V"))
	public void onReasonLoggingStart2(CallbackInfo ci, @Local GoalSelector.Entry entry) {
		if(ChunkDebugToolLogger.logger.enabled)
			ChunkDebugToolLogger.setReason("Entity" + "lazy to do");
	}
	@Inject(method = "tick",at= @At(value = "INVOKE", target = "net/minecraft/util/profiler/Profiler.pop ()V",ordinal = 1))
	public void onReasonLoggingEnd2(CallbackInfo ci) {
		ChunkDebugToolLogger.resetReason();
	}

}
