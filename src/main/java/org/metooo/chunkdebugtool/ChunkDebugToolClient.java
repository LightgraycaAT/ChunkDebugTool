package org.metooo.chunkdebugtool;

import net.minecraft.client.options.KeyBinding;
import net.ornithemc.osl.entrypoints.api.client.ClientModInitializer;
import net.ornithemc.osl.keybinds.api.KeyBindingEvents;
import net.ornithemc.osl.lifecycle.api.client.MinecraftClientEvents;
import net.ornithemc.osl.networking.api.ChannelRegistry;
import net.ornithemc.osl.networking.api.client.ClientPlayNetworking;
import org.lwjgl.input.Keyboard;
import org.metooo.chunkdebugtool.hud.ChunkGridStyle;
import org.metooo.chunkdebugtool.hud.Chunkdata;
import org.metooo.chunkdebugtool.hud.Controller;
import org.metooo.chunkdebugtool.hud.GuiChunkGrid;

public class ChunkDebugToolClient implements ClientModInitializer{
    private static final KeyBinding toggleChunkDebugTool =new KeyBinding("toggleChunkDebugTool", Keyboard.KEY_F6, "Chunk Debug Tool");
    private static final KeyBinding toggleChangeStyle = new KeyBinding("toggleChangeStyle", Keyboard.KEY_F8, "Chunk Debug Tool");
    private static boolean running = false;
    private static boolean loggedOut = false;

    @Override
    public void initClient() {
        ChannelRegistry.register(ChunkDebugTool.DATA_CHANNEL, true, true);
        KeyBindingEvents.REGISTER_KEYBINDS.register(keyBindingRegistry -> keyBindingRegistry.register(toggleChunkDebugTool));
        KeyBindingEvents.REGISTER_KEYBINDS.register(keyBindingRegistry -> keyBindingRegistry.register(toggleChangeStyle));

        GuiChunkGrid.instance = new GuiChunkGrid();

        MinecraftClientEvents.TICK_END.register(minecraft -> {
            running=minecraft.isIntegratedServerRunning()||minecraft.getCurrentServerEntry()!=null;
            if (running) {
                Controller.tick();
                loggedOut = true;
                if (GuiChunkGrid.instance.getMinimapType() != 0) {
                    GuiChunkGrid.instance.getController().updateMinimap();
                }
            } else if (loggedOut) {
                loggedOut = false;
                GuiChunkGrid.instance = new GuiChunkGrid();
            }
            while (toggleChunkDebugTool.consumeClick()){
                minecraft.openScreen(GuiChunkGrid.instance);
            }
            while (toggleChangeStyle.consumeClick()){
                ChunkGridStyle.changeStyle();
            }
        });
        ClientPlayNetworking.registerListener(ChunkDebugTool.DATA_CHANNEL, (ctx, data) -> {
            Chunkdata.processPacket(data);
        });
    }
}
