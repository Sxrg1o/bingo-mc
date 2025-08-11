package com.bingaso.bingo.model;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.Material;

public class BingoItem {

    private final Material material;
    private final Set<BingoTeam> completedByTeams = new HashSet<>();

    public BingoItem(Material material) {
        this.material = material;
    }

    public Material getMaterial() {
        return material;
    }

    public void addCompletingTeam(BingoTeam team) {
        this.completedByTeams.add(team);
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
