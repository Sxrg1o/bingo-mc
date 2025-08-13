package com.bingaso.bingo.team;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.bingaso.bingo.card.quest.BingoQuest;
import com.bingaso.bingo.player.BingoPlayer;

/**
 * Manages bingo teams, providing functionality to create, modify, and organize teams.
 * 
 * This class handles team creation, player assignment, team size limits, and provides
 * access to team-specific operations. It maintains the relationship between players
 * and their teams, ensuring data consistency throughout the game.
 * 
 * @since 1.0
 */
public class BingoTeamManager {

    /**
     * Thrown when attempting to create a team with a name that already exists.
     * 
     * @since 1.0
     */
    public static class TeamNameAlreadyExistsException extends Exception {
        /**
         * Constructs a new TeamNameAlreadyExistsException with the specified message.
         * 
         * @param message The detail message explaining the exception
         */
        public TeamNameAlreadyExistsException(String message) {
            super(message);
        }
    }

    /**
     * Thrown when an attempt is made to add a player to a team that is already full.
     * 
     * @since 1.0
     */
    public static class MaxPlayersException extends Exception {
        /**
         * Constructs a new MaxPlayersException with the specified message.
         * 
         * @param message The detail message explaining the exception
         */
        public MaxPlayersException(String message) {
            super(message);
        }
    }
    
    private final HashMap<String, BingoTeam> nameBingoTeamMap = new HashMap<>();
    private final HashMap<BingoPlayer, BingoTeam> bingoPlayerBingoTeamMap = new HashMap<>();
    private int maxTeamSize = 1;

    /**
     * Constructs a new BingoTeamManager with default settings.
     * 
     * @since 1.0
     */
    public BingoTeamManager() {}

    /**
     * Creates a new bingo team with the specified name.
     * 
     * @param name The name for the new team
     * @return The newly created BingoTeam
     * @throws TeamNameAlreadyExistsException If a team with this name already exists
     * @since 1.0
     */
    public BingoTeam createBingoTeam(String name) throws TeamNameAlreadyExistsException {
        BingoTeam bingoTeam = new BingoTeam(name);
        if(nameBingoTeamMap.containsKey(bingoTeam.getName())) {
            throw new TeamNameAlreadyExistsException("A team with the name '" + bingoTeam.getName() + "' already exists.");
        }
        nameBingoTeamMap.put(bingoTeam.getName(), bingoTeam);
        return bingoTeam;
    }

    // TODO: Add docs
    public void removeBingoTeam(String name) {
        BingoTeam bingoTeam = nameBingoTeamMap.get(name);
        if(bingoTeam == null) return;
        for(BingoPlayer player : bingoTeam.getPlayers()) {
            bingoPlayerBingoTeamMap.remove(player);
        }
        nameBingoTeamMap.remove(name);
    }

    /**
     * Sets the maximum size of the teams.
     * 
     * When the team size is changed, all existing teams are cleared
     * as the size restriction has changed.
     * 
     * @param newMaxSize The new maximum team size
     * @since 1.0
     */
    public void setMaxTeamSize(int newMaxSize) {
        maxTeamSize = newMaxSize;
        // Deletes all teams as size has changed
        clear();
    }

    /**
     * Gets the maximum size of the teams.
     * 
     * @return The current maximum team size
     * @since 1.0
     */
    public int getMaxTeamSize() {
        return maxTeamSize;
    }

    /**
     * Get all teams.
     * 
     * @return An immutable list of all teams created
     * @since 1.0
     */
    public List<BingoTeam> getTeams() {
        return List.copyOf(nameBingoTeamMap.values());
    }

    /**
     * Get a team by its name.
     * 
     * @param name The name of the team
     * @return The team with the given name, or null if not found
     * @since 1.0
     */
    public BingoTeam getTeamByName(String name) {
        return nameBingoTeamMap.get(name);
    }

    /**
     * Checks if a team name is already taken.
     * 
     * @param name The name to check
     * @return true if the name is available, false if it's already taken
     * @since 1.0
     */
    public boolean isTeamNameAvailable(String name) {
        return nameBingoTeamMap.get(name) == null;
    }

    /**
     * Gets the next team in the collection relative to this team.
     * 
     * @param bingoTeam The team to evaluate over 
     * @return The next team in the collection, or the first if this team is the last
     * @since 1.0
     */
    public BingoTeam getNextTeam(BingoTeam bingoTeam) {
        List<BingoTeam> teamList = new ArrayList<>(nameBingoTeamMap.values());
        int currentIndex = teamList.indexOf(bingoTeam);
        if(currentIndex == teamList.size() - 1) {
            return teamList.get(0);
        }
        return teamList.get(currentIndex + 1);
    }

