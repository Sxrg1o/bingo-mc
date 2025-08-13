package com.bingaso.bingo.player;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Represents a player in the Bingo game with team management capabilities.
 * 
 * This class wraps a Bukkit Player and provides persistent storage of player
 * information such as UUID and name, even when the player goes offline.
 * It serves as a bridge between the Bukkit Player API and the Bingo game logic.
 * @since 1.0
 */
public class BingoPlayer {

    private final UUID uuid;
    private final String name;

    /**
     * Constructs a new BingoPlayer from an online Player.
     * 
     * The constructor captures the player's UUID and name at the time of creation,
     * allowing the BingoPlayer to persist this information even if the player goes offline.
     *
     * @param player The online Player to create a BingoPlayer for
     * @throws IllegalArgumentException if player is null
     * @since 1.0
     */
    BingoPlayer(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        this.uuid = player.getUniqueId();
        this.name = player.getName();
    }

    /**
     * Gets the UUID of this player.
     * 
     * The UUID is a persistent unique identifier that remains constant
     * across player name changes and server sessions.
     *
     * @return The player's unique identifier
     * @since 1.0
     */
    public UUID getUniqueId() {
        return uuid;
    }

    /**
     * Gets the name of this player.
     * 
     * Note: This returns the name that was captured when the BingoPlayer
     * was created and may not reflect the current name if the player has
     * changed their username since then.
     *
     * @return The player's name at the time of BingoPlayer creation
     * @since 1.0
     */
    public String getName() {
        return name;
    }

    /**
     * Checks if this player is currently online on the server.
     * 
     * This method queries the Bukkit server to determine if the player
     * is currently connected and available.
     *
     * @return true if the player is online, false otherwise
     * @since 1.0
     */
    public boolean isOnline() {
        Player player = Bukkit.getPlayer(uuid);
        return player != null;
    }

    /**
     * Gets the online Player instance for this BingoPlayer.
     * 
     * This method attempts to retrieve the current Bukkit Player instance
     * from the server. The returned Player can be used to interact with
     * the player if they are online.
     *
     * @return The online Player instance, or null if the player is offline
     * @since 1.0
     */
    public Player getOnlinePlayer() {
        return Bukkit.getPlayer(uuid);
    }
}
