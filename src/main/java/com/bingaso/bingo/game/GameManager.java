package com.bingaso.bingo.game;

import com.bingaso.bingo.BingoPlugin;
import com.bingaso.bingo.model.BingoCard;
import com.bingaso.bingo.model.BingoItem;
import com.bingaso.bingo.model.BingoPlayer;
import com.bingaso.bingo.model.BingoTeam;
import com.bingaso.bingo.model.DifficultyLevel;
import com.bingaso.bingo.model.GameMode;
import com.bingaso.bingo.model.TeamMode;
import com.bingaso.bingo.utils.Broadcaster;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class GameManager {

    private GameState currentState = GameState.LOBBY;
    private MatchSettings currentMatchSettings;
    private BingoCard sharedBingoCard;
    private long matchStartTime;

    private final CardGenerator cardGenerator;
    private final Broadcaster broadcaster;
    private final BingoScoreboard scoreboard;

    private final List<BingoTeam> teams = new ArrayList<>();
    private List<BingoTeam> winnerTeams = new ArrayList<>();
    private BukkitTask matchEndTask = null;

    public GameManager(CardGenerator cardGenerator, Broadcaster broadcaster) {
        this.currentMatchSettings = new MatchSettings();
        this.cardGenerator = cardGenerator;
        this.broadcaster = broadcaster;
        this.scoreboard = new BingoScoreboard(this);
    }

    public void updateSettings(MatchSettings matchSettings) {
        if (currentState == GameState.LOBBY) {
            this.currentMatchSettings = matchSettings;
        }
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public long getElapsedSeconds() {
        if (currentState != GameState.IN_PROGRESS) return 0;
        return (System.currentTimeMillis() - matchStartTime) / 1000;
    }

    public List<Player> getOnlinePlayersInMatch() {
        List<Player> players = new ArrayList<>();
        for (BingoTeam team : teams) {
            players.addAll(team.getOnlinePlayers());
        }
        return players;
    }

    public List<BingoTeam> getTeams() {
        return teams;
    }

    public void startMatch() {
        // Restart state
        this.teams.clear();
        this.winnerTeams.clear();
        if (this.matchEndTask != null) {
            this.matchEndTask.cancel();
        }

        // Generate card
        DifficultyLevel difficulty = currentMatchSettings.getDifficultyLevel();
        this.sharedBingoCard = cardGenerator.generateCard(difficulty);

        // Team management
        if (currentMatchSettings.getTeamMode() == TeamMode.MANUAL) {
            // TODO: Team GUI
        } else {
            // TODO: Random team assignment
        }

        for (BingoTeam team : BingoTeam.getAllTeams()) {
            if (team.getSize() > 0) {
                this.teams.add(team);
            }
        }

        if (teams.isEmpty()) {
            // TODO: Cancel match if no valid teams
            return;
        }

        // When teams are ready
        this.currentState = GameState.IN_PROGRESS;
        this.matchStartTime = System.currentTimeMillis();

        if (currentMatchSettings.getGameMode() == GameMode.TIMED) {
            long durationInTicks =
                currentMatchSettings.getGameDuration() * 60 * 20L;
            this.matchEndTask = BingoPlugin.getInstance()
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
        this.scoreboard.start();
    }

    public void stopMatch() {
        // idk if we need smth more, xd tp to lobby
        if (this.matchEndTask != null) {
            this.matchEndTask.cancel();
            this.matchEndTask = null;
        }
        this.scoreboard.stop();
        this.currentState = GameState.LOBBY;
        this.teams.clear();
        this.winnerTeams.clear();
    }

    private void endMatch(List<BingoTeam> winners) {
        if (currentState != GameState.IN_PROGRESS) return;

        this.currentState = GameState.FINISHING;
        this.winnerTeams = winners;

        broadcaster.announceWinners(winners);

        // TODO: Celebration or smth like that idk

        stopMatch();
    }

    public void onPlayerFindsItem(BingoPlayer player, Material item) {
        if (currentState != GameState.IN_PROGRESS) return;

        BingoItem bingoItem = sharedBingoCard.getItem(item);
        if (bingoItem == null) return;

        BingoTeam team = player.getTeam();

        if (!teams.contains(team)) return;

        if (
            currentMatchSettings.getGameMode() == GameMode.LOCKED &&
            bingoItem.isCompletedByAnyTeam()
        ) {
            if (!bingoItem.isCompletedBy(team)) {
                player.getPlayer().sendMessage("§cItem already claimed!");
                return;
            }
        }

        bingoItem.addCompletingTeam(team);
        team.addFoundItem(item);

        broadcaster.announceItemFound(team, item);

        checkWinConditions(team);
    }

    private void checkWinConditions(BingoTeam team) {
        GameMode mode = currentMatchSettings.getGameMode();
        switch (mode) {
            case LOCKED:
                int teamCount = teams.size();
                if (teamCount == 0) return;

                int requiredItems = (int) Math.floor(25.0 / teamCount) + 1;
                int foundItems = team.getFoundItems().size();

                if (foundItems >= requiredItems) {
                    endMatch(Collections.singletonList(team));
                }
                break;
            case STANDARD:
                // Rows
                BingoItem[][] items = sharedBingoCard.getItems();
                for (int i = 0; i < 5; i++) {
                    boolean rowComplete = true;
                    for (int j = 0; j < 5; j++) {
                        if (!items[i][j].isCompletedBy(team)) {
                            rowComplete = false;
                            break;
                        }
                    }
                    if (rowComplete) {
                        endMatch(Collections.singletonList(team));
                    }
                }
                // Columns
                for (int i = 0; i < 5; i++) {
                    boolean columnComplete = true;
                    for (int j = 0; j < 5; j++) {
                        if (!items[j][i].isCompletedBy(team)) {
                            columnComplete = false;
                            break;
                        }
                    }
                    if (columnComplete) {
                        endMatch(Collections.singletonList(team));
                    }
                }
                // Diagonal
                boolean diagonalComplete = true;
                for (int i = 0; i < 5; i++) {
                    if (
                        !items[i][i].isCompletedBy(team) ||
                        !items[i][4 - i].isCompletedBy(team)
                    ) {
                        diagonalComplete = false;
                        break;
                    }
                }
                if (diagonalComplete) {
                    endMatch(Collections.singletonList(team));
                }
                break;
            case BLACKOUT:
                int totalFoundItems = team.getFoundItems().size();
                if (totalFoundItems == 25) {
                    endMatch(Collections.singletonList(team));
                }
                break;
            case TIMED:
                break;
        }
    }

    private void determineTimedWinners() {
        if (currentState != GameState.IN_PROGRESS) return;

        List<BingoTeam> potentialWinners = new ArrayList<>();
        int highestScore = -1;

        for (BingoTeam team : this.teams) {
            int currentScore = team.getFoundItems().size();
            if (currentScore > highestScore) {
                highestScore = currentScore;
                potentialWinners.clear();
                potentialWinners.add(team);
            } else if (currentScore == highestScore) {
                potentialWinners.add(team);
            }
        }

        endMatch(potentialWinners);
    }
}
