package com.bingaso.bingo.match;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import com.bingaso.bingo.BingoPlugin;
import com.bingaso.bingo.card.BingoCard;
import com.bingaso.bingo.card.BingoCardGenerator;
import com.bingaso.bingo.card.BingoCardGui;
import com.bingaso.bingo.player.BingoPlayerRepositoryInMemory;
import com.bingaso.bingo.player.BingoPlayerRepositoryReadOnly;
import com.bingaso.bingo.player.BingoPlayerRepository.PlayerAlreadyExistsException;
import com.bingaso.bingo.quest.BingoQuest;
import com.bingaso.bingo.player.BingoPlayer;
import com.bingaso.bingo.player.BingoPlayerRepository;
import com.bingaso.bingo.scoreboard.BingoGlobalScoreboard;
import com.bingaso.bingo.team.BingoTeam;
import com.bingaso.bingo.team.BingoTeamColorGenerator;
import com.bingaso.bingo.team.BingoTeamRepositoryInMemory;
import com.bingaso.bingo.team.BingoTeamRepositoryReadOnly;
import com.bingaso.bingo.team.TeamQuestService;
import com.bingaso.bingo.team.TeamQuestService.QuestAlreadyCompletedException;
import com.bingaso.bingo.team.BingoTeamRepository;
import com.bingaso.bingo.team.BingoTeamRepository.ColorAlreadyExistsException;
import com.bingaso.bingo.team.BingoTeamRepository.TeamNameAlreadyExistsException;
import com.bingaso.bingo.utils.Broadcaster;

import net.kyori.adventure.text.format.TextColor;

public class BingoMatch {
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

    /**
     * Represents the possible states of a Bingo game.
     * The game transitions through these states during its lifecycle.
     */
    public static enum State {
        /**
         * Initial state where players can join teams and settings can be configured.
         * No active gameplay occurs in this state.
         */
        LOBBY,

        /**
         * Active gameplay state where players are finding items for their Bingo cards.
         * The game remains in this state until win conditions are met or time expires.
         */
        IN_PROGRESS,

        /**
         * Transitional state after a game has ended but before returning to the lobby.
         * Used for winner announcements and cleanup operations.
         */
        FINISHING,
    }

    /* Broadcaster */
    private final Broadcaster broadcaster = new Broadcaster();

    /* Players and Teams in this match */
    private final BingoTeamRepository bingoTeamRepository  = new BingoTeamRepositoryInMemory();
    private final BingoPlayerRepository bingoPlayerRepository = new BingoPlayerRepositoryInMemory();

    /* Settings of the match */
    private final BingoMatchSettings matchSettings = new BingoMatchSettings();

    /* Card with quests in this match */
    private BingoCard bingoCard;
    /* Current state of the match */
    private State state = State.LOBBY;
    /* Match instances */
    private Instant startInstant;
    private Instant endInstant;
    /* Active global scoreboard */
    private BingoGlobalScoreboard globalScoreboard;
    /* Winner teams */
    private final List<BingoTeam> winnerTeams = new ArrayList<>();
    /* Task that ends the game after a certain duration */
    private BukkitTask endGameTask;

    public BingoMatch() {
        generateNewBingoCard();
    }

    // Team and Player Repository 
    public BingoTeamRepositoryReadOnly getBingoTeamRepository() {
        return bingoTeamRepository;
    }

    public BingoPlayerRepositoryReadOnly getBingoPlayerRepository() {
        return bingoPlayerRepository;
    }

    public boolean removePlayer(Player player) {
        BingoPlayer bingoPlayer = bingoPlayerRepository.findByUUID(
            player.getUniqueId());
        if(bingoPlayer == null) return false;

        bingoPlayerRepository.remove(bingoPlayer);
        BingoTeam bingoTeam = bingoTeamRepository.removePlayerFromTeam(bingoPlayer);

        if(bingoTeam != null && bingoTeam.getSize() == 0) {
            bingoTeamRepository.remove(bingoTeam);
        }
        return true;
    }

    public boolean addPlayer(Player player) {
        BingoPlayer bingoPlayer = new BingoPlayer(player);
        try {
            bingoPlayerRepository.save(bingoPlayer);
            return true;
        } catch (PlayerAlreadyExistsException e) {
            return false;
        }
    }

    public BingoTeam getBingoTeamFromPlayer(Player player) {
        BingoPlayer bingoPlayer = bingoPlayerRepository.findByUUID(player.getUniqueId());
        if(bingoPlayer == null) return null;
        return bingoTeamRepository.findTeamByPlayer(bingoPlayer);
    }

