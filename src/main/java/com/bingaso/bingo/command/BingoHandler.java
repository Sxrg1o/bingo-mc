package com.bingaso.bingo.command;

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

public abstract class BingoHandler implements BingoSubCommand {
    
    /**
     * Sends a help message for this command handler.
     * 
     * @param sender The command sender to send the help message to
     */
    public abstract void sendHelpMessage(@NotNull CommandSender sender);
    
    /**
     * Checks if the sender has operator permissions or is the console.
     * 
     * @param sender The command sender to check
     * @return true if the sender is an operator or console, false otherwise
     */
    protected boolean isOpOrConsole(@NotNull CommandSender sender) {
        return sender.isOp() || sender instanceof ConsoleCommandSender;
    }
    
    /**
     * Parses command flags and returns the target player.
     * Handles the --asPlayer flag and validates permissions.
     * 
     * @param sender The command sender
     * @param args The command arguments
     * @param startIndex The index to start parsing flags from
     * @return The target player, or null if parsing failed or no target specified
     */
    @Nullable
    protected Player parseTargetPlayer(@NotNull CommandSender sender, @NotNull String[] args, int startIndex) {
        Player targetPlayer = null;
        
        // Handle Flags
        for (int i = startIndex; i < args.length - 1; i += 2) {
            switch (args[i]) {
                case "--asPlayer":
                    if (!isOpOrConsole(sender)) {
                        sender.sendMessage(Component.text("You do not have permission to use the --asPlayer flag.", NamedTextColor.RED));
                        return null;
                    }
                    if (i + 1 >= args.length) {
                        sendHelpMessage(sender);
                        return null;
                    }

                    String playerName = args[i + 1];
                    targetPlayer = Bukkit.getPlayer(playerName);
                    if (targetPlayer == null) {
                        sender.sendMessage(Component.text("Player '" + playerName + "' not found or not online.", NamedTextColor.RED));
                        return null;
                    }
                    break;
                default:
                    sender.sendMessage(Component.text("Unknown flag: " + args[i], NamedTextColor.RED));
                    return null;
            }
        }
        
        return targetPlayer;
    }
    
    /**
     * Validates that the sender is a player or returns the target player from flags.
     * If no target player is specified via flags, the sender must be a player.
     * 
     * @param sender The command sender
     * @param targetPlayer The target player from flags (can be null)
     * @return The final target player, or null if validation failed
     */
    @Nullable
    protected Player validatePlayerTarget(@NotNull CommandSender sender, @Nullable Player targetPlayer) {
        if (targetPlayer == null) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Component.text("Console must use --asPlayer <playerName> flag.", NamedTextColor.RED));
                return null;
            }
            targetPlayer = (Player) sender;
        }
        
        return targetPlayer;
    }
    
    /**
     * Generates tab completions for the --asPlayer flag.
     * 
     * @param args The command arguments
     * @return List of tab completions, or empty list if no completions available
     */
    @NotNull
    protected List<String> getAsPlayerTabCompletions(@NotNull String[] args) {
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
    
    /**
     * Sends a notification to the target player if they are different from the sender.
     * 
     * @param sender The command sender
     * @param targetPlayer The target player
     * @param message The message to send
     */
    protected void notifyTargetPlayer(@NotNull CommandSender sender, @NotNull Player targetPlayer, @NotNull Component message) {
        if (!sender.equals(targetPlayer)) {
            targetPlayer.sendMessage(message);
        }
    }
}
