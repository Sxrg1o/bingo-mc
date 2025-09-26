package com.bingaso.bingo.match;

import com.bingaso.bingo.card.BingoCard;
import com.bingaso.bingo.card.BingoCardGenerator;
import com.bingaso.bingo.card.BingoCardGui;
import com.bingaso.bingo.gui.BingoGuiItemFactory;
import com.bingaso.bingo.match.managers.MatchLifecycleManager;
import com.bingaso.bingo.match.managers.PlayerManager;
import com.bingaso.bingo.match.managers.TeamManager;
import com.bingaso.bingo.match.managers.TeamManager.MaxPlayersException;
import com.bingaso.bingo.match.managers.WinConditionService;
import com.bingaso.bingo.player.BingoPlayer;
import com.bingaso.bingo.player.BingoPlayerRepository;
import com.bingaso.bingo.quest.BingoQuest;
import com.bingaso.bingo.scoreboard.BingoGlobalScoreboard;
import com.bingaso.bingo.team.BingoTeam;
import com.bingaso.bingo.team.BingoTeamRepository;
import com.bingaso.bingo.team.BingoTeamRepository.TeamNameAlreadyExistsException;
import com.bingaso.bingo.team.TeamQuestService;
import com.bingaso.bingo.team.TeamQuestService.QuestAlreadyCompletedException;
import com.bingaso.bingo.utils.Broadcaster;
import java.time.Instant;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Main class that handles a Bingo match.
 * <p>
 * This class coordinates all aspects of a Bingo game including player management,
 * team creation, match lifecycle, card generation, and win conditions.
 * </p>
 *
 * @since 1.0
 */
public class BingoMatch {

    /**
     * Represents the current state of a Bingo match.
     *
     * @since 1.0
     */
    public static enum State {
        /**
         * Players are in the lobby, waiting for the match to start.
         */
        LOBBY,
        /**
         * Match is currently active and in progress.
         */
        IN_PROGRESS,
        /**
         * Match is in the finishing state (completing end-game tasks).
         */
        FINISHING,
    }

    /** Match configuration and settings. */
    private final BingoMatchSettings matchSettings;
    /** Manages players in the match. */
    private final PlayerManager playerManager;
    /** Manages teams in the match. */
    private final TeamManager teamManager;
    /** Manages the match lifecycle (start, end). */
    private final MatchLifecycleManager lifecycleManager;
    /** Service for checking win conditions. */
    private WinConditionService winConditionService;

    /** Utility for broadcasting messages to players. */
    private final Broadcaster broadcaster = new Broadcaster();
    /** Global scoreboard for the match. */
    private BingoGlobalScoreboard globalScoreboard;

    /** The bingo card containing the items teams need to find. */
    private BingoCard bingoCard;

    /**
     * Creates a new Bingo match with default settings.
     * <p>
     * Initializes the match settings, player manager, team manager, and lifecycle manager.
     * Also generates a new bingo card.
     * </p>
     */
    public BingoMatch() {
        this.matchSettings = new BingoMatchSettings();
        this.playerManager = new PlayerManager();
        this.teamManager = new TeamManager(matchSettings.getMaxTeamSize());
        this.lifecycleManager = new MatchLifecycleManager(matchSettings);
        generateNewBingoCard();
    }

    /**
     * Adds a player to the match.
     *
     * @param player The Bukkit player to add
     */
    public void addPlayer(Player player) {
        playerManager.addPlayer(player);
    }

    /**
     * Removes a player from the match and their team if they are in one.
     *
     * @param player The Bukkit player to remove
     */
    public void removePlayer(Player player) {
        BingoPlayer bingoPlayer = playerManager.getBingoPlayer(
            player.getUniqueId()
        );
        if (bingoPlayer != null) {
            teamManager.removePlayerFromBingoTeam(bingoPlayer);
            playerManager.removePlayer(player);
        }
    }

    /**
     * Creates a new team with the specified name.
     *
     * @param name The team name to create
     * @return The newly created team
     * @throws TeamNameAlreadyExistsException If a team with the same name already exists
     */
    public BingoTeam createBingoTeam(String name)
        throws TeamNameAlreadyExistsException {
        return teamManager.createBingoTeam(name);
    }

    /**
     * Adds a player to the specified team.
     * <p>
     * If the player is already in a team, they will be removed from that team first.
     * Updates the player's tab list name to reflect their team color.
     * </p>
     *
     * @param player The player to add
     * @param bingoTeam The team to add the player to
     * @throws MaxPlayersException If the team has reached its maximum player capacity
     */
    public void addPlayerToBingoTeam(Player player, BingoTeam bingoTeam)
        throws MaxPlayersException {
        BingoPlayer bingoPlayer = playerManager.getBingoPlayer(
            player.getUniqueId()
        );
        if (bingoPlayer != null) {
            teamManager.addPlayerToBingoTeam(bingoPlayer, bingoTeam);
            updatePlayerTabName(player);
        }
    }

