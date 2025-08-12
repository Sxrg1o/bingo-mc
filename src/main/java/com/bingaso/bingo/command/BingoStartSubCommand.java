package com.bingaso.bingo.command;

import com.bingaso.bingo.BingoPlugin;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Command executor for bingo match start.
 * Handles: /bingostart
 */
public class BingoStartSubCommand implements SubCommand {

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String[] args) {
        BingoPlugin.getInstance().getGameManager().startMatch();
        return true;
    }

    @Override
    public @Nullable List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String[] args) {
        // TODO Auto-generated method stub
        return null;
    }
}
