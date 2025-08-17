package com.bingaso.bingo.game;

import com.bingaso.bingo.BingoPlugin;
import com.bingaso.bingo.model.BingoCard;
import com.bingaso.bingo.model.BingoItem;
import com.bingaso.bingo.model.DifficultyLevel;
import com.bingaso.bingo.model.GameMode;
import com.bingaso.bingo.model.TeamMode;
import com.bingaso.bingo.player.BingoPlayer;
import com.bingaso.bingo.player.BingoPlayerManager;
import com.bingaso.bingo.team.BingoTeam;
import com.bingaso.bingo.team.BingoTeamManager;
import com.bingaso.bingo.team.BingoTeamManager.MaxPlayersException;
import com.bingaso.bingo.team.BingoTeamManager.MaxTeamsException;
import com.bingaso.bingo.team.BingoTeamManager.TeamNameAlreadyExistsException;
import com.bingaso.bingo.team.TeamDisplayManager;
import com.bingaso.bingo.utils.Broadcaster;
import com.bingaso.bingo.utils.ItemRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

/**
 * Core class that manages the Bingo game lifecycle and gameplay.
 * Handles game state transitions, team management, match settings,
 * item discovery, win conditions, and coordination between game components.
 */
public class BingoGameManager {

    private ItemRepository itemRepository = new ItemRepository();

    private GameState currentState = GameState.LOBBY;
    private MatchSettings currentMatchSettings = new MatchSettings();
    private BingoCard sharedBingoCard;
    private Instant startInstant;

    private final CardGenerator cardGenerator = new CardGenerator(
        itemRepository
    );
    private final Broadcaster broadcaster = new Broadcaster();
    private final BingoScoreboard scoreboard = new BingoScoreboard(this);
    private TeamDisplayManager displayManager = new TeamDisplayManager();

    private final BingoTeamManager bingoTeamManager = new BingoTeamManager(
        displayManager
    );
    private final BingoPlayerManager bingoPlayerManager =
        new BingoPlayerManager();
    private final List<BingoTeam> winnerTeams = new ArrayList<>();
    private BukkitTask matchEndTask = null;

    /**
     * Creates a new GameManager with the specified dependencies.
     * Initializes with default match settings.
     *
     */
    public BingoGameManager() {}

    /**
     * Updates the match settings for the next game.
     * Settings can only be changed while in the lobby state.
     *
     * @param matchSettings The new match settings to apply
     */
    public void updateSettings(MatchSettings matchSettings) {
        if (currentState == GameState.LOBBY) {
            this.currentMatchSettings = matchSettings;
        }
    }

    /**
     * Gets the current state of the game.
     *
     * @return The current GameState (LOBBY, IN_PROGRESS, or FINISHING)
     */
    public GameState getCurrentState() {
        return currentState;
    }

    /**
     * Calculates the elapsed time since the match started.
     *
     * @return The number of seconds elapsed since the match started, or 0 if not in progress
     */
    public long getElapsedSeconds() {
        if (currentState != GameState.IN_PROGRESS) return 0;
        return Instant.now().getEpochSecond() - startInstant.getEpochSecond();
    }

    /**
     * Gets a list of all online players currently participating in the match.
     *
     * @return A list of online players across all teams
     */
    public List<Player> getOnlinePlayersInMatch() {
        List<Player> players = new ArrayList<>();
        for (BingoTeam team : bingoTeamManager.getTeams()) {
            players.addAll(team.getOnlinePlayers());
        }
        return players;
    }

    /**
     * Gets all teams participating in the current match.
     *
     * @return A list of participating teams
     */
    public List<BingoTeam> getTeams() {
        return bingoTeamManager.getTeams();
    }

    /**
     * Starts a new Bingo match with the current settings.
     * Initializes teams, generates a Bingo card, and transitions to the IN_PROGRESS state.
     * For timed matches, schedules the end-of-match task.
     */
    public void startMatch() {
        // Restart state
        bingoTeamManager.clear();
        this.winnerTeams.clear();
        if (this.matchEndTask != null) {
            this.matchEndTask.cancel();
        }

        // Generate card
        DifficultyLevel difficulty = currentMatchSettings.getDifficultyLevel();
        this.sharedBingoCard = cardGenerator.generateCard(difficulty);

        // Team management
        if (currentMatchSettings.getTeamMode() != TeamMode.MANUAL) {
            // TODO: Random assignment
        }

        if (bingoTeamManager.isEmpty()) {
            // TODO: Cancel match if no valid teams
            return;
        }

        // When teams are ready
        this.currentState = GameState.IN_PROGRESS;
        this.startInstant = Instant.now();

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

        spreadAndPrepareTeams();
        broadcaster.announceStart();
        this.scoreboard.start();
    }

