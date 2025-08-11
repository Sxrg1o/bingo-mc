package com.bingaso.bingo.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BingoPlayer {

    private static final Map<UUID, BingoPlayer> ALL_PLAYERS = new HashMap<>();

    public static BingoPlayer getBingoPlayer(UUID uuid) {
        return ALL_PLAYERS.get(uuid);
    }

    private final UUID uuid;
    private String name;
    private boolean isOnline;
    private BingoTeam team;

    public BingoPlayer(UUID uuid) {
        this.uuid = uuid;
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            this.name = player.getName();
            this.isOnline = true;
        } else {
            this.name = Bukkit.getOfflinePlayer(uuid).getName();
            this.isOnline = false;
        }
        ALL_PLAYERS.put(uuid, this);
    }

    public void setTeam(BingoTeam newTeam) {
        if (this.team != null) {
            this.team.removePlayer(this);
        }
        this.team = newTeam;
        try {
            if (newTeam != null) {
                newTeam.addPlayer(this);
            }
        } catch (Exception e) {}
    }

    public Player getOnlinePlayer() {
        if (!isOnline()) {
            return null;
        } else {
            return Bukkit.getPlayer(uuid);
        }
    }

    public BingoPlayer(Player player) {
        this.uuid = player.getUniqueId();
        this.name = player.getName();
        this.isOnline = true;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public BingoTeam getTeam() {
        return team;
    }

    public boolean isOnline() {
        Player player = Bukkit.getPlayer(uuid);
        this.isOnline = player != null && player.isOnline();
        return isOnline;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public void updateStatus() {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            this.name = player.getName();
            this.isOnline = true;
        } else {
            this.isOnline = false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BingoPlayer that = (BingoPlayer) o;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public String toString() {
        return (
            "BingoPlayer{" +
            "uuid=" +
            uuid +
            ", name='" +
            name +
            '\'' +
            ", isOnline=" +
            isOnline +
            '}'
        );
    }
}
