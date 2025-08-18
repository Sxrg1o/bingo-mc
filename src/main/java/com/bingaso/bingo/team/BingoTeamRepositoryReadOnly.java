package com.bingaso.bingo.team;

import java.util.List;

import com.bingaso.bingo.player.BingoPlayer;

import net.kyori.adventure.text.format.TextColor;

public interface BingoTeamRepositoryReadOnly {

    /**
     * Find all {@link BingoTeam}s in the repository.
     * 
     * @return An immutable list of all {@link BingoTeam}s.
     * @since 1.0
     */
    public List<BingoTeam> findAll();

    /**
     * Finds a {@link BingoTeam} by its name.
     * 
     * @param name The name of the {@link BingoTeam}.
     * @return The {@link BingoTeam} with the given name, or null if not found.
     * @since 1.0
     */
    public BingoTeam findByName(String name);

    /**
     * Checks if a team with the specified name exists in the repository.
     * 
     * @param name The name to check.
     * @return true if a team with this name exists, false otherwise.
     * @since 1.0
     */
    public boolean existsByName(String name);

    /**
     * Checks if a team name is available (not already taken).
     * 
     * @param name The name to check.
     * @return True if the name is available, false if it's already taken.
     * @since 1.0
     */
    public boolean isTeamNameAvailable(String name);

    /**
     * Finds a {@link BingoTeam} by its color.
     * 
     * @param color The color of the {@link BingoTeam}.
     * @return The {@link BingoTeam} with the given color, or null if not found.
     * @since 1.0
     */
    public BingoTeam findByColor(TextColor color);

    /**
     * Checks if a team with the specified color exists in the repository.
     * 
     * @param color The color to check.
     * @return True if a team with this color exists, false otherwise.
     * @since 1.0
     */
    public boolean existsByColor(TextColor color);

    /**
     * Checks if a team color is available (not already taken).
     * 
     * @param color The color to check.
     * @return True if the color is available, false if it's already taken.
     * @since 1.0
     */
    public boolean isTeamColorAvailable(TextColor color);

    /**
     * Finds the {@link BingoTeam} that a {@link BingoPlayer} is currently
     * assigned to.
     * 
     * @param bingoPlayer The {@link BingoPlayer} to look up
     * @return The {@link BingoTeam} the {@link BingoPlayer} is on, or null if
     * they're not on any team.
     * @since 1.0
     */
    public BingoTeam findTeamByPlayer(BingoPlayer bingoPlayer);

    /**
     * Checks if a {@link BingoPlayer} is currently assigned to any team.
     * 
     * @param bingoPlayer The {@link BingoPlayer} to check
     * @return true if the player is assigned to a team, false otherwise.
     * @since 1.0
     */
    public boolean isPlayerAssigned(BingoPlayer bingoPlayer);

    /**
     * Gets the total number of teams in the repository.
     * 
     * @return The number of teams currently stored.
     * @since 1.0
     */
    public int getTeamCount();

    /**
     * Gets the total number of players assigned to teams.
     * 
     * @return The number of players currently assigned to any team.
     * @since 1.0
     */
    public int getAssignedPlayerCount();

    /**
     * Checks if there are no {@link BingoTeam}s currently stored.
     * 
     * @return True if no teams exist, false otherwise.
     * @since 1.0
     */
    public boolean isEmpty();

    public BingoTeam getPreviousTeam(BingoTeam bingoTeam);

    public BingoTeam getNextTeam(BingoTeam bingoTeam);
}
