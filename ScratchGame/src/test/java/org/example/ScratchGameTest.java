package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

class ScratchGameTest {
    private Config mockConfig;

    @BeforeEach
    void setUp() {
        mockConfig = new Config();

        // Set grid size
        mockConfig.setRows(3);
        mockConfig.setColumns(3);

        // Define symbols
        Map<String, Symbol> symbols = new HashMap<>();
        symbols.put("A", new Symbol() {{ setType("standard"); setRewardMultiplier(5.0); }});
        symbols.put("B", new Symbol() {{ setType("standard"); setRewardMultiplier(2.0); }});
        symbols.put("+1000", new Symbol() {{ setType("bonus"); setImpact("extra_bonus"); setExtra(1000); }});
        mockConfig.setSymbols(symbols);

        // Define probabilities
        Probabilities probabilities = new Probabilities();
        List<StandardSymbolProbability> standardSymbolProbabilities = new ArrayList<>();
        StandardSymbolProbability standardProb = new StandardSymbolProbability();
        standardProb.setRow(0);
        standardProb.setColumn(0);
        standardProb.setSymbols(Map.of("A", 50, "B", 50));
        standardSymbolProbabilities.add(standardProb);
        probabilities.setStandardSymbols(standardSymbolProbabilities);

        BonusSymbolProbability bonusProb = new BonusSymbolProbability();
        bonusProb.setSymbols(Map.of("+1000", 100));
        probabilities.setBonusSymbols(bonusProb);

        mockConfig.setProbabilities(probabilities);

        // Define winning combinations
        Map<String, WinCombination> winCombinations = new HashMap<>();
        winCombinations.put("same_symbol_3_times", new WinCombination() {{ setWhen("same_symbols"); setCount(3); setRewardMultiplier(2.0); }});
        mockConfig.setWinCombinations(winCombinations);
    }


    @Test
    void testMatrixGeneration() {
        String[][] matrix = ScratchGame.generateMatrix(mockConfig);
        assertEquals(3, matrix.length);
        assertEquals(3, matrix[0].length);
    }

    @Test
    void testSymbolCounting() {
        String[][] matrix = {
                {"A", "B", "A"},
                {"B", "A", "B"},
                {"A", "B", "+1000"}
        };
        Map<String, Integer> symbolCounts = ScratchGame.countSymbols(matrix, mockConfig);
        assertEquals(4, symbolCounts.get("A"));
        assertEquals(4, symbolCounts.get("B"));
    }

    @Test
    void testWinningCombinations() {
        Map<String, Integer> symbolCounts = new HashMap<>();
        symbolCounts.put("A", 3);
        symbolCounts.put("B", 2);
        Map<String, List<String>> appliedWins = ScratchGame.getAppliedWinningCombinations(symbolCounts, mockConfig);
        assertTrue(appliedWins.containsKey("A"));
        assertEquals("same_symbol_3_times", appliedWins.get("A").get(0));
    }

    @Test
    void testCalculateRewardBeforeBonus() {
        Map<String, List<String>> appliedWins = new HashMap<>();
        appliedWins.put("A", Collections.singletonList("same_symbol_3_times"));
        double reward = ScratchGame.calculateTotalRewardBeforeBonus(appliedWins, mockConfig, 100);
        assertEquals(100 * 5 * 2, reward);
    }

    @Test
    void testBonusApplication() {
        double rewardBeforeBonus = 1000;
        String bonusSymbol = "+1000";
        double finalReward = ScratchGame.calculateFinalReward(rewardBeforeBonus, bonusSymbol, mockConfig);
        assertEquals(2000, finalReward);
    }
}
