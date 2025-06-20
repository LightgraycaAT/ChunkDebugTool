package org.metooo.chunkdebugtool.mixins;

import org.metooo.chunkdebugtool.ChunkDebugToolLogger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	public LivingEntityMixin(World world) {
		super(world);
	}

	@Inject(method = "moveEntityWithVelocity", at = @At(value = "INVOKE", target = "net/minecraft/entity/living/LivingEntity.move (Lnet/minecraft/entity/MoverType;DDD)V",ordinal = 3))
	public void onReasonLoggingStart(float velocityX, float velocityY, float velocityZ, CallbackInfo ci) {
		if (ChunkDebugToolLogger.logger.enabled)
			ChunkDebugToolLogger.setReason("Entity walking around: " + getName());
	}
	@Inject(method = "moveEntityWithVelocity", at = @At(value = "INVOKE", target = "net/minecraft/entity/living/LivingEntity.move (Lnet/minecraft/entity/MoverType;DDD)V",ordinal = 3, shift = At.Shift.AFTER))
	public void onReasonLoggingEnd(float velocityX, float velocityY, float velocityZ, CallbackInfo ci) {
		ChunkDebugToolLogger.resetReason();
	}
}
