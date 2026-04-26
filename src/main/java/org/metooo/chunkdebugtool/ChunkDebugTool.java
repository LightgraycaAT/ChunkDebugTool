package org.metooo.chunkdebugtool;

import net.ornithemc.osl.core.api.util.NamespacedIdentifier;
import net.ornithemc.osl.core.impl.util.NamespacedIdentifierImpl;
import net.ornithemc.osl.networking.api.ChannelRegistry;
import net.ornithemc.osl.networking.api.server.ServerConnectionEvents;
import net.ornithemc.osl.networking.api.server.ServerPlayNetworking;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.ornithemc.osl.entrypoints.api.ModInitializer;


public class ChunkDebugTool implements ModInitializer {

	private static final String MOD_NAME = "chunk_debug_tool";
	public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

	public static final NamespacedIdentifier DATA_CHANNEL = new NamespacedIdentifierImpl(MOD_NAME, "data");
	public static boolean enabled = false;

	@Override
	public void init() {
		LOGGER.info("initializing chunk debug tool!");
		ChannelRegistry.register(ChunkDebugTool.DATA_CHANNEL, true, true);

		ServerPlayNetworking.registerListener(DATA_CHANNEL, (ctx, data) -> {
			ChunkDebugToolLogger.logger.registerPlayer(ctx.player(), data);
		});
		ServerConnectionEvents.DISCONNECT.register((minecraftServer1, serverPlayerEntity) -> ChunkDebugToolLogger.logger.unregisterPlayer(serverPlayerEntity));

	}
}
