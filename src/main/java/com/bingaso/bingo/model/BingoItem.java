package com.bingaso.bingo.model;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Material;

/**
 * Represents an item on a Bingo card that players need to find during a game.
 * Each BingoItem tracks which teams have found it and when they found it.
 */
public class BingoItem {

    private final Material material;
    private final Set<BingoTeam> completedByTeams = new HashSet<>();
    private final HashMap<BingoTeam, Long> completionMilliseconds = new HashMap<>();

    /**
     * Creates a new BingoItem for the specified material.
     *
     * @param material The Minecraft material that this item represents
     */
    public BingoItem(Material material) {
        this.material = material;
    }

    /**
     * Gets the material that this item represents.
     *
     * @return The Minecraft material of this item
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * Marks this item as completed by the specified team and records the completion timestamp.
     *
     * @param team The team that has found this item
     * @param startInstant The instant when the bingo match started
     */
    public void addCompletingTeam(BingoTeam team, Instant startInstant) {
        this.completedByTeams.add(team);
        Long milli = Instant.now().toEpochMilli() - startInstant.toEpochMilli();
        this.completionMilliseconds.put(team, milli);
    }

    /**
     * Returns the completion milliseconds for the specified team.
     * @param team The team to get the completion milliseconds for
     * @return The completion milliseconds for the team, or null if not found
     */
    public Long getCompletionMilliseconds(BingoTeam team) {
        return this.completionMilliseconds.get(team);
    }

    /**
     * Checks if this item has been found by the specified team.
     *
     * @param team The team to check
     * @return true if the team has found this item, false otherwise
     */
    public boolean isCompletedBy(BingoTeam team) {
        return this.completedByTeams.contains(team);
    }

    /**
     * Checks if this item has been found by any team.
     *
     * @return true if at least one team has found this item, false otherwise
     */
    public boolean isCompletedByAnyTeam() {
        return !this.completedByTeams.isEmpty();
    }

    /**
     * Gets all teams that have found this item.
     *
     * @return A set of teams that have found this item
     */
    public Set<BingoTeam> getCompletedByTeams() {
        return completedByTeams;
    }
}
