package com.bingaso.bingo.team.select.handlers;

import com.bingaso.bingo.BingoPlugin;
import com.bingaso.bingo.command.BingoSubCommand;
import com.bingaso.bingo.match.BingoMatch;
import com.bingaso.bingo.player.BingoPlayer;
import com.bingaso.bingo.team.BingoTeam;
import com.bingaso.bingo.team.select.BingoTeamSelectGui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LeaveTeamHandler implements BingoSubCommand {

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String[] args) {
        Player targetPlayer = null;

        // Handle Flags
        for (int i = 1; i < args.length - 1; i+=2) {
            switch (args[i]) {
                case "--asPlayer":
                    if(!isOpOrConsole(sender)) {
                        sender.sendMessage(Component.text("You do not have permission to use the --asPlayer flag.", NamedTextColor.RED));
                        return true;
                    }
                    if (i + 1 >= args.length) {
                        sendHelpMessage(sender);
                        return true;
                    }

                    String playerName = args[i + 1];
                    targetPlayer = Bukkit.getPlayer(playerName);
                    if (targetPlayer == null) {
                        sender.sendMessage(Component.text("Player '" + playerName + "' not found or not online.", NamedTextColor.RED));
                        return true;
                    }
                    break;
                default:
                    sender.sendMessage(Component.text("Unknown flag: " + args[i], NamedTextColor.RED));
                    return true;
            }
        }

        BingoMatch gameManager = BingoPlugin.getInstance().getBingoMatch();

        // If no --asPlayer flag, use the sender (must be a player)
        if (targetPlayer == null) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Component.text("Console must use --asPlayer <playerName> flag.", NamedTextColor.RED));
                return true;
            }
            targetPlayer = (Player) sender;

            // Only allow team creation in lobby state for non-ops
            if(gameManager.getState() != BingoMatch.State.LOBBY) {
                sender.sendMessage(Component.text(
                    "You cannot join teams right now.",
                    NamedTextColor.RED
                ));
                return true;
            }
        }

        BingoPlayer bingoPlayer = gameManager.getBingoPlayerRepository().findByUUID(targetPlayer.getUniqueId());
        BingoTeam oldTeam = gameManager.getBingoTeamRepository().findTeamByPlayer(bingoPlayer);
        if(oldTeam == null) {
            sender.sendMessage(Component.text("You are not in a team.", NamedTextColor.RED));
            return true;
        }
        gameManager.removePlayerFromBingoTeam(targetPlayer);
        // Notify target player if different from sender
        if (!sender.equals(targetPlayer)) {
            targetPlayer.sendMessage(Component.text("You have been removed from team: " + oldTeam.getName(), NamedTextColor.GREEN));
        } else {
            sender.sendMessage(Component.text("Successfully left team " + oldTeam.getName(), NamedTextColor.GREEN));
        }

        BingoTeamSelectGui.getInstance().updateInventories();
        return true;
    }
    
    public void sendHelpMessage(CommandSender sender) {
        if(isOpOrConsole(sender)) {
            sender.sendMessage(Component.text("/bingo team leave [--asPlayer <playerName>] - Leave an existing team", NamedTextColor.GREEN));
        } else {
            sender.sendMessage(Component.text("/bingo team leave - Leave your current team", NamedTextColor.GREEN));
        }
    }

    public boolean isOpOrConsole(CommandSender sender) {
        return sender.isOp() || sender instanceof ConsoleCommandSender;
    }
    
    @Override
    public @Nullable List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String[] args) {
        // Check if we're completing after --asPlayer
        for (int i = 1; i < args.length - 1; i++) {
            if ("--asPlayer".equals(args[i]) && i + 1 == args.length - 1) {
                return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(java.util.stream.Collectors.toList());
            }
        }
        
        // Suggest --asPlayer flag if not already present
        boolean hasAsPlayerFlag = false;
        for (String arg : args) {
            if ("--asPlayer".equals(arg)) {
                hasAsPlayerFlag = true;
                break;
            }
        }
        
        if (!hasAsPlayerFlag) {
            List<String> completions = new ArrayList<>();
            completions.add("--asPlayer");
            return completions;
        }
        return new ArrayList<>();
    }
}
