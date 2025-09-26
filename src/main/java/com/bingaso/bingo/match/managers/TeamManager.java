package com.bingaso.bingo.match.managers;

import com.bingaso.bingo.player.BingoPlayer;
import com.bingaso.bingo.team.BingoTeam;
import com.bingaso.bingo.team.BingoTeamColorGenerator;
import com.bingaso.bingo.team.BingoTeamRepository;
import com.bingaso.bingo.team.BingoTeamRepository.ColorAlreadyExistsException;
import com.bingaso.bingo.team.BingoTeamRepository.TeamNameAlreadyExistsException;
import com.bingaso.bingo.team.BingoTeamRepositoryInMemory;
import net.kyori.adventure.text.format.TextColor;

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

    private final BingoTeamRepository teamRepository =
        new BingoTeamRepositoryInMemory();
    private final int maxTeamSize;

    public TeamManager(int maxTeamSize) {
        this.maxTeamSize = maxTeamSize;
    }

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

    public void removePlayerFromBingoTeam(BingoPlayer bingoPlayer) {
        BingoTeam team = teamRepository.removePlayerFromTeam(bingoPlayer);
        if (team != null && team.getSize() == 0) {
            teamRepository.remove(team);
        }
    }

    public BingoTeam getTeamByPlayer(BingoPlayer bingoPlayer) {
        return teamRepository.findTeamByPlayer(bingoPlayer);
    }

    public BingoTeamRepository getTeamRepository() {
        return teamRepository;
    }

    public void clear() {
        teamRepository.clear();
    }
}
