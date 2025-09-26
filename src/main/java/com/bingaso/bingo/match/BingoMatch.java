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

public class BingoMatch {

    public static enum State {
        LOBBY,
        IN_PROGRESS,
        FINISHING,
    }

    private final BingoMatchSettings matchSettings;
    private final PlayerManager playerManager;
    private final TeamManager teamManager;
    private final MatchLifecycleManager lifecycleManager;
    private WinConditionService winConditionService;

    private final Broadcaster broadcaster = new Broadcaster();
    private BingoGlobalScoreboard globalScoreboard;

    private BingoCard bingoCard;

    public BingoMatch() {
        this.matchSettings = new BingoMatchSettings();
        this.playerManager = new PlayerManager();
        this.teamManager = new TeamManager(matchSettings.getMaxTeamSize());
        this.lifecycleManager = new MatchLifecycleManager(matchSettings);
        generateNewBingoCard();
    }

    public void addPlayer(Player player) {
        playerManager.addPlayer(player);
    }

    public void removePlayer(Player player) {
        BingoPlayer bingoPlayer = playerManager.getBingoPlayer(
            player.getUniqueId()
        );
        if (bingoPlayer != null) {
            teamManager.removePlayerFromBingoTeam(bingoPlayer);
            playerManager.removePlayer(player);
        }
    }

    public BingoTeam createBingoTeam(String name)
        throws TeamNameAlreadyExistsException {
        return teamManager.createBingoTeam(name);
    }

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

    public void removePlayerFromBingoTeam(Player player) {
        BingoPlayer bingoPlayer = playerManager.getBingoPlayer(
            player.getUniqueId()
        );
        if (bingoPlayer != null) {
            teamManager.removePlayerFromBingoTeam(bingoPlayer);
            updatePlayerTabName(player);
        }
    }

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

    public State getState() {
        return lifecycleManager.getState();
    }

    public BingoCard getBingoCard() {
        return bingoCard;
    }

    public BingoMatchSettings getMatchSettings() {
        return matchSettings;
    }

    public TeamManager getTeamManager() {
        return teamManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public BingoTeamRepository getBingoTeamRepository() {
        return teamManager.getTeamRepository();
    }

    public BingoPlayerRepository getBingoPlayerRepository() {
        return playerManager.getPlayerRepository();
    }

    public BingoTeam getBingoTeamFromPlayer(Player player) {
        BingoPlayer bingoPlayer = playerManager.getBingoPlayer(
            player.getUniqueId()
        );
        return (bingoPlayer != null)
            ? teamManager.getTeamByPlayer(bingoPlayer)
            : null;
    }

    public long getMatchDurationSeconds(@NotNull Instant instant) {
        if (lifecycleManager.getStartInstant() == null) {
            return 0;
        }
        return (
            instant.getEpochSecond() -
            lifecycleManager.getStartInstant().getEpochSecond()
        );
    }

    public long getMatchDurationMilliseconds(@NotNull Instant instant) {
        if (lifecycleManager.getStartInstant() == null) {
            return 0;
        }
        return (
            instant.toEpochMilli() -
            lifecycleManager.getStartInstant().toEpochMilli()
        );
    }

    public void generateNewBingoCard() {
        BingoCardGenerator cardGenerator = new BingoCardGenerator(
            matchSettings.getItemRepository(),
            matchSettings.getDifficultyLevel()
        );
        this.bingoCard = cardGenerator.generateCard();
    }

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
