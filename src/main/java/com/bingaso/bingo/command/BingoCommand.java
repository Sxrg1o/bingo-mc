package com.bingaso.bingo.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.bingaso.bingo.card.BingoCardSubCommand;
import com.bingaso.bingo.match.BingoMatchSettingsSubCommand;
import com.bingaso.bingo.match.BingoMatchStartSubCommand;
import com.bingaso.bingo.team.select.BingoTeamSubCommand;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class BingoCommand implements CommandExecutor, TabCompleter {
    
    private final Map<String, BingoSubCommand> subCommands = new HashMap<>();
    
    public BingoCommand() {
        // Register subcommands
        subCommands.put("team", new BingoTeamSubCommand());
        subCommands.put("card", new BingoCardSubCommand());
        subCommands.put("start", new BingoMatchStartSubCommand());
        subCommands.put("settings", new BingoMatchSettingsSubCommand());
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return new ArrayList<>(subCommands.keySet());
        }

        if (args.length > 1) {
            BingoSubCommand subCommand = subCommands.get(args[0].toLowerCase());
            if (subCommand != null) {
                String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
                return subCommand.getTabCompletions(sender, subArgs);
            }
        }
        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sendHelpMessage(sender);
            return true;
        }
        
        String subCommandName = args[0].toLowerCase();
        BingoSubCommand subCommand = subCommands.get(subCommandName);
        
        if (subCommand == null) {
            sender.sendMessage(Component.text("Unknown subcommand: " + subCommandName, NamedTextColor.RED));
            sendHelpMessage(sender);
            return true;
        }
        
        // Remove the subcommand from args array
        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
        return subCommand.execute(sender, subArgs);
    }
    
    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(Component.text("=== Bingo Commands ===", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("/bingo team - Team management.", NamedTextColor.GREEN));
        sender.sendMessage(Component.text("/bingo card - Shows the bingo card.", NamedTextColor.GREEN));
        sender.sendMessage(Component.text("/bingo start - Starts the Bingo Game.", NamedTextColor.GREEN));
        sender.sendMessage(Component.text("/bingo settings - Opens the configuration.", NamedTextColor.GREEN));
    }
    
}
