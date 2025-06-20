package org.metooo.chunkdebugtool.mixins.hud;

import org.metooo.chunkdebugtool.hud.GuiChunkGrid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Window;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@Shadow
	@Final
	private Minecraft minecraft;
	@Inject(method = "render(FJ)V", at = @At(value = "INVOKE", target = "net/minecraft/client/gui/GameGui.render (F)V"))
	public void render(float tickDelta, long startTime, CallbackInfo ci) {
		if (this.minecraft.world != null && this.minecraft.player != null)
		{
			if (GuiChunkGrid.instance.getMinimapType() != 0) {
				Window window = new Window(this.minecraft);
				GuiChunkGrid.instance.renderMinimap(window.getWidth(), window.getHeight());
			}
		}
	}
}
