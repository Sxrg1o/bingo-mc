package com.bingaso.bingo.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.format.NamedTextColor;

/**
 * Represents a team of players in the Bingo game.
 */
public class BingoTeam {

    /**
     * Thrown when an attempt is made to add a player to a team that is already full.
     */
    public static class MaxPlayersException extends Exception {

        public MaxPlayersException(String message) {
            super(message);
        }
    }

    /**
     * Thrown when attempting to create a team with a name that already exists.
     */
    public static class TeamNameAlreadyExistsException extends Exception {
        public TeamNameAlreadyExistsException(String message) {
            super(message);
        }
    }

    private static final HashMap<String, BingoTeam> ALL_TEAMS = new HashMap<>();
    private static int MAX_SIZE = 1;

    /**
     * Sets the maximum size of the teams
     */
    public static void setTeamsMaxSize(int newMaxSize) {
        MAX_SIZE = newMaxSize;
        // Deletes all teams as size has changes
        for(BingoTeam t: new ArrayList<>(ALL_TEAMS.values())) {
            for(BingoPlayer player: t.getPlayers()) {
                t.removePlayer(player);
            }
        }
    }

    /**
     * Sets the maximum size of the teams
     */
    public static int getTeamsMaxSize() {
        return MAX_SIZE;
    }
    

    /**
     * Get all teams.
     * @return All teams created.
     */
    public static List<BingoTeam> getAllTeams() {
        return new ArrayList<>(ALL_TEAMS.values());
    }

    /**
     * Get a team by its name.
     * @param name The name of the team.
     * @return The team with the given name, or null if not found.
     */
    public static BingoTeam getTeamByName(String name) {
        return ALL_TEAMS.get(name);
    }

    /**
     * Checks if a team name is already taken.
     * @param name The name to check.
     * @return true if the name is available, false if it's already taken.
     */
    public static boolean isTeamNameAvailable(String name) {
        return ALL_TEAMS.get(name) == null;
    }

    /**
     * Gets the next team in the collection relative to this team.
     * @return The next team in the collection, or the first if this team is
     * the last.
     */
    public BingoTeam getNextTeam() {
        List<BingoTeam> teamList = new ArrayList<>(ALL_TEAMS.values());
        int currentIndex = teamList.indexOf(this);
        if(currentIndex == teamList.size() - 1) {
            return teamList.get(0);
        }
        return teamList.get(currentIndex + 1);
    }

    /**
     * Gets the previous team in the collection relative to this team.
     * @return The previous team in the collection, or the last if this team is the first.
     */
    public BingoTeam getPreviousTeam() {        
        List<BingoTeam> teamList = new ArrayList<>(ALL_TEAMS.values());
        int currentIndex = teamList.indexOf(this);
        if (currentIndex == 0) {
            return teamList.get(teamList.size() - 1);
        }
        return teamList.get(currentIndex - 1);
    }

    private final List<BingoPlayer> players = new ArrayList<>();
    private String name = "";
    private final Set<Material> foundItems = new HashSet<>();
    private final NamedTextColor color;

    /**
     * Constructs a new Team with a given name.
     * @param name The name of the team.
     * @throws TeamNameAlreadyExistsException If a team with this name already exists.
     */
    public BingoTeam(String name) throws TeamNameAlreadyExistsException {
        if (!isTeamNameAvailable(name)) {
            throw new TeamNameAlreadyExistsException("A team with the name '" + name + "' already exists.");
        }
        this.color = TEAM_COLORS.get(nextColorIdx);
        nextColorIdx = (nextColorIdx + 1) % TEAM_COLORS.size();
        this.name = name;
        ALL_TEAMS.put(this.name, this);
    }

