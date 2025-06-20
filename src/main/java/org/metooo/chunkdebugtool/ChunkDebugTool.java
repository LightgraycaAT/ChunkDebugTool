package org.metooo.chunkdebugtool;

import org.metooo.chunkdebugtool.hud.ChunkGridStyle;
import org.metooo.chunkdebugtool.hud.Chunkdata;
import org.metooo.chunkdebugtool.hud.Controller;
import org.metooo.chunkdebugtool.hud.GuiChunkGrid;
import net.minecraft.client.options.KeyBinding;
import net.ornithemc.osl.keybinds.api.KeyBindingEvents;
import net.ornithemc.osl.lifecycle.api.client.MinecraftClientEvents;
import net.ornithemc.osl.networking.api.client.ClientPlayNetworking;
import net.ornithemc.osl.networking.api.server.ServerConnectionEvents;
import net.ornithemc.osl.networking.api.server.ServerPlayNetworking;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.ornithemc.osl.entrypoints.api.ModInitializer;
import net.ornithemc.osl.entrypoints.api.client.ClientModInitializer;
import org.lwjgl.input.Keyboard;


public class ChunkDebugTool implements ModInitializer, ClientModInitializer {

	public static final Logger LOGGER = LogManager.getLogger("ChunkDebugTool");
	private static final KeyBinding toggleEnabled = new KeyBinding("toggleEnabled", Keyboard.KEY_F7, "Chunk Debug Tool");
	private static final KeyBinding toggleChunkDebugTool =new KeyBinding("toggleChunkDebugTool", Keyboard.KEY_F6, "Chunk Debug Tool");
	private static final KeyBinding toggleChangeStyle = new KeyBinding("toggleChangeStyle", Keyboard.KEY_F8, "Chunk Debug Tool");
	public static final String DATA_CHANNEL = "CDT|Data";
	public static boolean enabled = false;
	private static boolean running = false;
	private static boolean loggedOut = false;
	@Override
	public void init() {
		LOGGER.info("initializing chunk debug tool!");
		GuiChunkGrid.instance = new GuiChunkGrid();
		ServerPlayNetworking.registerListener(DATA_CHANNEL, (server, handler, player, data) -> {
			ChunkDebugToolLogger.logger.registerPlayer(player, data);
			return true;
		});
		ServerConnectionEvents.DISCONNECT.register((minecraftServer1, serverPlayerEntity) -> ChunkDebugToolLogger.logger.unregisterPlayer(serverPlayerEntity));
	}

	@Override
	public void initClient() {
		KeyBindingEvents.REGISTER_KEYBINDS.register(keyBindingRegistry -> keyBindingRegistry.register(toggleChunkDebugTool));
		KeyBindingEvents.REGISTER_KEYBINDS.register(keyBindingRegistry -> keyBindingRegistry.register(toggleEnabled));
		KeyBindingEvents.REGISTER_KEYBINDS.register(keyBindingRegistry -> keyBindingRegistry.register(toggleChangeStyle));
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
			while (toggleEnabled.consumeClick()){
				enabled = !enabled;
				if(!enabled) {
					ChunkDebugToolLogger.logger.disable();
				}
			}
			while (toggleChangeStyle.consumeClick()){
				ChunkGridStyle.changeStyle();
			}
		});
		ClientPlayNetworking.registerListener(DATA_CHANNEL, (minecraft, handler, data) -> {
			Chunkdata.processPacket(data);
			return true;
		});
	}
}
