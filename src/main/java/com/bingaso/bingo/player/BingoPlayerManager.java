package com.bingaso.bingo.player;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;

/**
 * Manages BingoPlayer instances, providing functionality to create, retrieve,
 * and remove BingoPlayer objects associated with Bukkit Player instances.
 * 
 * This class maintains a mapping between player UUIDs and their corresponding 
 * BingoPlayer instances to ensure consistent player data management throughout
 * the bingo game.
 * @since 1.0
 */
public class BingoPlayerManager {
    
    private final HashMap<UUID, BingoPlayer> uuidBingoPlayerMap = new HashMap<>();
    
    /**
     * Constructs a new BingoPlayerManager with an empty player registry.
     */
    public BingoPlayerManager() {}

    /**
     * Creates a new BingoPlayer instance for the given player, or returns the existing one
     * if the player is already registered.
     * 
     * @param player The Bukkit Player instance to create a BingoPlayer for
     * @return The BingoPlayer instance associated with the player (either newly created or existing)
     * @since 1.0
     */
    public BingoPlayer createBingoPlayer(@NotNull Player player) {
        BingoPlayer existingBingoPlayer = uuidBingoPlayerMap.get(player.getUniqueId());
        if(existingBingoPlayer != null) {
            return existingBingoPlayer;
        }
        BingoPlayer bingoPlayer = new BingoPlayer(player);
        uuidBingoPlayerMap.put(player.getUniqueId(), bingoPlayer);
        return bingoPlayer;
    }

    /**
     * Retrieves a BingoPlayer instance by their online Player.
     * 
     * @param player The Player instance to look up
     * @return The BingoPlayer instance, or null if not found
     * @since 1.0
     */
    public BingoPlayer getBingoPlayer(@NotNull Player player) {
        return uuidBingoPlayerMap.get(player.getUniqueId());
    }

    /**
     * Removes a BingoPlayer instance from the manager.
     * 
     * @param player The Player whose BingoPlayer instance should be removed
     * @since 1.0
     */
    public void removeBingoPlayer(@NotNull Player player) {
        uuidBingoPlayerMap.remove(player.getUniqueId());
    }

    /**
     * Broadcasts a message to all Online Players and stores it in the inbox of
     * offline players.
     * @param message The message to broadcast
     */
    public void broadcastMessage(@NotNull Component message) {
        for (BingoPlayer bingoPlayer : uuidBingoPlayerMap.values()) {
            bingoPlayer.sendMessage(message);
        }
    }
}
