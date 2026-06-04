package org.metooo.chunkdebugtool;

import io.netty.buffer.Unpooled;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.ornithemc.osl.networking.api.PacketBuffer;
import net.ornithemc.osl.networking.api.server.ServerPlayNetworking;

public class ChunkDebugToolHandler {
	public static void sendNBTChunkData(ServerPlayerEntity sender,int type,NbtCompound compound) {
		PacketBuffer data = new PacketBuffer(Unpooled.buffer());
		data.writeInt(type);
		try {
			data.writeNbtCompound(compound);
		} catch (Exception ignored){}
		ServerPlayNetworking.send(sender, ChunkDebugTool.DATA_CHANNEL, data);
	}
}
