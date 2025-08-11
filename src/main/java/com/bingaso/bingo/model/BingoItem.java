package com.bingaso.bingo.model;

import java.util.HashSet;
import java.util.Set;
import java.time.Instant;
import java.util.HashMap;

import org.bukkit.Material;

public class BingoItem {

    private final Material material;
    private final Set<BingoTeam> completedByTeams = new HashSet<>();
    private final HashMap<BingoTeam, Instant> completionTimestamps = new HashMap<>();

    public BingoItem(Material material) {
        this.material = material;
    }

    public Material getMaterial() {
        return material;
    }

    public void addCompletingTeam(BingoTeam team) {
        this.completedByTeams.add(team);
        this.completionTimestamps.put(team, Instant.now());
    }

    public boolean isCompletedBy(BingoTeam team) {
        return this.completedByTeams.contains(team);
    }

    public boolean isCompletedByAnyTeam() {
        return !this.completedByTeams.isEmpty();
    }

    public Set<BingoTeam> getCompletedByTeams() {
        return completedByTeams;
    }
}