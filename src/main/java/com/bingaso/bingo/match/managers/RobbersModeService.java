package com.bingaso.bingo.match.managers;

import com.bingaso.bingo.BingoPlugin;
import com.bingaso.bingo.match.BingoMatch;
import com.bingaso.bingo.player.BingoPlayer;
import com.bingaso.bingo.quest.BingoQuest;
import com.bingaso.bingo.quest.BingoQuestItem;
import com.bingaso.bingo.team.BingoTeam;
import com.bingaso.bingo.team.TeamQuestService;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class RobbersModeService {

    private final BingoMatch bingoMatch;
    private final TeamQuestService teamQuestService;

    public RobbersModeService(BingoMatch bingoMatch) {
        this.bingoMatch = bingoMatch;
        this.teamQuestService = new TeamQuestService(
            bingoMatch.getBingoTeamRepository()
        );
    }

    public void handleItemLoss(Player player, Material material) {
        if (!bingoMatch.getMatchSettings().isRobbersModeEnabled()) {
            return;
        }

        BingoPlayer bingoPlayer = bingoMatch
            .getBingoPlayerRepository()
            .findByUUID(player.getUniqueId());
        if (bingoPlayer == null) {
            return;
        }

        BingoTeam team = bingoMatch
            .getBingoTeamRepository()
            .findTeamByPlayer(bingoPlayer);
        if (team == null) {
            return;
        }

        BingoQuest quest = bingoMatch.getBingoCard().getItem(material);
        if (quest == null || !(quest instanceof BingoQuestItem)) {
            return;
        }

        if (!team.hasCompletedQuest(quest)) {
            return;
        }

        BingoPlugin.getInstance()
            .getServer()
            .getScheduler()
            .runTaskLater(
                BingoPlugin.getInstance(),
                () -> {
                    if (!teamHasItem(team, material)) {
                        teamQuestService.removeQuestCompletion(team, quest);
                    }
                },
                1L
            );
    }

    public void scheduleInventoryCheck(Player player) {
        if (!bingoMatch.getMatchSettings().isRobbersModeEnabled()) {
            return;
        }

        BingoPlayer bingoPlayer = bingoMatch
            .getBingoPlayerRepository()
            .findByUUID(player.getUniqueId());
        if (bingoPlayer == null) return;

        BingoTeam team = bingoMatch
            .getBingoTeamRepository()
            .findTeamByPlayer(bingoPlayer);
        if (team == null) return;

        BingoPlugin.getInstance()
            .getServer()
            .getScheduler()
            .runTaskLater(
                BingoPlugin.getInstance(),
                () -> {
                    checkTeamInventoryForCompletedQuests(team);
                },
                1L
            );
    }

    private void checkTeamInventoryForCompletedQuests(BingoTeam team) {
        Set<BingoQuest> completedQuests = new HashSet<>(
            team.getCompletedQuests().keySet()
        );

        for (BingoQuest quest : completedQuests) {
            if (quest instanceof BingoQuestItem) {
                Material itemMaterial = ((BingoQuestItem) quest).getMaterial();
                if (!teamHasItem(team, itemMaterial)) {
                    teamQuestService.removeQuestCompletion(team, quest);
                    team
                        .getOnlinePlayers()
                        .forEach(p ->
                            p.sendMessage(
                                "§cYour team has lost the: " +
                                    itemMaterial.name() +
                                    "!"
                            )
                        );
                }
            }
        }
    }

    private boolean teamHasItem(BingoTeam team, Material material) {
        for (BingoPlayer teamMember : team.getPlayers()) {
            Player onlinePlayer = teamMember.getOnlinePlayer();
            if (onlinePlayer != null && onlinePlayer.isOnline()) {
                if (playerHasItem(onlinePlayer, material)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean playerHasItem(Player player, Material material) {
        Inventory inventory = player.getInventory();
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() == material) {
                return true;
            }
        }
        return false;
    }
}
