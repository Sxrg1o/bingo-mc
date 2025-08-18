package com.bingaso.bingo.team;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import com.bingaso.bingo.quest.BingoQuest;

/**
 * Service for managing quest completion for teams.
 * 
 * This service handles the business logic around quest completion,
 * validation, and provides quest-related operations for teams.
 * It serves as a bridge between quest management and team management.
 * 
 * @since 1.0
 */
public class TeamQuestService {

    /**
     * Thrown when attempting to complete a quest that has already been completed.
     * 
     * @since 1.0
     */
    public static class QuestAlreadyCompletedException extends Exception {
        /**
         * Constructs a new QuestAlreadyCompletedException with the specified message.
         * 
         * @param message The detail message explaining the exception
         */
        public QuestAlreadyCompletedException(String message) {
            super(message);
        }
    }

    /**
     * Thrown when attempting to operate on a quest that doesn't exist or isn't valid.
     * 
     * @since 1.0
     */
    public static class InvalidQuestException extends Exception {
        /**
         * Constructs a new InvalidQuestException with the specified message.
         * 
         * @param message The detail message explaining the exception
         */
        public InvalidQuestException(String message) {
            super(message);
        }
    }

    private final BingoTeamRepository teamRepository;

    /**
     * Constructs a new TeamQuestService with the specified team repository.
     * 
     * @param teamRepository The repository to use for team data access
     * @since 1.0
     */
    public TeamQuestService(BingoTeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    /**
     * Marks a quest as completed by a team with the current timestamp.
     * 
     * @param team The {@link BingoTeam} that completed the quest
     * @param quest The {@link BingoQuest} that was completed
     * @throws QuestAlreadyCompletedException If the team has already completed this quest
     * @since 1.0
     */
    public void completeQuest(BingoTeam team, BingoQuest quest) throws QuestAlreadyCompletedException {
        completeQuest(team, quest, Instant.now());
    }

    /**
     * Marks a quest as completed by a team with a specific timestamp.
     * 
     * @param team The {@link BingoTeam} that completed the quest
     * @param quest The {@link BingoQuest} that was completed
     * @param completionTime The {@link Instant} when the quest was completed
     * @throws QuestAlreadyCompletedException If the team has already completed this quest
     * @since 1.0
     */
    public void completeQuest(BingoTeam team, BingoQuest quest, Instant completionTime) 
            throws QuestAlreadyCompletedException {
        if (team == null || quest == null) {
            throw new IllegalArgumentException("Team and quest cannot be null");
        }

        if (team.hasCompletedQuest(quest)) {
            throw new QuestAlreadyCompletedException(
                "Team '" + team.getName() + "' has already completed quest: " + quest.getQuestName()
            );
        }

        team.addCompletedQuest(quest, completionTime);
    }

    /**
     * Removes a completed quest from a team (for administrative purposes).
     * 
     * @param team The {@link BingoTeam} to remove the quest from
     * @param quest The {@link BingoQuest} to remove
     * @return true if the quest was removed, false if it wasn't completed by the team
     * @since 1.0
     */
    public boolean removeQuestCompletion(BingoTeam team, BingoQuest quest) {
        if (team == null || quest == null) {
            return false;
        }

        if (!team.hasCompletedQuest(quest)) {
            return false;
        }

        team.removeCompletedQuest(quest);
        return true;
    }

    /**
     * Clears all completed quests for a team.
     * 
     * @param team The {@link BingoTeam} to clear quests for
     * @since 1.0
     */
    public void clearAllQuests(BingoTeam team) {
        if (team != null) {
            team.clearCompletedQuests();
        }
    }

    /**
     * Gets all completed quests for a team.
     * 
     * @param team The {@link BingoTeam} to get quests for
     * @return An immutable map of completed quests and their completion times
     * @since 1.0
     */
    public Map<BingoQuest, Instant> getCompletedQuests(BingoTeam team) {
        if (team == null) {
            return Map.of();
        }
        return team.getCompletedQuests();
    }

    /**
     * Checks if a team has completed a specific quest.
     * 
     * @param team The {@link BingoTeam} to check
     * @param quest The {@link BingoQuest} to check for completion
     * @return true if the team has completed the quest, false otherwise
     * @since 1.0
     */
    public boolean hasCompletedQuest(BingoTeam team, BingoQuest quest) {
        if (team == null || quest == null) {
            return false;
        }
        return team.hasCompletedQuest(quest);
    }

    /**
     * Gets the completion time for a specific quest.
     * 
     * @param team The {@link BingoTeam} to check
     * @param quest The {@link BingoQuest} to get the completion time for
     * @return The {@link Instant} when the quest was completed, or null if not completed
     * @since 1.0
     */
    public Instant getQuestCompletionTime(BingoTeam team, BingoQuest quest) {
        if (team == null || quest == null) {
            return null;
        }
        return team.getCompletionInstant(quest);
    }

    /**
     * Gets the number of quests completed by a team.
     * 
     * @param team The {@link BingoTeam} to count quests for
     * @return The number of completed quests
     * @since 1.0
     */
    public int getCompletedQuestCount(BingoTeam team) {
        if (team == null) {
            return 0;
        }
        return team.getCompletedQuests().size();
    }

    /**
     * Checks if any team has completed the specified quest.
     * 
     * @param quest The {@link BingoQuest} to check
     * @return true if any team has completed the quest, false otherwise
     * @since 1.0
     */
    public boolean isQuestCompletedByAnyTeam(BingoQuest quest) {
        if (quest == null) {
            return false;
        }

        List<BingoTeam> teams = teamRepository.findAll();
        return teams.stream()
            .anyMatch(team -> team.hasCompletedQuest(quest));
    }

    /**
     * Gets all teams that have completed a specific quest.
     * 
     * @param quest The {@link BingoQuest} to check
     * @return A list of teams that have completed the quest
     * @since 1.0
     */
    public List<BingoTeam> getTeamsWithCompletedQuest(BingoQuest quest) {
        if (quest == null) {
            return List.of();
        }

        List<BingoTeam> teams = teamRepository.findAll();
        return teams.stream()
            .filter(team -> team.hasCompletedQuest(quest))
            .toList();
    }

    /**
     * Gets the team with the most completed quests.
     * 
     * @return The {@link BingoTeam} with the highest quest count, or null if no teams exist
     * @since 1.0
     */
    public BingoTeam getTeamWithMostQuests() {
        List<BingoTeam> teams = teamRepository.findAll();
        return teams.stream()
            .max((team1, team2) -> Integer.compare(
                team1.getCompletedQuests().size(),
                team2.getCompletedQuests().size()
            ))
            .orElse(null);
    }

    /**
     * Gets all teams sorted by their quest completion count in descending order.
     * 
     * @return A list of teams ordered by quest completion count
     * @since 1.0
     */
    public List<BingoTeam> getTeamsByQuestCount() {
        List<BingoTeam> teams = teamRepository.findAll();
        return teams.stream()
            .sorted((team1, team2) -> Integer.compare(
                team2.getCompletedQuests().size(), // Descending order
                team1.getCompletedQuests().size()
            ))
            .toList();
    }

    /**
     * Validates if a quest completion is allowed based on game rules.
     * This can be extended with game mode specific validation.
     * 
     * @param team The {@link BingoTeam} attempting to complete the quest
     * @param quest The {@link BingoQuest} being completed
     * @return true if the completion is valid, false otherwise
     * @since 1.0
     */
    public boolean isQuestCompletionValid(BingoTeam team, BingoQuest quest) {
        if (team == null || quest == null) {
            return false;
        }

        // Basic validation - quest not already completed
        if (team.hasCompletedQuest(quest)) {
            return false;
        }

        // Additional validation can be added here based on game mode:
        // - LOCKED mode: Check if quest is already completed by another team
        // - Team size validation
        // - Time limits, etc.

        return true;
    }
}
