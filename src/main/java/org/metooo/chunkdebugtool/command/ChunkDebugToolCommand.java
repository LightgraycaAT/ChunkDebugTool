package org.metooo.chunkdebugtool.command;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.AbstractCommand;
import net.minecraft.server.command.exception.CommandException;
import net.minecraft.server.command.exception.IncorrectUsageException;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.metooo.chunkdebugtool.ChunkDebugTool;

import java.util.Collections;
import java.util.List;

public class ChunkDebugToolCommand extends AbstractCommand {
    @Override
    public String getName() {
        return "chunkdebugtool";
    }

    @Override
    public String getUsage(CommandSource source) {
        return "chunkdebugtool <enable|disable>";
    }

    @Override
    public void run(MinecraftServer server, CommandSource source, String[] args) throws CommandException {
        if(args.length == 1){
            if(args[0].equals("enable")){
                ChunkDebugTool.enabled = true;
                AbstractCommand.sendSuccess(source, this, "Chunk Debug Tool enabled");
            } else if(args[0].equals("disable")){
                ChunkDebugTool.enabled = false;
                AbstractCommand.sendSuccess(source, this, "Chunk Debug Tool disabled");
            } else {
                throw new CommandException("Invalid argument");
            }
        } else {
            throw new IncorrectUsageException("Invalid argument");
        }
    }

    public List<String> getSuggestions(MinecraftServer server, CommandSource source, String[] args, @Nullable BlockPos pos) {
        return args.length != 1 && args.length != 2 ? Collections.emptyList() : suggestMatching(args, "enable", "disable");
    }
}
