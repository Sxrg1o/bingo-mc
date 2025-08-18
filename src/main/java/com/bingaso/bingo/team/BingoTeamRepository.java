package com.bingaso.bingo.team;

import com.bingaso.bingo.player.BingoPlayer;

import net.kyori.adventure.text.format.TextColor;

/**
 * Abstraction for storing and retrieving teams and their player assignments.
 * Implementations must maintain referential integrity between players and teams.
 */
public interface BingoTeamRepository extends BingoTeamRepositoryReadOnly {

    /**
     * Thrown when attempting to create a team with a name that already exists.
     * 
     * @since 1.0
     */
    public static class TeamNameAlreadyExistsException extends Exception {
        /** 
         * @param name The name that already exists.
         */
        public TeamNameAlreadyExistsException(String name) {
            super("A team with the name '" + name + "' already exists.");
        }
    }

    /**
     * Thrown when attempting to create a team with a color that already exists.
     * 
     * @since 1.0
     */
    public static class ColorAlreadyExistsException extends Exception {
        /** 
         * @param color The color that already exists.
         */
        public ColorAlreadyExistsException(TextColor color) {
            super("A team with the color '" + color.asHexString() + "' already exists.");
        }
    }

    /**
     * Saves a {@link BingoTeam} in the repository.
     * 
     * @param bingoTeam The {@link BingoTeam} to save.
     * @throws TeamNameAlreadyExistsException If a team with this name already  
     * exists.
     * @throws ColorAlreadyExistsException If a team with this color already
     * exists.
     * @since 1.0
     */
    public void save(BingoTeam bingoTeam)
        throws TeamNameAlreadyExistsException, ColorAlreadyExistsException;

    /**
     * Removes a {@link BingoTeam} from the repository.
     * 
     * @param bingoTeam The {@link BingoTeam} to remove.
     * @return True if the team was removed, false if it didn't exist in the
     * repository.
     * @since 1.0
     */
    public boolean remove(BingoTeam bingoTeam);

    /**
     * Changes the name of an existing {@link BingoTeam}.
     * 
     * @param bingoTeam The {@link BingoTeam} to rename.
     * @param newName The new name for the {@link BingoTeam}.
     * @throws TeamNameAlreadyExistsException If a {@link BingoTeam} with the
     * new name already exists
     * @since 1.0
     */
    public void changeTeamName(BingoTeam bingoTeam, String newName)
        throws TeamNameAlreadyExistsException;

    /**
     * Changes the color of an existing {@link BingoTeam}.
     * 
     * @param bingoTeam The {@link BingoTeam} to recolor.
     * @param newColor The new color for the {@link BingoTeam}.
     * @throws ColorAlreadyExistsException If a team with this color already
     * exists.
     * @since 1.0
     */
    public void changeTeamColor(BingoTeam bingoTeam, TextColor newColor)
        throws ColorAlreadyExistsException;

    /**
     * Assigns a {@link BingoPlayer} to a {@link BingoTeam}, automatically
     * removing them from their previous {@link BingoTeam} to maintain data
     * integrity.
     * 
     * @param bingoPlayer The {@link BingoPlayer} to assign
     * @param bingoTeam The {@link BingoTeam} to assign the {@link BingoPlayer}
     * to.
     * @since 1.0
     */
    public void assignPlayerToTeam(
        BingoPlayer bingoPlayer,
        BingoTeam bingoTeam
    );

    /**
     * Removes a {@link BingoPlayer} from their current {@link BingoTeam}.
     * 
     * @param bingoPlayer The {@link BingoPlayer} to remove from their current
     * {@link BingoTeam}.
     * @return The {@link BingoTeam} the player was removed from, or null if
     * they weren't on any team.
     * @since 1.0
     */
    public BingoTeam removePlayerFromTeam(BingoPlayer bingoPlayer);

    /**
     * Clears the repository.
     * 
     * @since 1.0
     */
    public void clear();
}
