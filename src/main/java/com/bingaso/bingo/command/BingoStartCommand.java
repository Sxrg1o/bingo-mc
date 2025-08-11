package com.bingaso.bingo.command;

import com.bingaso.bingo.BingoPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Command executor for bingo match start.
 * Handles: /bingostart
 */
public class BingoStartCommand implements CommandExecutor {

    @Override
    public boolean onCommand(
        CommandSender sender,
        Command command,
        String label,
        String[] args
    ) {
        BingoPlugin.getInstance().getGameManager().startMatch();
        return true;
    }
}
