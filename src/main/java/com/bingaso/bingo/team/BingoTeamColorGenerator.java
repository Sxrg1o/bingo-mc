package com.bingaso.bingo.team;

import java.util.Random;
import net.kyori.adventure.text.format.TextColor;

/**
 * Manages the allocation and release of unique colors for BingoTeams.
 * This service ensures that no two teams have the same color by generating
 * random, unique 24-bit RGB colors and tracking which ones are currently in
 * use.
 *
 * @since 1.0
 */
public class BingoTeamColorGenerator {

    private final Random random = new Random();
    private final BingoTeamRepository bingoTeamRepository;
    
    // The upper bound for 24-bit color generation (0xFFFFFF).
    private static final int MAX_COLOR_VALUE = 0xFFFFFF + 1;
    // A safeguard to prevent potential infinite loops, though highly unlikely.
    private static final int MAX_GENERATION_ATTEMPTS = 1000;

    /**
     * Constructor for BingoTeamColorGenerator.
     * @param bingoTeamRepository The repository to check for color
     * availability.
     */
    public BingoTeamColorGenerator(BingoTeamRepository bingoTeamRepository) {
        this.bingoTeamRepository = bingoTeamRepository;
    }

    /**
     * Generates a new, unique, and random 24-bit RGB color.
     * This method repeatedly generates random colors until it finds one that
     * is not currently in use in the {@link BingoTeamRepository}
     *
     * @return A new unique {@link TextColor}.
     * @throws IllegalStateException if a unique color cannot be found after a
     * reasonable number of attempts.
     */
    public TextColor generateRandomColor() {
        int attempts = 0;
        while (attempts < MAX_GENERATION_ATTEMPTS) {
            int randomHex = random.nextInt(MAX_COLOR_VALUE);
            TextColor newColor = TextColor.color(randomHex);
            if(bingoTeamRepository.isTeamColorAvailable(newColor)) {
                return newColor;
            }
            attempts++;
        }
        
        throw new IllegalStateException(
            "Failed to generate a unique team color after " +
            MAX_GENERATION_ATTEMPTS + " attempts."
        );
    }
}