    /**
     * Removes a player from their team.
     * <p>
     * Updates the player's tab list name to remove the team color.
     * </p>
     *
     * @param player The player to remove from their team
     */
    public void removePlayerFromBingoTeam(Player player) {
        BingoPlayer bingoPlayer = playerManager.getBingoPlayer(
            player.getUniqueId()
        );
        if (bingoPlayer != null) {
            teamManager.removePlayerFromBingoTeam(bingoPlayer);
            updatePlayerTabName(player);
        }
    }

    /**
     * Handles when a player finds a bingo item.
     * <p>
     * This method checks if:
     * <ul>
     *   <li>The match is in progress</li>
     *   <li>The item is on the bingo card</li>
     *   <li>The player is in a team</li>
     *   <li>The team hasn't already found this item</li>
     *   <li>The item is not locked (in LOCKED game mode)</li>
     * </ul>
     * If all conditions are met, the item is marked as found for the team and win conditions are checked.
     * </p>
     *
     * @param player The player who found the item
     * @param item The Minecraft material that was found
     */
    public void onPlayerFindsItem(BingoPlayer player, Material item) {
        if (lifecycleManager.getState() != State.IN_PROGRESS) return;

        BingoQuest quest = bingoCard.getItem(item);
        if (quest == null) return;

        BingoPlayer bingoPlayer = playerManager.getBingoPlayer(
            player.getUniqueId()
        );
        if (bingoPlayer == null) return;

        BingoTeam team = teamManager.getTeamByPlayer(bingoPlayer);
        if (team == null || team.hasCompletedQuest(quest)) return;

        TeamQuestService questService = new TeamQuestService(
            teamManager.getTeamRepository()
        );

        boolean isLocked =
            matchSettings.getGameMode() == BingoMatchSettings.GameMode.LOCKED &&
            questService.isQuestCompletedByAnyTeam(quest);

        if (!isLocked) {
            try {
                questService.completeQuest(team, quest);
                broadcaster.announceItemFound(team, item);
                BingoCardGui.getInstance().updateInventories();

                List<BingoTeam> winners =
                    winConditionService.checkWinConditions(
                        team,
                        teamManager.getTeamRepository()
                    );
                if (!winners.isEmpty()) {
                    end(winners);
                }
            } catch (QuestAlreadyCompletedException e) {
                player.getOnlinePlayer().sendMessage("§cItem already claimed!");
                return;
            }
        }
    }

    /**
     * Starts the bingo match.
     * <p>
     * This will:
     * <ul>
     *   <li>Initialize win condition service and scoreboard</li>
     *   <li>Set players to survival mode</li>
     *   <li>Clear player inventories and give them a bingo card item</li>
     *   <li>Start match timer (for timed matches)</li>
     *   <li>Start the global scoreboard</li>
     * </ul>
     * The match won't start if there are no teams.
     * </p>
     */
    public void start() {
        if (teamManager.getTeamRepository().isEmpty()) return;

        this.winConditionService = new WinConditionService(
            matchSettings,
            bingoCard
        );
        this.globalScoreboard = new BingoMatchScoreboard(this);

        for (BingoPlayer bingoPlayer : playerManager.getAllPlayers()) {
            Player player = bingoPlayer.getOnlinePlayer();
            if (player != null) {
                player.setGameMode(GameMode.SURVIVAL);
                player.getInventory().clear();
                player
                    .getInventory()
                    .addItem(BingoGuiItemFactory.createBingoCardItem());
            }
        }

        lifecycleManager.start(() -> {
            List<BingoTeam> winners = winConditionService.determineTimedWinners(
                teamManager.getTeamRepository()
            );
            end(winners);
        });

        globalScoreboard.start(20);
    }

    /**
     * Ends the bingo match and announces winners.
     * <p>
     * This will:
     * <ul>
     *   <li>Notify the lifecycle manager of the winners</li>
     *   <li>Stop the scoreboard</li>
     *   <li>Clear team quests and player data</li>
     *   <li>Update all player tab names</li>
     *   <li>Reset player game modes and give them team selection items</li>
     * </ul>
     * </p>
     *
     * @param winners The list of winning teams
     */
    private void end(List<BingoTeam> winners) {
        List<BingoPlayer> playersInMatch = playerManager.getAllPlayers();

        lifecycleManager.end(winners);

        if (globalScoreboard != null) globalScoreboard.stop();

        TeamQuestService questService = new TeamQuestService(
            teamManager.getTeamRepository()
        );
        teamManager
            .getTeamRepository()
            .findAll()
            .forEach(questService::clearAllQuests);

        teamManager.clear();
        playerManager.clear();

        for (BingoPlayer p : playersInMatch) {
            Player onlinePlayer = p.getOnlinePlayer();
            if (onlinePlayer != null && onlinePlayer.isOnline()) {
                updatePlayerTabName(onlinePlayer);
            }
        }

        for (Player onlinePlayer : org.bukkit.Bukkit.getOnlinePlayers()) {
            GameMode gm = onlinePlayer.getGameMode();
            if (gm == GameMode.SURVIVAL || gm == GameMode.ADVENTURE) {
                playerManager.addPlayer(onlinePlayer);

                onlinePlayer.getInventory().clear();
                onlinePlayer.setGameMode(GameMode.ADVENTURE);
                onlinePlayer
                    .getInventory()
                    .addItem(BingoGuiItemFactory.createTeamSelectionItem());
            }
        }
    }

