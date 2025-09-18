package com.bingaso.bingo.team.command;

import com.bingaso.bingo.BingoPlugin;
import com.bingaso.bingo.command.BingoSubCommand;
import com.bingaso.bingo.match.BingoMatch;
import com.bingaso.bingo.match.BingoMatch.MaxPlayersException;
import com.bingaso.bingo.team.BingoTeam;
import com.bingaso.bingo.team.BingoTeamRepository.TeamNameAlreadyExistsException;
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

public class BinogTeamCreateHandler implements BingoSubCommand {

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 1) {
            sendHelpMessage(sender);
            return true;
        }

        String teamName = args[0];
        Player targetPlayer = null;

        if(teamName.length() < 3 || teamName.length() > 14) {
            sender.sendMessage(Component.text("Team name must be between 3 and 14 characters.", NamedTextColor.RED));
            return true;
        }

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
                    "You cannot create teams right now.",
                    NamedTextColor.RED
                ));
                return true;
            }
        }

        try {
            BingoTeam bingoTeam = gameManager.createBingoTeam(teamName);
            gameManager.addPlayerToBingoTeam(targetPlayer, bingoTeam);
            sender.sendMessage(Component.text("Team created successfully! Team Name: " + bingoTeam.getName(), NamedTextColor.GREEN));
            // Notify target player if different from sender
            if (!sender.equals(targetPlayer)) {
                targetPlayer.sendMessage(Component.text("You have been added to team: " + bingoTeam.getName(), NamedTextColor.GREEN));
            }
        } catch (MaxPlayersException e) {
            sender.sendMessage(Component.text("Error: " + e.getMessage(), NamedTextColor.RED));
        } catch (TeamNameAlreadyExistsException e) {
            sender.sendMessage(Component.text("A team with this name already exists. Please choose a different name.", NamedTextColor.RED));
        }

        BingoTeamSelectGui.getInstance().updateInventories();
        return true;
    }
    
    public void sendHelpMessage(CommandSender sender) {
        if(isOpOrConsole(sender)) {
            sender.sendMessage(Component.text("/bingo team create <teamName> [--asPlayer <playerName>] - Create a new team", NamedTextColor.GREEN));
        } else {
            sender.sendMessage(Component.text("/bingo team create <teamName> - Create a new team", NamedTextColor.GREEN));
        }
    }

    public boolean isOpOrConsole(CommandSender sender) {
        return sender.isOp() || sender instanceof ConsoleCommandSender;
    }
    
    @Override
    public @Nullable List<String> getTabCompletions(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length > 2) {
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
        }
        
        return new ArrayList<>();
    }
}
