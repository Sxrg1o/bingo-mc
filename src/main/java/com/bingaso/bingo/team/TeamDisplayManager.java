package com.bingaso.bingo.team;

import com.bingaso.bingo.player.BingoPlayer;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class TeamDisplayManager {

    private final Scoreboard mainScoreboard;

    public TeamDisplayManager() {
        this.mainScoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
    }

    public void createTeamDisplay(BingoTeam bingoTeam) {
        String teamName = bingoTeam.getName();
        Team scoreboardTeam = mainScoreboard.getTeam(teamName);
        if (scoreboardTeam == null) {
            scoreboardTeam = mainScoreboard.registerNewTeam(teamName);
        }

        scoreboardTeam.color(bingoTeam.getColor());

        scoreboardTeam.setAllowFriendlyFire(false);
    }

    public void addPlayerToTeamDisplay(
        BingoPlayer bingoPlayer,
        BingoTeam bingoTeam
    ) {
        Team scoreboardTeam = mainScoreboard.getTeam(bingoTeam.getName());
        if (scoreboardTeam != null) {
            scoreboardTeam.addEntry(bingoPlayer.getName());
        }
    }

    public void removePlayerFromDisplay(BingoPlayer bingoPlayer) {
        Team scoreboardTeam = mainScoreboard.getEntryTeam(
            bingoPlayer.getName()
        );
        if (scoreboardTeam != null) {
            scoreboardTeam.removeEntry(bingoPlayer.getName());
        }
    }

    public void removeTeamDisplay(BingoTeam bingoTeam) {
        Team scoreboardTeam = mainScoreboard.getTeam(bingoTeam.getName());
        if (scoreboardTeam != null) {
            scoreboardTeam.unregister();
        }
    }

    public void cleanup() {
        for (Team team : mainScoreboard.getTeams()) {
            team.unregister();
        }
    }
}