    /**
     * Gets the current state of the match.
     *
     * @return The current match state
     */
    public State getState() {
        return lifecycleManager.getState();
    }

    /**
     * Gets the bingo card for this match.
     *
     * @return The current bingo card
     */
    public BingoCard getBingoCard() {
        return bingoCard;
    }

    /**
     * Gets the match settings.
     *
     * @return The match settings
     */
    public BingoMatchSettings getMatchSettings() {
        return matchSettings;
    }

    /**
     * Gets the team manager.
     *
     * @return The team manager
     */
    public TeamManager getTeamManager() {
        return teamManager;
    }

    /**
     * Gets the player manager.
     *
     * @return The player manager
     */
    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    /**
     * Gets the team repository.
     *
     * @return The team repository
     */
    public BingoTeamRepository getBingoTeamRepository() {
        return teamManager.getTeamRepository();
    }

    /**
     * Gets the player repository.
     *
     * @return The player repository
     */
    public BingoPlayerRepository getBingoPlayerRepository() {
        return playerManager.getPlayerRepository();
    }

    /**
     * Gets the team that a player belongs to.
     *
     * @param player The player to check
     * @return The player's team, or null if not in a team
     */
    public BingoTeam getBingoTeamFromPlayer(Player player) {
        BingoPlayer bingoPlayer = playerManager.getBingoPlayer(
            player.getUniqueId()
        );
        return (bingoPlayer != null)
            ? teamManager.getTeamByPlayer(bingoPlayer)
            : null;
    }

    /**
     * Calculates the duration of the match in seconds from start to the given instant.
     *
     * @param instant The current time
     * @return The match duration in seconds, or 0 if match hasn't started
     */
    public long getMatchDurationSeconds(@NotNull Instant instant) {
        if (lifecycleManager.getStartInstant() == null) {
            return 0;
        }
        return (
            instant.getEpochSecond() -
            lifecycleManager.getStartInstant().getEpochSecond()
        );
    }

    /**
     * Calculates the duration of the match in milliseconds from start to the given instant.
     *
     * @param instant The current time
     * @return The match duration in milliseconds, or 0 if match hasn't started
     */
    public long getMatchDurationMilliseconds(@NotNull Instant instant) {
        if (lifecycleManager.getStartInstant() == null) {
            return 0;
        }
        return (
            instant.toEpochMilli() -
            lifecycleManager.getStartInstant().toEpochMilli()
        );
    }

    /**
     * Generates a new bingo card for the match based on current settings.
     * <p>
     * Uses the item repository and difficulty level from match settings.
     * </p>
     */
    public void generateNewBingoCard() {
        BingoCardGenerator cardGenerator = new BingoCardGenerator(
            matchSettings.getItemRepository(),
            matchSettings.getDifficultyLevel()
        );
        this.bingoCard = cardGenerator.generateCard();
    }

    /**
     * Updates a player's tab list name to reflect their team color.
     * <p>
     * If the player is in a team, their name will be colored with the team color.
     * Otherwise, their name will be displayed without color.
     * </p>
     *
     * @param player The player whose tab name to update
     */
    public void updatePlayerTabName(Player player) {
        BingoPlayer bingoPlayer = playerManager.getBingoPlayer(
            player.getUniqueId()
        );

        BingoTeam team = (bingoPlayer != null)
            ? teamManager.getTeamByPlayer(bingoPlayer)
            : null;

        if (team != null) {
            player.playerListName(
                Component.text(player.getName(), team.getColor())
            );
        } else {
            player.playerListName(Component.text(player.getName()));
        }
    }

    /**
     * Updates tab list names for all players in the match.
     * <p>
     * This is useful when teams change or at the start/end of a match.
     * </p>
     */
    public void updateAllPlayersTabNames() {
        playerManager
            .getAllPlayers()
            .forEach(p -> {
                Player onlinePlayer = p.getOnlinePlayer();
                if (onlinePlayer != null) {
                    updatePlayerTabName(onlinePlayer);
                }
            });
    }
}
