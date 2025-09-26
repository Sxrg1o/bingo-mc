package com.bingaso.bingo.match.managers;

import com.bingaso.bingo.player.BingoPlayer;
import com.bingaso.bingo.team.BingoTeam;
import com.bingaso.bingo.team.BingoTeamColorGenerator;
import com.bingaso.bingo.team.BingoTeamRepository;
import com.bingaso.bingo.team.BingoTeamRepository.ColorAlreadyExistsException;
import com.bingaso.bingo.team.BingoTeamRepository.TeamNameAlreadyExistsException;
import com.bingaso.bingo.team.BingoTeamRepositoryInMemory;
import net.kyori.adventure.text.format.TextColor;

/**
 * Manages teams in a Bingo match.
 * <p>
 * This class handles team creation, player assignment to teams, and team removal.
 * It enforces team size limits and maintains the team repository.
 * </p>
 *
 * @since 1.0
 */
public class TeamManager {

    /**
     * Thrown when an attempt is made to add a player to a team that is already full.
     *
     * @since 1.0
     */
    public static class MaxPlayersException extends Exception {

        /**
         * Constructs a new MaxPlayersException with a default message.
         *
         * @param maxTeamSize The maximum size of the team.
         */
        public MaxPlayersException(int maxTeamSize) {
            super("Team has already reached " + maxTeamSize + " players.");
        }
    }

    /** Repository for storing and retrieving team data */
    private final BingoTeamRepository teamRepository =
        new BingoTeamRepositoryInMemory();
    /** Maximum number of players allowed per team */
    private final int maxTeamSize;

    /**
     * Creates a new team manager with the specified maximum team size.
     *
     * @param maxTeamSize The maximum number of players allowed in a team
     */
    public TeamManager(int maxTeamSize) {
        this.maxTeamSize = maxTeamSize;
    }

    /**
     * Creates a new bingo team with the specified name.
     * <p>
     * Automatically generates a unique color for the team.
     * </p>
     *
     * @param name The name for the new team
     * @return The newly created team
     * @throws TeamNameAlreadyExistsException If a team with the same name already exists
     * @throws IllegalStateException If unable to generate a unique team color
     */
    public BingoTeam createBingoTeam(String name)
        throws TeamNameAlreadyExistsException {
        BingoTeamColorGenerator colorGenerator = new BingoTeamColorGenerator(
            teamRepository
        );
        TextColor textColor = colorGenerator.generateRandomColor();
        BingoTeam bingoTeam = new BingoTeam(name, textColor);

        try {
            teamRepository.save(bingoTeam);
            return bingoTeam;
        } catch (ColorAlreadyExistsException e) {
            throw new IllegalStateException(
                "Failed to generate a unique team color.",
                e
            );
        }
    }

    /**
     * Adds a player to a bingo team.
     * <p>
     * If the player is already in another team, they will be removed from that team first.
     * Checks the maximum team size before adding the player.
     * </p>
     *
     * @param bingoPlayer The player to add to the team
     * @param team The team to add the player to
     * @throws MaxPlayersException If the team has reached its maximum capacity
     */
    public void addPlayerToBingoTeam(BingoPlayer bingoPlayer, BingoTeam team)
        throws MaxPlayersException {
        if (team.getSize() >= maxTeamSize) {
            throw new MaxPlayersException(maxTeamSize);
        }

        BingoTeam oldTeam = teamRepository.findTeamByPlayer(bingoPlayer);
        if (oldTeam != null) {
            removePlayerFromBingoTeam(bingoPlayer);
        }

        teamRepository.assignPlayerToTeam(bingoPlayer, team);
    }

    /**
     * Removes a player from their team.
     * <p>
     * If the team becomes empty after removing the player, the team is also removed.
     * </p>
     *
     * @param bingoPlayer The player to remove from their team
     */
    public void removePlayerFromBingoTeam(BingoPlayer bingoPlayer) {
        BingoTeam team = teamRepository.removePlayerFromTeam(bingoPlayer);
        if (team != null && team.getSize() == 0) {
            teamRepository.remove(team);
        }
    }

    /**
     * Gets the team that a player belongs to.
     *
     * @param bingoPlayer The player to look up
     * @return The player's team, or null if they are not in a team
     */
    public BingoTeam getTeamByPlayer(BingoPlayer bingoPlayer) {
        return teamRepository.findTeamByPlayer(bingoPlayer);
    }

    /**
     * Gets the team repository used by this manager.
     *
     * @return The team repository
     */
    public BingoTeamRepository getTeamRepository() {
        return teamRepository;
    }

    /**
     * Clears all teams from the repository.
     * <p>
     * This is typically called at the end of a match.
     * </p>
     */
    public void clear() {
        teamRepository.clear();
    }
}
