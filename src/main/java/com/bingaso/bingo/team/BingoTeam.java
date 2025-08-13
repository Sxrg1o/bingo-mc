package com.bingaso.bingo.team;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;

import com.bingaso.bingo.card.quest.BingoQuest;
import com.bingaso.bingo.player.BingoPlayer;

import net.kyori.adventure.text.format.NamedTextColor;

/**
 * Represents a team of players in the Bingo game.
 * 
 * This class manages a group of players, their completed items, team color,
 * and provides functionality for team communication and item tracking.
 * Teams are automatically assigned colors from a predefined set when created.
 * 
 * @since 1.0
 */
public class BingoTeam {

    private final List<BingoPlayer> players = new ArrayList<>();
    private String name;
    private final HashMap<BingoQuest, Instant> completedItems = new HashMap<>();
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
    BingoTeam(String name) {
        this.color = TEAM_COLORS.get(nextColorIdx);
        nextColorIdx = (nextColorIdx + 1) % TEAM_COLORS.size();
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
     * Adds a bingo quest to the team's completed collection.
     * 
     * This method tracks which bingo quests the team has successfully completed.
     * Duplicate quests are automatically handled by the underlying Map implementation.
     *
     * @param bingoQuest The BingoQuest that was completed by the team
     * @param instant The Instant in which the team completed the quest
     * @since 1.0
     */
    void addCompletedItem(BingoQuest bingoQuest, Instant instant) {
        completedItems.put(bingoQuest, instant);
    }

    /**
     * Gets the set of quests that this team has completed.
     * 
     * This returns the actual Set, allowing external code to check
     * which quests have been completed by the team.
     *
     * @return A Set containing all BingoQuests completed by this team
     * @since 1.0
     */
    public Set<BingoQuest> getCompletedItems() {
        return Set.copyOf(completedItems.keySet());
    }

    /**
     * Gets completion instant
     * @param bingoQuest The BingoQuest to check
     * @return The Instant when the quest was completed, or null if not completed
     */
    public Instant getCompletionInstant(BingoQuest bingoQuest) {
        return completedItems.get(bingoQuest);
    }

    /**
     * Checks if the team has completed a BingoQuest
     * 
     * @param bingoQuest The BingoQuest to check
     * @return true if the team has completed the quest, false otherwise
     * @since 1.0
     */
    public boolean hasCompletedItem(BingoQuest bingoQuest) {
        return completedItems.containsKey(bingoQuest);
    }

    /**
     * Clears all completed items from the team's collection.
     * 
     * This method is typically used when starting a new game
     * or resetting the team's progress.
     *
     * @since 1.0
     */
    void clearCompletedItems() {
        completedItems.clear();
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

    private static int nextColorIdx = 0;
    private static final List<NamedTextColor> TEAM_COLORS = Arrays.asList(
        NamedTextColor.BLUE,
        NamedTextColor.GREEN,
        NamedTextColor.RED,
        NamedTextColor.YELLOW
    );
}
