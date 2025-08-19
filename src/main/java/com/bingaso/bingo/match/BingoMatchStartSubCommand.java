package com.bingaso.bingo.match;

import com.bingaso.bingo.BingoPlugin;
import com.bingaso.bingo.command.BingoSubCommand;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Command executor for bingo match start.
 * Handles: /bingostart
 */
public class BingoMatchStartSubCommand implements BingoSubCommand {

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if(!isOpOrConsole(sender)) {
            sender.sendMessage(Component.text("You do not have permission to start the match.", NamedTextColor.RED));
            return true;
        }
        if(BingoPlugin.getInstance().getBingoMatch().getState() != BingoMatch.State.IN_PROGRESS) {
            BingoPlugin.getInstance().getBingoMatch().start();
        }
        return true;
    }

    public boolean isOpOrConsole(CommandSender sender) {
        return sender.isOp() || sender instanceof ConsoleCommandSender;
    }
    

    @Override
    public @Nullable List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String[] args) {
        // TODO Auto-generated method stub
        return null;
    }
}