    /**
     * Gets the name of the team.
     *
     * @return The team's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Changes the team's name.
     * 
     * @param newName The new name of the team.
     * @throws TeamNameAlreadyExistsException If a team with this name already exists.
     */
    public void setName(String newName) throws TeamNameAlreadyExistsException {
        if (!this.name.equals(newName) && !isTeamNameAvailable(newName)) {
            throw new TeamNameAlreadyExistsException("A team with the name '" + newName + "' already exists.");
        }
        // Remove from map with old name and add with new name
        ALL_TEAMS.remove(this.name);
        this.name = newName;
        ALL_TEAMS.put(this.name, this);
    }

    /**
     * Adds a player to the team by their UUID.
     * Does nothing if the player is already on the team.
     *
     * @param bingoPlayer The UUID of the player to add.
     * @throws ConfigNotSetException If the maxSize is not set.
     * @throws MaxPlayersException If the team is already full.
     * @throws PlayerAlreadyInThisTeam If the player is already in the team.
     */
    public void addPlayer(BingoPlayer bingoPlayer) throws MaxPlayersException {
        if(players.size() + 1 > BingoTeam.MAX_SIZE) {
            throw new BingoTeam.MaxPlayersException("Team is already full");
        }
        if (players.contains(bingoPlayer)) {
            return;
        }
        // If the player is already in a team, remove them from that team
        BingoTeam oldTeam = bingoPlayer.getTeam();
        if(oldTeam != null) {
            oldTeam.removePlayer(bingoPlayer);
        }
        players.add(bingoPlayer);
        bingoPlayer.setTeam(this);
    }

    /**
     * Removes a player from the team by their UUID.
     * If the team empties, the team is removed from the ALL_TEAMS list.
     *
     * @param bingoPlayer The bingo player to remove.
     * @return true if the player was on the team and was removed, false otherwise.
     */
    public boolean removePlayer(BingoPlayer bingoPlayer) {
        boolean b = players.remove(bingoPlayer);
        bingoPlayer.setTeam(null);
        if(b && players.size() == 0) {
            ALL_TEAMS.remove(this.name);
        }
        return b;
    }

    /**
     * Checks if a player is a member of this team.
     *
     * @param bingoPlayer The bingo player to check.
     * @return true if the player is on this team, false otherwise.
     */
    public boolean hasPlayer(BingoPlayer bingoPlayer) {
        return players.contains(bingoPlayer);
    }

    /**
     * Gets the number of players on the team.
     *
     * @return The size of the team.
     */
    public int getSize() {
        return players.size();
    }

    public NamedTextColor getColor() {
        return color;
    }

    /**
     * Gets an unmodifiable list of all player UUIDs on this team.
     * This prevents external code from modifying the team's player list directly.
     *
     * @return An unmodifiable list of player UUIDs.
     */
    public List<BingoPlayer> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public void addFoundItem(Material item) {
        foundItems.add(item);
    }

    public Set<Material> getFoundItems() {
        return foundItems;
    }

    public void clearFoundItems() {
        foundItems.clear();
    }

    /**
     * Gets a list of all team members who are currently online.
     *
     * @return A list of online {@link Player} objects.
     */
    public List<Player> getOnlinePlayers() {
        List<Player> onlinePlayers = new ArrayList<>();
        for (BingoPlayer player : players) {
            Player onlinePlayer = player.getOnlinePlayer();
            if (onlinePlayer != null) {
                onlinePlayers.add(onlinePlayer);
            }
        }
        return onlinePlayers;
    }

    /**
     * Sends a message to all online members of the team.
     *
     * @param message The message to send.
     */
    public void broadcastMessage(String message) {
        for (Player player : getOnlinePlayers()) {
            player.sendMessage(message);
        }
    }

    /**
     * Checks if this Team object is equal to another object.
     * Two teams are considered equal if they have the same name (case-sensitive).
     *
     * @param o The object to compare against.
     * @return true if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BingoTeam team = (BingoTeam) o;
        return Objects.equals(name, team.name);
    }

    private static final List<NamedTextColor> TEAM_COLORS = Arrays.asList(
        NamedTextColor.BLUE,
        NamedTextColor.GREEN,
        NamedTextColor.RED,
        NamedTextColor.YELLOW
    );
    private static int nextColorIdx = 0;
}
