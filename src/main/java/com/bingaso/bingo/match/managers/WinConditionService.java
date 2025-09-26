package com.bingaso.bingo.match.managers;

import com.bingaso.bingo.card.BingoCard;
import com.bingaso.bingo.match.BingoMatchSettings;
import com.bingaso.bingo.team.BingoTeam;
import com.bingaso.bingo.team.BingoTeamRepositoryReadOnly;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WinConditionService {

    private final BingoMatchSettings settings;
    private final BingoCard bingoCard;

    public WinConditionService(
        BingoMatchSettings settings,
        BingoCard bingoCard
    ) {
        this.settings = settings;
        this.bingoCard = bingoCard;
    }

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
