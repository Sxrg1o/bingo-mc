package com.bingaso.bingo.game;

import com.bingaso.bingo.model.BingoCard;
import com.bingaso.bingo.model.BingoItem;
import com.bingaso.bingo.model.BingoPlayer;
import com.bingaso.bingo.model.BingoTeam;
import com.bingaso.bingo.model.GameMode;
import com.bingaso.bingo.model.TeamMode;
import org.bukkit.Material;

public class GameManager {

    private GameState currentState = GameState.LOBBY;
    private MatchSettings currentMatchSettings;
    private BingoCard sharedBingoCard;
    private long matchStartTime;

    public GameManager() {
        this.currentMatchSettings = new MatchSettings();
    }

    public void updateSettings(MatchSettings matchSettings) {
        if (currentState == GameState.LOBBY) {
            this.currentMatchSettings = matchSettings;
        }
    }

    public void startMatch() {
        // First generate card
        this.sharedBingoCard = new BingoCard();
        // Team management
        if (currentMatchSettings.getTeamMode() == TeamMode.MANUAL) {
            // TODO: Team GUI
        } else {
            // TODO: Random team assignment
        }

        // When teams are ready
        this.currentState = GameState.IN_PROGRESS;
        this.matchStartTime = System.currentTimeMillis();
    }

    public void stopMatch() {
        // idk if we need smth more
        this.currentState = GameState.LOBBY;
    }

    public void onPlayerFindsItem(BingoPlayer player, Material item) {
        if (currentState != GameState.IN_PROGRESS) return;

        BingoItem bingoItem = sharedBingoCard.getItem(item);
        if (bingoItem == null) return;

        BingoTeam team = player.getTeam();

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

        // TODO: Broadcast

        checkWinConditions(team);
    }

    private void checkWinConditions(BingoTeam team) {
        GameMode mode = currentMatchSettings.getGameMode();
        switch (mode) {
            case LOCKED:
            case STANDARD:
            case BLACKOUT:
        }
    }
}
