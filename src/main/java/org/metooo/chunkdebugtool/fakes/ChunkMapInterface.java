package org.metooo.chunkdebugtool.fakes;

import net.minecraft.util.math.ChunkPos;

import java.util.Iterator;

public interface ChunkMapInterface {
	default Iterator<ChunkPos> chunkDebugTool$carpetGetAllChunkCoordinates(){
		return null;
	}
}
