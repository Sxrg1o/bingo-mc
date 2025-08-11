package com.bingaso.bingo.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Represents a team of players in the Bingo game.
 * Manages players by their unique identifiers (UUIDs) to handle cases
 * where players log off and on.
 */
public class BingoTeam {

    /**
     * Thrown when an attempt is made to add a player to a team that is already full.
     */
    public static class MaxPlayersException extends Exception {

        public MaxPlayersException(String message) {
            super(message);
        }
    }

    /**
     * Thrown when an operation requires a configuration value (like max team size)
     * that has not yet been set.
     */
    public static class ConfigNotSetException extends Exception {

        public ConfigNotSetException(String message) {
            super(message);
        }
    }

    /**
     * Thrown when attempting to add a player into a team in which it is
     * already in.
     */
    public static class PlayersAlreadyInTeam extends Exception {

        public PlayersAlreadyInTeam(String message) {
            super(message);
        }
    }

    private static final List<BingoTeam> ALL_TEAMS = new ArrayList<>();
    private static int MAX_SIZE = -1;

    /**
     * Sets the maximum size of the teams
     */
    public static void setTeamsMaxSize(int newMaxSize) {
        MAX_SIZE = newMaxSize;
        // Deletes all teams as size has changes
        for (BingoTeam t : ALL_TEAMS) {
            for (BingoPlayer player : t.getPlayers()) {
                t.removePlayer(player);
            }
        }
    }

    /**
     * Get all teams.
     * @return All teams created.
     */
    public static List<BingoTeam> getAllTeams() {
        return ALL_TEAMS;
    }

    /**
     * Sets the maximum size of the teams
     */
    public static int getTeamsMaxSize() {
        return MAX_SIZE;
    }

    private final List<BingoPlayer> players;
    private String name = "";
    private final UUID uuid;
    private final Set<Material> foundItems = new HashSet<>();

    /**
     * Constructs a new Team with a given name.
     */
    public BingoTeam() {
        this.players = new ArrayList<>();
        this.uuid = UUID.randomUUID();
        ALL_TEAMS.add(this);
    }

    /**
     * Gets the uuid of the team.
     *
     * @return The team's unique identificator.
     */
    public UUID getUniqueId() {
        return uuid;
    }

    /**
     * Gets the name of the team.
     *
     * @return The team's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Changes the team's name.
     *
     * @param name The new name of the team.
     */
    public void SetName(String name) {
        this.name = name;
    }

    /**
     * Adds a player to the team by their UUID.
     * Does nothing if the player is already on the team.
     *
     * @param bingoPlayer The UUID of the player to add.
     * @throws ConfigNotSetException If the maxSize is not set.
     * @throws MaxPlayersException If the team is already full.
     * @throws PlayersAlreadyInTeam If the player is already in the team.
     */
    public void addPlayer(BingoPlayer bingoPlayer)
        throws ConfigNotSetException, MaxPlayersException, PlayersAlreadyInTeam {
        if (BingoTeam.MAX_SIZE == -1) {
            throw new BingoTeam.ConfigNotSetException("MaxSize not set.");
        }
        if (players.size() + 1 > BingoTeam.MAX_SIZE) {
            throw new BingoTeam.MaxPlayersException("Team is already full");
        }
        if (players.contains(bingoPlayer)) {
            throw new BingoTeam.PlayersAlreadyInTeam(
                "Player is already in the team."
            );
        }
        players.add(bingoPlayer);
    }

    /**
     * Removes a player from the team by their UUID.
     * If the team empties, the team is removed from the ALL_TEAMS list.
     *
     * @param bingoPlayer The bingo player to remove.
     * @return true if the player was on the team and was removed, false otherwise.
     */
    public boolean removePlayer(BingoPlayer bingoPlayer) {
        boolean b = players.remove(bingoPlayer);
        if (b && players.size() == 0) {
            ALL_TEAMS.remove(this);
        }
        return b;
    }

    /**
     * Checks if a player is a member of this team.
     *
     * @param bingoPlayer The bingo player to check.
     * @return true if the player is on this team, false otherwise.
     */
    public boolean hasPlayer(BingoPlayer bingoPlayer) {
        return players.contains(bingoPlayer);
    }

    /**
     * Gets the number of players on the team.
     *
     * @return The size of the team.
     */
    public int getSize() {
        return players.size();
    }

    /**
     * Gets an unmodifiable list of all player UUIDs on this team.
     * This prevents external code from modifying the team's player list directly.
     *
     * @return An unmodifiable list of player UUIDs.
     */
    public List<BingoPlayer> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public void addFoundItem(Material item) {
        foundItems.add(item);
    }

    public Set<Material> getFoundItems() {
        return foundItems;
    }

    public void clearFoundItems() {
        foundItems.clear();
    }

    /**
     * Gets a list of all team members who are currently online.
     *
     * @return A list of online {@link Player} objects.
     */
    public List<Player> getOnlinePlayers() {
        List<Player> onlinePlayers = new ArrayList<>();
        for (BingoPlayer player : players) {
            Player onlinePlayer = player.getOnlinePlayer();
            if (onlinePlayer != null) {
                onlinePlayers.add(onlinePlayer);
            }
        }
        return onlinePlayers;
    }

    /**
     * Sends a message to all online members of the team.
     *
     * @param message The message to send.
     */
    public void broadcastMessage(String message) {
        for (Player player : getOnlinePlayers()) {
            player.sendMessage(message);
        }
    }

    /**
     * Checks if this Team object is equal to another object.
     * Two teams are considered equal if they have the same name (case-sensitive).
     *
     * @param o The object to compare against.
     * @return true if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BingoTeam team = (BingoTeam) o;
        return name.equals(team.name);
    }

    /**
     * Generates a hash code for the Team, based on its name.
     *
     * @return The hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