    public boolean addPlayerToBingoTeam(
        Player player,
        BingoTeam bingoTeam
    ) throws MaxPlayersException {
        if(bingoTeam.getSize() >= matchSettings.getMaxTeamSize()) {
            throw new MaxPlayersException(matchSettings.getMaxTeamSize());
        }
        BingoPlayer bingoPlayer = bingoPlayerRepository.findByUUID(player.getUniqueId());
        if(bingoTeamRepository.findTeamByPlayer(bingoPlayer) != null) {
            removePlayerFromBingoTeam(player);
        }
        bingoTeamRepository.assignPlayerToTeam(bingoPlayer, bingoTeam);
        return true;
    }

    public boolean removePlayerFromBingoTeam(Player player) {
        BingoPlayer bingoPlayer = bingoPlayerRepository.findByUUID(
            player.getUniqueId());
        if(bingoPlayer == null) return false;

        BingoTeam bingoTeam = bingoTeamRepository.removePlayerFromTeam(bingoPlayer);

        if(bingoTeam != null && bingoTeam.getSize() == 0) {
            bingoTeamRepository.remove(bingoTeam);
        }
        return true;
    }

    public BingoTeam createBingoTeam(String name)
        throws TeamNameAlreadyExistsException {
        BingoTeamColorGenerator colorGenerator = new BingoTeamColorGenerator(bingoTeamRepository);
        TextColor textColor = colorGenerator.generateRandomColor();
        BingoTeam bingoTeam = new BingoTeam(name, textColor);
        
        try {
            bingoTeamRepository.save(bingoTeam);
            return bingoTeam;
        } catch (ColorAlreadyExistsException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    public BingoMatchSettings getMatchSettings() {
        return matchSettings;
    }

    public BingoCard getBingoCard() {
        return bingoCard;
    }

    public void generateNewBingoCard() {
        BingoCardGenerator cardGenerator = new BingoCardGenerator(
            matchSettings.getItemRepository(),
            matchSettings.getDifficultyLevel()
        );
        bingoCard = cardGenerator.generateCard();
    }

    public State getState() {
        return state;
    }

    /**
     * Starts a new Bingo match with the current settings.
     * Initializes teams, generates a Bingo card, and transitions to the IN_PROGRESS state.
     * For timed matches, schedules the end-of-match task.
     */
    public void start() {
        // Restart state
        winnerTeams.clear();
        //globalScoreboard.stop();
        globalScoreboard = new BingoMatchScoreboard(this);
        if (endGameTask != null) {
            endGameTask.cancel();
        }

        // Team management
        if (matchSettings.getTeamMode() != BingoMatchSettings.TeamMode.MANUAL) {
            // TODO: Random assignment
        }

        if (bingoTeamRepository.isEmpty()) {
            // TODO: Cancel match if no valid teams
            return;
        }

        // When teams are ready
        state = State.IN_PROGRESS;
        startInstant = Instant.now();

        if (matchSettings.getGameMode() == BingoMatchSettings.GameMode.TIMED) {
            long durationInTicks =
                matchSettings.getGameDuration() * 60 * 20L;
            endGameTask = BingoPlugin.getInstance()
                .getServer()
                .getScheduler()
                .runTaskLater(
                    BingoPlugin.getInstance(),
                    this::determineTimedWinners,
                    durationInTicks
                );
        }

        // TODO: Spreadplayers, kit? idk
        broadcaster.announceStart();
        globalScoreboard.start(20);
    }

    /**
     * Ends the current match with the specified winners.
     * Transitions the game to the FINISHING state and announces the results.
     * Stops the current match and resets the game state to LOBBY.
     * Cleans up resources, cancels scheduled tasks, and clears team data.
     *
     * @param winners The list of teams that won the match
     */
    private void end(List<BingoTeam> winners) {
        if (state != State.IN_PROGRESS) return;

        state = State.FINISHING;
        winnerTeams.clear();
        winnerTeams.addAll(winners);

        broadcaster.announceWinners(winners);

        // TODO: Celebration or smth like that idk

        // idk if we need smth more, xd tp to lobby
        if (endGameTask != null) {
            endGameTask.cancel();
            endGameTask = null;
        }
        globalScoreboard.stop();
        state = State.LOBBY;

        TeamQuestService questService = new TeamQuestService(bingoTeamRepository);
        for (BingoTeam team : bingoTeamRepository.findAll()) {
            questService.clearAllQuests(team);
        }
        bingoTeamRepository.clear();
        bingoPlayerRepository.clear();
        winnerTeams.clear();
    }

    public Instant getStartInstant() {
        return startInstant;
    }

    public void setStartInstant(Instant startInstant) {
        this.startInstant = startInstant;
    }

    public Instant getEndInstant() {
        return endInstant;
    }

    public void setEndInstant(Instant endInstant) {
        this.endInstant = endInstant;
    }

    public long getMatchDurationSeconds(@NotNull Instant instant) {
        if (startInstant == null) {
            return 0;
        }
        return instant.getEpochSecond() - startInstant.getEpochSecond();
    }

    public long getMatchDurationMilliseconds(@NotNull Instant instant) {
        if (startInstant == null) {
            return 0;
        }
        return instant.toEpochMilli() - startInstant.toEpochMilli();
    }

    public BingoGlobalScoreboard getGlobalScoreboard() {
        return globalScoreboard;
    }

    public List<BingoTeam> getWinnerTeams() {
        return new ArrayList<>(winnerTeams);
    }

    public void addWinnerTeam(BingoTeam team) {
        if (!winnerTeams.contains(team)) {
            winnerTeams.add(team);
        }
    }

    public void clearWinnerTeams() {
        winnerTeams.clear();
    }

    public boolean hasWinners() {
        return !winnerTeams.isEmpty();
    }

    /**
     * Handles when a player finds an item during the game.
     * This method checks if the item is part of the bingo card and awards it to the player's team.
     * 
     * @param player The player who found the item
     * @param item The material type of the found item
     */
    public void onPlayerFindsItem(BingoPlayer player, Material item) {
        if (state != State.IN_PROGRESS) return;

        BingoQuest bingoQuest = bingoCard.getItem(item);
        if (bingoQuest == null) return;

        BingoTeam team = bingoTeamRepository.findTeamByPlayer(player);
        if(team == null) return;

        if (team.hasCompletedQuest(bingoQuest)) return;

        TeamQuestService questService = new TeamQuestService(bingoTeamRepository);
        if (
            !(matchSettings.getGameMode() == BingoMatchSettings.GameMode.LOCKED &&
            questService.isQuestCompletedByAnyTeam(bingoQuest))
        ) {
            // Mark the quest as completed by the team
            try {
                questService.completeQuest(team, bingoQuest);
            } catch (QuestAlreadyCompletedException e) {
                player.getOnlinePlayer().sendMessage("§cItem already claimed!");
                return;
            }
        }

        broadcaster.announceItemFound(team, item);
        BingoCardGui.getInstance().updateInventories();
        checkWinConditions(team);
    }
    
    /**
     * Checks if the specified team has met win conditions based on the current game mode.
     * Different modes have different win conditions:
     * - LOCKED: First team to find a certain number of items
     * - STANDARD: Complete a row, column, or diagonal
     * - BLACKOUT: Find all 25 items
     * - TIMED: Most items found when time expires
     *
     * @param team The team to check for win conditions
     */
    private void checkWinConditions(BingoTeam team) {
        BingoMatchSettings.GameMode mode = matchSettings.getGameMode();
        switch (mode) {
            case LOCKED:
                int teamCount = bingoTeamRepository.findAll().size();
                if (teamCount == 0) return;

                int requiredItems = (int) Math.floor(25.0 / teamCount) + 1;
                int foundItems = team.getCompletedQuests().size();

                if (foundItems >= requiredItems) {
                    end(Collections.singletonList(team));
                }
                break;
            case STANDARD:
                if(bingoCard.isAnyRowCompletedByTeam(team) ||
                    bingoCard.isAnyColumnCompletedByTeam(team) ||
                    bingoCard.isAnyDiagonalCompletedByTeam(team)) {
                    end(Collections.singletonList(team));
                    break;
                }
                break;
            case BLACKOUT:
                int totalFoundItems = team.getCompletedQuests().size();
                if (totalFoundItems == bingoCard.getItems().size()) {
                    end(Collections.singletonList(team));
                }
                break;
            case TIMED:
                break;
        }
    }

    /**
     * Determines the winners for a timed match when the time expires.
     * Winners are teams with the highest number of found items.
     * Multiple teams can win if they have the same highest score.
     */
    private void determineTimedWinners() {
        if (state != State.IN_PROGRESS) return;

        List<BingoTeam> potentialWinners = new ArrayList<>();
        int highestScore = -1;

        for (BingoTeam team : bingoTeamRepository.findAll()) {
            int currentScore = team.getCompletedQuests().size();
            if (currentScore > highestScore) {
                highestScore = currentScore;
                potentialWinners.clear();
                potentialWinners.add(team);
            } else if (currentScore == highestScore) {
                potentialWinners.add(team);
            }
        }

        end(potentialWinners);
    }
}
