package com.bingaso.bingo.game;

import com.bingaso.bingo.model.BingoCard;
import com.bingaso.bingo.model.BingoItem;
import com.bingaso.bingo.model.DifficultyLevel;
import com.bingaso.bingo.utils.ItemRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.bukkit.Material;

/**
 * Generates Bingo cards with items of varying difficulty levels.
 * This class handles the weighted random selection of items based on
 * difficulty scores to create balanced Bingo cards for gameplay.
 */
public class CardGenerator {

    private final ItemRepository itemRepository;
    private final Random random = new Random();
    private final Map<
        DifficultyLevel,
        Map<Integer, Integer>
    > difficultyWeights = new HashMap<>();

    /**
     * Creates a new CardGenerator with the specified item repository.
     * Initializes the difficulty weight mappings for item selection.
     *
     * @param itemRepository The repository containing all available items
     */
    public CardGenerator(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
        initializeWeights();
    }

    /**
     * Initializes the weight mappings for each difficulty level.
     * These weights determine how likely items of different scores (1-5)
     * are to be selected for each difficulty level.
     * Higher weights mean higher probability of selection.
     */
    private void initializeWeights() {
        // EASY
        Map<Integer, Integer> easyWeights = new HashMap<>();
        easyWeights.put(1, 80);
        easyWeights.put(2, 40);
        easyWeights.put(3, 5);
        easyWeights.put(4, 0);
        easyWeights.put(5, 0);
        difficultyWeights.put(DifficultyLevel.EASY, easyWeights);

        // MEDIUM
        Map<Integer, Integer> mediumWeights = new HashMap<>();
        mediumWeights.put(1, 15);
        mediumWeights.put(2, 60);
        mediumWeights.put(3, 45);
        mediumWeights.put(4, 5);
        mediumWeights.put(5, 0);
        difficultyWeights.put(DifficultyLevel.MEDIUM, mediumWeights);

        // HARD
        Map<Integer, Integer> hardWeights = new HashMap<>();
        hardWeights.put(1, 2);
        hardWeights.put(2, 10);
        hardWeights.put(3, 50);
        hardWeights.put(4, 30);
        hardWeights.put(5, 3);
        difficultyWeights.put(DifficultyLevel.HARD, hardWeights);

        // EXTREME
        Map<Integer, Integer> extremeWeights = new HashMap<>();
        extremeWeights.put(1, 0);
        extremeWeights.put(2, 0);
        extremeWeights.put(3, 40);
        extremeWeights.put(4, 60);
        extremeWeights.put(5, 10);
        difficultyWeights.put(DifficultyLevel.EXTREME, extremeWeights);
    }

    /**
     * Generates a Bingo card with 25 items based on the specified difficulty level.
     * Uses weighted random selection to choose items according to the difficulty weights.
     * The selected materials are then shuffled and arranged into a Bingo card.
     *
     * @param difficulty The difficulty level that determines item selection weights
     * @return A new BingoCard containing 25 randomly selected items
     */
    public BingoCard generateCard(DifficultyLevel difficulty) {
        List<ItemRepository.ItemData> sourceItems =
            itemRepository.getAllItems();
        Set<Material> selectedMaterials = new HashSet<>();
        Map<Integer, Integer> weights = difficultyWeights.get(difficulty);

        while (selectedMaterials.size() < 25) {
            int totalWeight = 0;
            for (ItemRepository.ItemData item : sourceItems) {
                totalWeight += weights.getOrDefault(item.score, 0);
            }

            if (totalWeight == 0) {
                return new BingoCard();
            }

            int randomNumber = random.nextInt(totalWeight);

            ItemRepository.ItemData chosenItem = null;
            for (ItemRepository.ItemData item : sourceItems) {
                int itemWeight = weights.getOrDefault(item.score, 0);
                if (randomNumber < itemWeight) {
                    chosenItem = item;
                    break;
                }
                randomNumber -= itemWeight;
            }

            if (chosenItem != null) {
                selectedMaterials.add(Material.getMaterial(chosenItem.name));
            }
        }

        List<BingoItem> finalItems = new ArrayList<>();
        List<Material> materialsToPlace = new ArrayList<>(selectedMaterials);
        Collections.shuffle(materialsToPlace);

        for (int i = 0; i < 25; i++) {
            finalItems.add(new BingoItem(materialsToPlace.get(i)));
        }

        return new BingoCard(finalItems);
    }
}
