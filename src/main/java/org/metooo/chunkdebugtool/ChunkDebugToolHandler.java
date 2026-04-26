package org.metooo.chunkdebugtool;

import io.netty.buffer.Unpooled;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.ornithemc.osl.networking.api.PacketBuffer;
import net.ornithemc.osl.networking.api.server.ServerPlayNetworking;

public class ChunkDebugToolHandler {
	public static void sendNBTChunkData(ServerPlayerEntity sender,int type,NbtCompound compound) {
		PacketBuffer data = new PacketBuffer(Unpooled.buffer());
		//Todo: make compatible with carpet mod
		data.writeInt(type);
		try {
			data.writeNbtCompound(compound);
		} catch (Exception ignored){}
		ServerPlayNetworking.send(sender, ChunkDebugTool.DATA_CHANNEL, data);
	}
}