    /**
     * Gets the previous team in the collection relative to this team.
     * 
     * @param bingoTeam The team to evaluate over 
     * @return The previous team in the collection, or the last if this team is the first
     * @since 1.0
     */
    public BingoTeam getPreviousTeam(BingoTeam bingoTeam) {        
        List<BingoTeam> teamList = new ArrayList<>(nameBingoTeamMap.values());
        int currentIndex = teamList.indexOf(bingoTeam);
        if (currentIndex == 0) {
            return teamList.get(teamList.size() - 1);
        }
        return teamList.get(currentIndex - 1);
    }

    /**
     * Changes the name of an existing team.
     * 
     * @param bingoTeam The team to rename
     * @param newName The new name for the team
     * @throws TeamNameAlreadyExistsException If a team with the new name already exists
     * @since 1.0
     */
    public void changeTeamName(BingoTeam bingoTeam, String newName) throws TeamNameAlreadyExistsException {
        if (!isTeamNameAvailable(newName)) {
            throw new TeamNameAlreadyExistsException("A team with the name '" + newName + "' already exists.");
        }
        nameBingoTeamMap.remove(bingoTeam.getName());
        bingoTeam.setName(newName);
        nameBingoTeamMap.put(newName, bingoTeam);
    }

    /**
     * Adds a player to a team.
     * 
     * If the player is already in another team, they will be removed
     * from that team first.
     * 
     * @param bingoPlayer The player to add
     * @param bingoTeam The team to add the player to
     * @throws MaxPlayersException If the team is already at maximum capacity
     * @since 1.0
     */
    public void addPlayerToTeam(BingoPlayer bingoPlayer, BingoTeam bingoTeam) throws MaxPlayersException{
        if(bingoTeam.getSize() + 1 > maxTeamSize) {
            throw new MaxPlayersException("Team is already full");
        }

        // If the player is already in a team, remove them from that team
        BingoTeam oldBingoTeam = bingoPlayerBingoTeamMap.get(bingoPlayer);
        if(oldBingoTeam != null) {
            oldBingoTeam.removePlayer(bingoPlayer);
        }
        bingoTeam.addPlayer(bingoPlayer);
        bingoPlayerBingoTeamMap.put(bingoPlayer, bingoTeam);
    }

    /**
     * Removes a player from their current team.
     * 
     * If the team becomes empty after removing the player, the team is deleted.
     * 
     * @param bingoPlayer The player to remove from their team
     * @since 1.0
     */
    public void removePlayerFromTeam(BingoPlayer bingoPlayer) {
        BingoTeam oldBingoTeam = bingoPlayerBingoTeamMap.get(bingoPlayer);
        if(oldBingoTeam == null) return;

        oldBingoTeam.removePlayer(bingoPlayer);
        bingoPlayerBingoTeamMap.remove(bingoPlayer);

        // remove team if it remains empty after player removal
        if(oldBingoTeam.getSize() == 0) {
            nameBingoTeamMap.remove(oldBingoTeam.getName());
        }
    }

    /**
     * Gets the team that a player is currently on.
     * 
     * @param bingoPlayer The player to look up
     * @return The team the player is on, or null if they're not on any team
     * @since 1.0
     */
    public BingoTeam getPlayerTeam(BingoPlayer bingoPlayer) {
        return bingoPlayerBingoTeamMap.get(bingoPlayer);
    }

    /**
     * Adds a completed quest to a team's collection.
     * 
     * @param bingoTeam The BingoTeam that completed the quest.
     * @param bingoQuest The BingoQuest that was completed.
     * @param instant The instant when the BingoQuest was completed.
     * @since 2.0
     */
    public void addCompletedItemToTeam(BingoTeam bingoTeam, BingoQuest bingoQuest, Instant instant) {
        bingoTeam.addCompletedItem(bingoQuest, instant);
    }

    /**
     * Clears all completed items from a team's collection.
     * 
     * This is typically used when starting a new game or resetting team progress.
     * 
     * @param bingoTeam The BingoTeam to clear completed items for
     * @since 1.0
     */
    public void clearTeamCompletedItems(BingoTeam bingoTeam) {
        bingoTeam.clearCompletedItems();
    }

    /**
     * Clears all teams and player-team mappings.
     * 
     * @since 1.0
     */
    public void clear() {
        bingoPlayerBingoTeamMap.clear();
        nameBingoTeamMap.clear();
    }

    /**
     * Checks if there are no teams currently managed.
     * 
     * @return true if no teams exist, false otherwise
     * @since 1.0
     */
    public boolean isEmpty() {
        return nameBingoTeamMap.isEmpty();
    }
}