    /**
     * Stops the current match and resets the game state to LOBBY.
     * Cleans up resources, cancels scheduled tasks, and clears team data.
     */
    public void stopMatch() {
        // idk if we need smth more, xd tp to lobby
        if (this.matchEndTask != null) {
            this.matchEndTask.cancel();
            this.matchEndTask = null;
        }
        this.scoreboard.stop();
        this.currentState = GameState.LOBBY;
        for (BingoTeam team : bingoTeamManager.getTeams()) {
            bingoTeamManager.clearTeamFoundItems(team);
        }
        bingoTeamManager.clear();
        this.winnerTeams.clear();
    }

    /**
     * Ends the current match with the specified winners.
     * Transitions the game to the FINISHING state and announces the results.
     *
     * @param winners The list of teams that won the match
     */
    private void endMatch(List<BingoTeam> winners) {
        if (currentState != GameState.IN_PROGRESS) return;

        this.currentState = GameState.FINISHING;
        this.winnerTeams.clear();
        this.winnerTeams.addAll(winners);

        broadcaster.announceWinners(winners);

        // TODO: Celebration or smth like that idk

        stopMatch();
    }

    /**
     * Handles a player finding an item during the match.
     * Checks if the item is on the Bingo card, marks it as found for the player's team,
     * announces the discovery, and checks if win conditions have been met.
     *
     * @param player The player who found the item
     * @param item The material type of the found item
     */
    public void onPlayerFindsItem(BingoPlayer player, Material item) {
        if (currentState != GameState.IN_PROGRESS) return;

        BingoItem bingoItem = sharedBingoCard.getItem(item);
        if (bingoItem == null) return;

        BingoTeam team = bingoTeamManager.getPlayerTeam(player);
        if (team == null) return;

        if (bingoItem.isCompletedBy(team)) return;

        if (
            currentMatchSettings.getGameMode() == GameMode.LOCKED &&
            bingoItem.isCompletedByAnyTeam()
        ) {
            if (!bingoItem.isCompletedBy(team)) {
                player.getOnlinePlayer().sendMessage("§cItem already claimed!");
                return;
            }
        }

        bingoItem.addCompletingTeam(team, startInstant);
        bingoTeamManager.addFoundItemToTeam(team, item);

        broadcaster.announceItemFound(team, item);

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
        GameMode mode = currentMatchSettings.getGameMode();
        switch (mode) {
            case LOCKED:
                int teamCount = bingoTeamManager.getTeams().size();
                if (teamCount == 0) return;

                int requiredItems = (int) Math.floor(25.0 / teamCount) + 1;
                int foundItems = team.getFoundItems().size();

                if (foundItems >= requiredItems) {
                    endMatch(Collections.singletonList(team));
                }
                break;
            case STANDARD:
                // Rows
                List<BingoItem> items = sharedBingoCard.getItems();
                for (int i = 0; i < 5; i++) {
                    boolean rowComplete = true;
                    for (int j = 0; j < 5; j++) {
                        if (!items.get(i * 5 + j).isCompletedBy(team)) {
                            rowComplete = false;
                            break;
                        }
                    }
                    if (rowComplete) {
                        endMatch(Collections.singletonList(team));
                        break;
                    }
                }
                // Columns
                for (int i = 0; i < 5; i++) {
                    boolean columnComplete = true;
                    for (int j = 0; j < 5; j++) {
                        if (!items.get(j * 5 + i).isCompletedBy(team)) {
                            columnComplete = false;
                            break;
                        }
                    }
                    if (columnComplete) {
                        endMatch(Collections.singletonList(team));
                        break;
                    }
                }
                // diagonal
                boolean diagonalComplete = true;
                for (int i = 0; i < 5; i++) {
                    if (!items.get(i * 5 + i).isCompletedBy(team)) {
                        diagonalComplete = false;
                        break;
                    }
                }
                if (diagonalComplete) {
                    endMatch(Collections.singletonList(team));
                    break;
                }
                // Anti-diagonal
                boolean antiDiagonalComplete = true;
                for (int i = 0; i < 5; i++) {
                    if (!items.get(i * 5 + (4 - i)).isCompletedBy(team)) {
                        antiDiagonalComplete = false;
                        break;
                    }
                }
                if (antiDiagonalComplete) {
                    endMatch(Collections.singletonList(team));
                    break;
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

    /**
     * Determines the winners for a timed match when the time expires.
     * Winners are teams with the highest number of found items.
     * Multiple teams can win if they have the same highest score.
     */
    private void determineTimedWinners() {
        if (currentState != GameState.IN_PROGRESS) return;

        List<BingoTeam> potentialWinners = new ArrayList<>();
        int highestScore = -1;

        for (BingoTeam team : bingoTeamManager.getTeams()) {
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

    private void spreadAndPrepareTeams() {
        List<Player> playersToSpread = getOnlinePlayersInMatch();
        if (playersToSpread.isEmpty()) return;

        World world = Bukkit.getWorld("world");
        if (world == null) {
            broadcaster.announce(
                Component.text(
                    "§cError: Could not find the main world to teleport players.",
                    NamedTextColor.RED
                )
            );
            return;
        }

        String targetPlayerNames = playersToSpread
            .stream()
            .map(Player::getName)
            .collect(Collectors.joining(" "));

        String command = String.format(
            "spreadplayers 0 0 500.0 1000.0 true %s",
            targetPlayerNames
        );

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);

        broadcaster.announce(
            Component.text(
                "Teleporting teams to their initial positions...",
                NamedTextColor.AQUA
            )
        );

        for (Player player : playersToSpread) {
            player.setRespawnLocation(player.getLocation(), true);

            player.getInventory().clear();
            player.setGameMode(org.bukkit.GameMode.SURVIVAL);
            player.setHealth(20.0);
            player.setFoodLevel(20);
            player.setSaturation(5.0f);
            player.setExp(0);
            player.setLevel(0);

            // TODO: Kit
        }
    }

    /**
     * Gets the shared Bingo card used in the current match.
     *
     * @return The current match's Bingo card
     */
    public BingoCard getSharedBingoCard() {
        return sharedBingoCard;
    }

    /**
     * Gets the current match settings.
     *
     * @return The current match settings
     */
    public MatchSettings getCurrentMatchSettings() {
        return currentMatchSettings;
    }

    public void removeBingoPlayer(Player player) {
        // Only able to remove players in lobby state
        if (currentState != GameState.LOBBY) return;

        BingoPlayer bingoPlayer = bingoPlayerManager.getBingoPlayer(player);
        if (bingoPlayer == null) return;

        bingoPlayerManager.removeBingoPlayer(player);
        bingoTeamManager.removePlayerFromTeam(bingoPlayer);
    }

    public BingoPlayer addPlayer(Player player) {
        // Only able to add players in lobby state
        if (currentState != GameState.LOBBY) return null;

        return bingoPlayerManager.createBingoPlayer(player);
    }

    public void addPlayerToTeam(Player player, BingoTeam bingoTeam)
        throws MaxPlayersException {
        // Only able to change teams during lobby state
        if (currentState != GameState.LOBBY) return;

        BingoPlayer bingoPlayer = bingoPlayerManager.getBingoPlayer(player);
        bingoTeamManager.addPlayerToTeam(bingoPlayer, bingoTeam);
    }

    public BingoPlayer getBingoPlayer(Player player) {
        return bingoPlayerManager.getBingoPlayer(player);
    }

    public BingoTeam createBingoTeam(String name)
        throws TeamNameAlreadyExistsException, MaxTeamsException {
        // Only able to create teams during lobby state
        if (currentState != GameState.LOBBY) return null;

        BingoTeam bingoTeam = bingoTeamManager.createBingoTeam(name);
        return bingoTeam;
    }

    public BingoTeam getPlayerTeam(Player player) {
        BingoPlayer bingoPlayer = bingoPlayerManager.getBingoPlayer(player);
        if (bingoPlayer == null) return null;
        return bingoTeamManager.getPlayerTeam(bingoPlayer);
    }

    public BingoTeam getBingoTeam(String teamName) {
        return bingoTeamManager.getTeamByName(teamName);
    }

    public int getMaxTeamSize() {
        return bingoTeamManager.getMaxTeamSize();
    }

    public BingoTeam getNextTeam(BingoTeam bingoTeam) {
        return bingoTeamManager.getNextTeam(bingoTeam);
    }

    public BingoTeam getPreviousTeam(BingoTeam bingoTeam) {
        return bingoTeamManager.getPreviousTeam(bingoTeam);
    }
}
