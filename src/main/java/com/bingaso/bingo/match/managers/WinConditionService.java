package com.bingaso.bingo.match.managers;

import com.bingaso.bingo.card.BingoCard;
import com.bingaso.bingo.match.BingoMatchSettings;
import com.bingaso.bingo.team.BingoTeam;
import com.bingaso.bingo.team.BingoTeamRepositoryReadOnly;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service responsible for checking win conditions in a Bingo match.
 * <p>
 * This class evaluates whether a team has met the criteria for winning based on the match settings
 * and game mode. It supports different win conditions for various game modes:
 * <ul>
 *   <li>STANDARD - Complete a row, column, or diagonal</li>
 *   <li>BLACKOUT - Complete all items on the card</li>
 *   <li>LOCKED - Complete a minimum number of items based on team count</li>
 *   <li>TIMED - Team with most items when time expires wins</li>
 * </ul>
 * </p>
 *
 * @since 1.0
 */
public class WinConditionService {

    /** Match settings used to determine game mode and other configuration */
    private final BingoMatchSettings settings;
    /** The bingo card with items to be found */
    private final BingoCard bingoCard;

    /**
     * Creates a new win condition service.
     *
     * @param settings The match settings used to determine game mode and other configuration
     * @param bingoCard The bingo card with items to be found
     */
    public WinConditionService(
        BingoMatchSettings settings,
        BingoCard bingoCard
    ) {
        this.settings = settings;
        this.bingoCard = bingoCard;
    }

    /**
     * Checks if a team has met the win conditions based on the current game mode.
     * <p>
     * This method evaluates different win criteria depending on the game mode:
     * <ul>
     *   <li>STANDARD - Complete a row, column, or diagonal</li>
     *   <li>BLACKOUT - Complete all items on the card</li>
     *   <li>LOCKED - Complete a minimum number of items based on team count</li>
     *   <li>TIMED - No immediate win condition check (determined by time expiry)</li>
     * </ul>
     * </p>
     *
     * @param team The team to check for win conditions
     * @param teamRepository The repository containing all teams in the match
     * @return A list containing the winning team, or an empty list if no winner yet
     */
    public List<BingoTeam> checkWinConditions(
        BingoTeam team,
        BingoTeamRepositoryReadOnly teamRepository
    ) {
        switch (settings.getGameMode()) {
            case STANDARD:
                if (
                    bingoCard.isAnyRowCompletedByTeam(team) ||
                    bingoCard.isAnyColumnCompletedByTeam(team) ||
                    bingoCard.isAnyDiagonalCompletedByTeam(team)
                ) {
                    return Collections.singletonList(team);
                }
                break;
            case BLACKOUT:
                if (
                    team.getCompletedQuests().size() ==
                    bingoCard.getItems().size()
                ) {
                    return Collections.singletonList(team);
                }
                break;
            case LOCKED:
                int teamCount = teamRepository.getTeamCount();
                if (teamCount == 0) return Collections.emptyList();
                int requiredItems = (int) Math.floor(25.0 / teamCount) + 1;
                if (team.getCompletedQuests().size() >= requiredItems) {
                    return Collections.singletonList(team);
                }
                break;
            case TIMED:
                break;
        }
        return Collections.emptyList();
    }

    /**
     * Determines winners for a timed game mode.
     * <p>
     * When time expires in a TIMED game mode, this method:
     * <ol>
     *   <li>Finds all teams with the highest number of completed quests</li>
     *   <li>If multiple teams have the same score, they all win (tie)</li>
     * </ol>
     * </p>
     *
     * @param teamRepository The repository containing all teams in the match
     * @return A list of winning teams (may be multiple in case of tie)
     */
    public List<BingoTeam> determineTimedWinners(
        BingoTeamRepositoryReadOnly teamRepository
    ) {
        List<BingoTeam> potentialWinners = new ArrayList<>();
        int highestScore = -1;

        for (BingoTeam team : teamRepository.findAll()) {
            int currentScore = team.getCompletedQuests().size();
            if (currentScore > highestScore) {
                highestScore = currentScore;
                potentialWinners.clear();
                potentialWinners.add(team);
            } else if (currentScore == highestScore) {
                potentialWinners.add(team);
            }
        }
        return potentialWinners;
    }
}
