package com.bingaso.bingo.model;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a player in the Bingo game with team management capabilities.
 */
public class BingoPlayer {
    private final static Map<UUID, BingoPlayer> ALL_PLAYERS = new HashMap<>();

    /**
     * Retrieves a BingoPlayer instance by their UUID.
     *
     * @param uuid The UUID of the player to retrieve.
     * @return The BingoPlayer instance, or null if not found.
     */
    public static BingoPlayer getBingoPlayer(UUID uuid) {
        return ALL_PLAYERS.get(uuid);
    }

    private final UUID uuid;
    private String name;
    private BingoTeam team = null;

    /**
     * Sets the team for this player.
     *
     * @param newTeam The new team to assign to this player, or null to remove from team.
     */
    public void setTeam(BingoTeam newTeam) {
        this.team = newTeam;
    }

    /**
     * Gets the team this player is currently on.
     *
     * @return The player's current team, or null if not on a team.
     */
    public BingoTeam getTeam() {
        return team;
    }

    /**
     * Gets the online Player instance for this BingoPlayer.
     *
     * @return The online Player instance, or null if the player is offline.
     */
    public Player getOnlinePlayer() {
        return Bukkit.getPlayer(uuid);
    }

    /**
     * Constructs a new BingoPlayer from an online Player.
     *
     * @param player The online Player to create a BingoPlayer for.
     */
    public BingoPlayer(Player player) {
        this.uuid = player.getUniqueId();
        this.name = player.getName();
        ALL_PLAYERS.put(this.uuid, this);
    }

    /**
     * Gets the UUID of this player.
     *
     * @return The player's unique identifier.
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Gets the name of this player.
     *
     * @return The player's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Checks if this player is currently online.
     *
     * @return true if the player is online, false otherwise.
     */
    public boolean isOnline() {
        Player player = Bukkit.getPlayer(uuid);
        return player != null;
    }

    /**
     * Gets the online Player instance for this BingoPlayer.
     *
     * @return The online Player instance, or null if the player is offline.
     */
    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    /**
     * Removes this BingoPlayer from the global registry.
     * Should be called when the player leaves the server.
     */
    public void cleanup() {
        if (this.team != null) {
            this.team.removePlayer(this);
        }
        ALL_PLAYERS.remove(this.uuid);
    }

    /**
     * Checks if this BingoPlayer is equal to another object.
     * Two BingoPlayers are considered equal if they have the same UUID.
     *
     * @param o The object to compare against.
     * @return true if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BingoPlayer that = (BingoPlayer) o;
        return Objects.equals(uuid, that.uuid);
    }

    /**
     * Returns a string representation of this BingoPlayer.
     *
     * @return A string containing the player's UUID and name.
     */
    @Override
    public String toString() {
        return "BingoPlayer{" +
                "uuid=" + uuid +
                ", name='" + name + "\'" +
                '}';
    }
}
