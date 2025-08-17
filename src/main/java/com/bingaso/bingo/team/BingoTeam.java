package com.bingaso.bingo.team;

import com.bingaso.bingo.player.BingoPlayer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Represents a team of players in the Bingo game.
 *
 * This class manages a group of players, their found items, team color,
 * and provides functionality for team communication and item tracking.
 * Teams are automatically assigned colors from a predefined set when created.
 *
 * @since 1.0
 */
public class BingoTeam {

    private final List<BingoPlayer> players = new ArrayList<>();
    private String name = "";
    private final Set<Material> foundItems = new HashSet<>();
    private final NamedTextColor color;

    /**
     * Constructs a new Team with a given name.
     *
     * The team is automatically assigned a color from the available team colors
     * in a round-robin fashion.
     *
     * @param name The name of the team
     * @since 1.0
     */
    BingoTeam(String name, NamedTextColor color) {
        this.color = color;
        this.name = name;
    }

    /**
     * Gets the name of the team.
     *
     * @return The team's name
     * @since 1.0
     */
    public String getName() {
        return name;
    }

    /**
     * Changes the team's name.
     *
     * @param newName The new name of the team
     * @since 1.0
     */
    void setName(String newName) {
        this.name = newName;
    }

    /**
     * Adds a player to the team.
     * Does nothing if the player is already on the team.
     *
     * @param bingoPlayer The BingoPlayer to add to the team
     * @return true if the player was added, false if they were already on the team
     * @since 1.0
     */
    boolean addPlayer(BingoPlayer bingoPlayer) {
        if (players.contains(bingoPlayer)) {
            return false;
        }
        return players.add(bingoPlayer);
    }

    /**
     * Removes a player from the team.
     *
     * @param bingoPlayer The bingo player to remove
     * @return true if the player was on the team and was removed, false otherwise
     * @since 1.0
     */
    boolean removePlayer(BingoPlayer bingoPlayer) {
        boolean b = players.remove(bingoPlayer);
        return b;
    }

    /**
     * Checks if a player is a member of this team.
     *
     * @param bingoPlayer The bingo player to check
     * @return true if the player is on this team, false otherwise
     * @since 1.0
     */
    public boolean hasPlayer(BingoPlayer bingoPlayer) {
        return players.contains(bingoPlayer);
    }

    /**
     * Gets the number of players on the team.
     *
     * @return The size of the team
     * @since 1.0
     */
    public int getSize() {
        return players.size();
    }

    /**
     * Gets the color assigned to this team.
     *
     * Team colors are automatically assigned when the team is created
     * and are used for visual identification in the game.
     *
     * @return The team's assigned color
     * @since 1.0
     */
    public NamedTextColor getColor() {
        return color;
    }

    /**
     * Gets an unmodifiable list of all players on this team.
     * This prevents external code from modifying the team's player list directly.
     *
     * @return An unmodifiable list of BingoPlayer objects
     * @since 1.0
     */
    public List<BingoPlayer> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    /**
     * Adds a found item to the team's collection.
     *
     * This method tracks which bingo items the team has successfully found.
     * Duplicate items are automatically handled by the underlying Set implementation.
     *
     * @param item The Material that was found by the team
     * @since 1.0
     */
    void addFoundItem(Material item) {
        foundItems.add(item);
    }

    /**
     * Gets the set of items that this team has found.
     *
     * This returns the actual Set, allowing external code to check
     * which items have been found by the team.
     *
     * @return A Set containing all Materials found by this team
     * @since 1.0
     */
    public Set<Material> getFoundItems() {
        return foundItems;
    }

    /**
     * Clears all found items from the team's collection.
     *
     * This method is typically used when starting a new game
     * or resetting the team's progress.
     *
     * @since 1.0
     */
    void clearFoundItems() {
        foundItems.clear();
    }

    /**
     * Gets a list of all team members who are currently online.
     *
     * @return A list of online {@link Player} objects
     * @since 1.0
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
     * @param message The message to send to all online team members
     * @since 1.0
     */
    public void broadcastMessage(String message) {
        for (Player player : getOnlinePlayers()) {
            player.sendMessage(message);
        }
    }
}
