package com.bingaso.bingo.team;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.bingaso.bingo.player.BingoPlayer;

import net.kyori.adventure.text.format.TextColor;

/**
 * Repository for storing and retrieving bingo teams and their player assignments.
 * 
 * This class serves as a data access layer for BingoTeam entities, maintaining
 * referential integrity between teams and players. It ensures data consistency
 * by automatically handling player-team relationships and preventing invalid states
 * such as players being assigned to multiple teams simultaneously.
 * 
 * @since 1.0
 */
public class BingoTeamRepositoryInMemory
    implements Serializable, BingoTeamRepository {
    
    private final HashMap<String, BingoTeam> nameTeams = new HashMap<>();
    private final HashMap<TextColor, BingoTeam> colorTeams = new HashMap<>();
    private final HashMap<BingoPlayer, BingoTeam> playersTeams = new HashMap<>();

    /**
     * Constructs a new BingoTeamRepository with empty storage.
     * 
     * @since 1.0
     */
    public BingoTeamRepositoryInMemory() {}

    /**
     * Saves a new bingo team to the repository.
     * 
     * @param bingoTeam The {@link BingoTeam} to save.
     * @throws TeamNameAlreadyExistsException If a team with this name already  
     * exists.
     * @throws ColorAlreadyExistsException If a team with this color already
     * exists.
     * @since 1.0
     */
    @Override
    public void save(BingoTeam bingoTeam)
        throws TeamNameAlreadyExistsException, ColorAlreadyExistsException {
        if(existsByName(bingoTeam.getName())) {
            throw new TeamNameAlreadyExistsException(bingoTeam.getName());
        }
        if(existsByColor(bingoTeam.getColor())) {
            throw new ColorAlreadyExistsException(bingoTeam.getColor());
        }
        nameTeams.put(bingoTeam.getName(), bingoTeam);
        colorTeams.put(bingoTeam.getColor(), bingoTeam);
    }

    /**
     * Removes a {@link BingoTeam} from the repository and cleans up all
     * player assignments to maintain data integrity.
     * 
     * @param bingoTeam The {@link BingoTeam} to remove.
     * @return True if the team was removed, false if it didn't exist.
     * @since 1.0
     */
    @Override
    public boolean remove(BingoTeam bingoTeam) {
        boolean removed_name = nameTeams.remove(bingoTeam.getName()) != null;
        boolean removed_color = colorTeams.remove(bingoTeam.getColor()) != null;
        if (removed_name || removed_color) {
            playersTeams.entrySet().removeIf(
                entry -> entry.getValue().equals(bingoTeam)
            );
        }
        return removed_name || removed_color;
    }

    /**
     * Find all {@link BingoTeam}s in the repository.
     * 
     * @return An immutable list of all {@link BingoTeam}s.
     * @since 1.0
     */
    @Override
    public List<BingoTeam> findAll() {
        return List.copyOf(nameTeams.values());
    }

    /**
     * Finds a {@link BingoTeam} by its name.
     * 
     * @param name The name of the {@link BingoTeam}.
     * @return The {@link BingoTeam} with the given name, or null if not found.
     * @since 1.0
     */
    @Override
    public BingoTeam findByName(String name) {
        return nameTeams.get(name);
    }

    /**
     * Checks if a team with the specified name exists in the repository.
     * 
     * @param name The name to check.
     * @return true if a team with this name exists, false otherwise.
     * @since 1.0
     */
    @Override
    public boolean existsByName(String name) {
        return nameTeams.containsKey(name);
    }

    /**
     * Checks if a team name is available (not already taken).
     * 
     * @param name The name to check.
     * @return True if the name is available, false if it's already taken.
     * @since 1.0
     */
    @Override
    public boolean isTeamNameAvailable(String name) {
        return !existsByName(name);
    }

    /**
     * Changes the name of an existing {@link BingoTeam}.
     * 
     * @param bingoTeam The {@link BingoTeam} to rename.
     * @param newName The new name for the {@link BingoTeam}.
     * @throws TeamNameAlreadyExistsException If a {@link BingoTeam} with the
     * new name already exists
     * @since 1.0
     */
    @Override
    public void changeTeamName(BingoTeam bingoTeam, String newName)
        throws TeamNameAlreadyExistsException {
        if (!isTeamNameAvailable(newName)) {
            throw new TeamNameAlreadyExistsException(newName);
        }
        nameTeams.remove(bingoTeam.getName());
        bingoTeam.setName(newName);
        nameTeams.put(newName, bingoTeam);
    }

    /**
     * Finds a {@link BingoTeam} by its color.
     * 
     * @param color The color of the {@link BingoTeam}.
     * @return The {@link BingoTeam} with the given color, or null if not found.
     * @since 1.0
     */
    public BingoTeam findByColor(TextColor color) {
        return colorTeams.get(color);
    }

    /**
     * Checks if a team with the specified color exists in the repository.
     * 
     * @param color The color to check.
     * @return True if a team with this color exists, false otherwise.
     * @since 1.0
     */
    public boolean existsByColor(TextColor color) {
        return colorTeams.containsKey(color);
    }

    /**
     * Checks if a team color is available (not already taken).
     * 
     * @param color The color to check.
     * @return True if the color is available, false if it's already taken.
     * @since 1.0
     */
    public boolean isTeamColorAvailable(TextColor color) {
        return !existsByColor(color);
    }

    /**
     * Changes the color of an existing {@link BingoTeam}.
     * 
     * @param bingoTeam The {@link BingoTeam} to recolor.
     * @param newColor The new color for the {@link BingoTeam}.
     * @throws ColorAlreadyExistsException If a team with this color already
     * exists.
     * @since 1.0
     */
    public void changeTeamColor(BingoTeam bingoTeam, TextColor newColor)
        throws ColorAlreadyExistsException {
        if (!isTeamColorAvailable(newColor)) {
            throw new ColorAlreadyExistsException(newColor);
        }
        colorTeams.remove(bingoTeam.getColor());
        bingoTeam.setColor(newColor);
        colorTeams.put(newColor, bingoTeam);
    }

    /**
     * Assigns a {@link BingoPlayer} to a {@link BingoTeam}, automatically
     * removing them from their previous {@link BingoTeam} to maintain data
     * integrity.
     * 
     * @param bingoPlayer The {@link BingoPlayer} to assign
     * @param bingoTeam The {@link BingoTeam} to assign the {@link BingoPlayer}
     * to.
     * @since 1.0
     */
    @Override
    public void assignPlayerToTeam(
        BingoPlayer bingoPlayer,
        BingoTeam bingoTeam
    ) {
        BingoTeam oldBingoTeam = playersTeams.get(bingoPlayer);
        if(oldBingoTeam != null) {
            oldBingoTeam.removePlayer(bingoPlayer);
        }
        bingoTeam.addPlayer(bingoPlayer);
        playersTeams.put(bingoPlayer, bingoTeam);
    }

    /**
     * Removes a {@link BingoPlayer} from their current {@link BingoTeam}.
     * 
     * @param bingoPlayer The {@link BingoPlayer} to remove from their current
     * {@link BingoTeam}.
     * @return The {@link BingoTeam} the player was removed from, or null if
     * they weren't on any team.
     * @since 1.0
     */
    @Override
    public BingoTeam removePlayerFromTeam(BingoPlayer bingoPlayer) {
        BingoTeam oldBingoTeam = playersTeams.get(bingoPlayer);
        if(oldBingoTeam != null) {
            oldBingoTeam.removePlayer(bingoPlayer);
            playersTeams.remove(bingoPlayer);
        }
        return oldBingoTeam;
    }

    /**
     * Finds the {@link BingoTeam} that a {@link BingoPlayer} is currently
     * assigned to.
     * 
     * @param bingoPlayer The {@link BingoPlayer} to look up
     * @return The {@link BingoTeam} the {@link BingoPlayer} is on, or null if
     * they're not on any team.
     * @since 1.0
     */
    @Override
    public BingoTeam findTeamByPlayer(BingoPlayer bingoPlayer) {
        return playersTeams.get(bingoPlayer);
    }

    /**
     * Checks if a {@link BingoPlayer} is currently assigned to any team.
     * 
     * @param bingoPlayer The {@link BingoPlayer} to check
     * @return true if the player is assigned to a team, false otherwise.
     * @since 1.0
     */
    @Override
    public boolean isPlayerAssigned(BingoPlayer bingoPlayer) {
        return playersTeams.containsKey(bingoPlayer);
    }

    /**
     * Gets the total number of teams in the repository.
     * 
     * @return The number of teams currently stored.
     * @since 1.0
     */
    @Override
    public int getTeamCount() {
        return nameTeams.size();
    }

    /**
     * Gets the total number of players assigned to teams.
     * 
     * @return The number of players currently assigned to any team.
     * @since 1.0
     */
    @Override
    public int getAssignedPlayerCount() {
        return playersTeams.size();
    }

    /**
     * Clears all {@link BingoTeam}s and {@link BingoPlayer}-{@link BingoTeam}
     * mappings.
     * 
     * @since 1.0
     */
    @Override
    public void clear() {
        playersTeams.clear();
        colorTeams.clear();
        nameTeams.clear();
    }

    /**
     * Checks if there are no {@link BingoTeam}s currently stored.
     * 
     * @return True if no teams exist, false otherwise.
     * @since 1.0
     */
    @Override
    public boolean isEmpty() {
        return nameTeams.isEmpty();
    }

    public BingoTeam getPreviousTeam(BingoTeam bingoTeam) {
        List<BingoTeam> teams = new ArrayList<>(nameTeams.values());
        int index = teams.indexOf(bingoTeam);
        if(index == -1) return null;
        if (index > 0) {
            return teams.get(index - 1);
        } else {
            return teams.get(teams.size() - 1);
        }
    }

    public BingoTeam getNextTeam(BingoTeam bingoTeam) {
        List<BingoTeam> teams = new ArrayList<>(nameTeams.values());
        int index = teams.indexOf(bingoTeam);
        if(index == -1) return null;
        if (index < teams.size() - 1) {
            return teams.get(index + 1);
        } else {
            return teams.get(0);
        }
    }
}
