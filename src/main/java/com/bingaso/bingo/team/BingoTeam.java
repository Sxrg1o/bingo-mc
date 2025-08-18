package com.bingaso.bingo.team;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import com.bingaso.bingo.quest.BingoQuest;
import com.bingaso.bingo.player.BingoPlayer;

import net.kyori.adventure.text.format.TextColor;

/**
 * Represents a team of players in the Bingo game.
 * 
 * This class manages a group of players, their completed items, team color,
 * and provides functionality for team communication and item tracking.
 * 
 * @since 1.0
 */
public class BingoTeam implements Serializable {

    private String name;
    private TextColor color;
    private final List<BingoPlayer> players = new ArrayList<>();
    private final HashMap<BingoQuest, Instant> completedItems = new HashMap<>();

    /**
     * Constructs a new Team with a given name and given color.
     * 
     * @param name The name of the team.
     * @param color The color of the team.
     * @since 1.0
     */
    public BingoTeam(@NotNull String name, @NotNull TextColor color) {
        this.name = name;
        this.color = color;
    }

    /**
     * @return The team's {@link TextColor}.
     * @since 1.0
     */
    public TextColor getColor() {
        return color;
    }

    /**
     * @param color New team's {@link TextColor}.
     * @since 1.0
     */
    protected void setColor(@NotNull TextColor color) {
        this.color = color;
    }

    /**
     * @return The team's name.
     * @since 1.0
     */
    public String getName() {
        return name;
    }

    /**
     * @param newName The new name of the team.
     * @since 1.0
     */
    protected void setName(@NotNull String newName) {
        this.name = newName;
    }

    /**
     * @return A {@link BingoPlayer} unmodifiable list of the team.
     * @since 1.0
     */
    public List<BingoPlayer> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    /** 
     * @return An online {@link Player} list of the team.
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
     * @return The size of the team.
     * @since 1.0
     */
    public int getSize() {
        return players.size();
    }

    /**
     * @return True if the team has no players, false otherwise.
     * @since 1.0
     */
    public boolean isEmpty() {
        return players.isEmpty();
    }

    /**
     * @param bingoPlayer The {@link BingoPlayer} to check.
     * @return True if the player is on this team, false otherwise.
     * @since 1.0
     */
    public boolean hasPlayer(@NotNull BingoPlayer bingoPlayer) {
        return players.contains(bingoPlayer);
    }

    /**
     * @param bingoPlayer The {@link BingoPlayer} to add to the team.
     * @return True if the player was added, false if they were already on the team.
     * @since 1.0
     */
    protected boolean addPlayer(@NotNull BingoPlayer bingoPlayer) {
        if (players.contains(bingoPlayer)) {
            return false;
        }
        return players.add(bingoPlayer);
    }

    /**
     * @param bingoPlayer The {@link BingoPlayer} to remove.
     * @return True if the player was on the team and was removed, false otherwise.
     * @since 1.0
     */
    protected boolean removePlayer(@NotNull BingoPlayer bingoPlayer) {
        boolean b = players.remove(bingoPlayer);
        return b;
    }

    /**
     * @return The unmodifiable Map of {@link BingoQuest}s with the
     * {@link Instant}s in which they were completed.
     */
    public Map<BingoQuest, Instant> getCompletedQuests() {
        return Collections.unmodifiableMap(completedItems);
    }

    /**
     * @param bingoQuest The {@link BingoQuest} to add.
     * @param instant The {@link Instant} in which the team completed the
     * {@link BingoQuest}
     * @since 1.0
     */
    protected void addCompletedQuest(
        @NotNull BingoQuest bingoQuest,
        @NotNull Instant instant) {
        completedItems.put(bingoQuest, instant);
    }

    /**
     * @param bingoQuest The {@link BingoQuest} to remove.
     */
    protected void removeCompletedQuest(@NotNull BingoQuest bingoQuest) {
        completedItems.remove(bingoQuest);
    }

    /**
     * @param bingoQuest The {@link BingoQuest} to check.
     * @return True if the {@link BingoQuest} has been completed by the team,
     * false otherwise.
     */
    public boolean hasCompletedQuest(@NotNull BingoQuest bingoQuest) {
        return completedItems.containsKey(bingoQuest);
    }

    /**
     * @param bingoQuest The {@link BingoQuest} to get the {@link Instant} of
     * completion.
     * @return The {@link Instant} in which the team completed the
     * {@link BingoQuest}
     */
    public Instant getCompletionInstant(@NotNull BingoQuest bingoQuest) {
        return completedItems.get(bingoQuest);
    }

    /**
     * Clears all completed {@link BingoQuest} from the team's collection.
     * 
     * @since 1.0
     */
    protected void clearCompletedQuests() {
        completedItems.clear();
    }
}
