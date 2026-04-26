package org.metooo.chunkdebugtool.mixins;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.handler.CommandListener;
import net.minecraft.server.command.handler.CommandManager;
import net.minecraft.server.command.handler.CommandRegistry;
import org.metooo.chunkdebugtool.command.ChunkDebugToolCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandManager.class)
public abstract class CommandManagerMixin extends CommandRegistry implements CommandListener {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void registerCommands(MinecraftServer server, CallbackInfo ci) {
        this.register(new ChunkDebugToolCommand());
    }
}
