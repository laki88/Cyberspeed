package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.util.*;

public class ScratchGame {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Random random = new Random();

    static {
        // Configure ObjectMapper to handle snake_case to camelCase conversion
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    }

    public static void main(String[] args) throws Exception {
        // Validate command-line arguments
        if (args.length != 4 || !args[0].equals("--config") || !args[2].equals("--betting-amount")) {
            System.out.println("Usage: java -jar scratch-game.jar --config <config_file> --betting-amount <amount>");
            return;
        }

        String configFilePath = args[1];
        double betAmount = Double.parseDouble(args[3]);

        // Parse configuration
        Config config = objectMapper.readValue(new File(configFilePath), Config.class);

        // Generate matrix
        String[][] matrix = generateMatrix(config);

        // Count standard symbols
        Map<String, Integer> symbolCounts = countSymbols(matrix, config);

        // Determine applied winning combinations
        Map<String, List<String>> appliedWinningCombinations = getAppliedWinningCombinations(symbolCounts, config);

        // Calculate reward before bonus
        double totalRewardBeforeBonus = calculateTotalRewardBeforeBonus(appliedWinningCombinations, config, betAmount);

        // Find bonus symbol
        String bonusSymbol = findBonusSymbol(matrix, config);

        // Calculate final reward with bonus
        double finalReward = calculateFinalReward(totalRewardBeforeBonus, bonusSymbol, config);

        // Generate output JSON
        ObjectNode output = objectMapper.createObjectNode();
        output.put("matrix", objectMapper.valueToTree(matrix));
        output.put("reward", finalReward);

        if (finalReward > 0) {
            ObjectNode appliedWinNode = objectMapper.createObjectNode();
            for (Map.Entry<String, List<String>> entry : appliedWinningCombinations.entrySet()) {
                appliedWinNode.put(entry.getKey(), objectMapper.valueToTree(entry.getValue()));
            }
            output.put("applied_winning_combinations", appliedWinNode);

            if (!"MISS".equals(bonusSymbol)) {
                output.put("applied_bonus_symbol", bonusSymbol);
            }
        }

        // Print formatted JSON output
        System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(output));
    }

    /** Generate the game matrix with standard symbols and one bonus symbol */
    public static String[][] generateMatrix(Config config) {
        int rows = config.getRows();
        int columns = config.getColumns();
        String[][] matrix = new String[rows][columns];

        // Default probability for cells not specified
        Map<String, Integer> defaultProb = config.getProbabilities().getStandardSymbols().get(0).getSymbols();

        // Fill matrix with standard symbols
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                Map<String, Integer> prob = getProbabilityForCell(config, i, j);
                matrix[i][j] = selectSymbol(prob != null ? prob : defaultProb);
            }
        }

        // Place one bonus symbol randomly
        int bonusRow = random.nextInt(rows);
        int bonusCol = random.nextInt(columns);
        Map<String, Integer> bonusProb = config.getProbabilities().getBonusSymbols().getSymbols();
        matrix[bonusRow][bonusCol] = selectSymbol(bonusProb);

        return matrix;
    }

    /** Get probability distribution for a specific cell */
    private static Map<String, Integer> getProbabilityForCell(Config config, int row, int col) {
        for (StandardSymbolProbability prob : config.getProbabilities().getStandardSymbols()) {
            if (prob.getRow() == row && prob.getColumn() == col) {
                return prob.getSymbols();
            }
        }
        return null;
    }

    /** Select a symbol based on probability distribution */
    private static String selectSymbol(Map<String, Integer> prob) {
        int total = prob.values().stream().mapToInt(Integer::intValue).sum();
        int rand = random.nextInt(total);
        int cumulative = 0;
        for (Map.Entry<String, Integer> entry : prob.entrySet()) {
            cumulative += entry.getValue();
            if (rand < cumulative) {
                return entry.getKey();
            }
        }
        return null; // Should not happen if probabilities are valid
    }

    /** Count occurrences of standard symbols in the matrix */
    public static Map<String, Integer> countSymbols(String[][] matrix, Config config) {
        Map<String, Integer> counts = new HashMap<>();
        for (String[] row : matrix) {
            for (String symbol : row) {
                if (config.getSymbols().containsKey(symbol) && "standard".equals(config.getSymbols().get(symbol).getType())) {
                    counts.put(symbol, counts.getOrDefault(symbol, 0) + 1);
                }
            }
        }
        return counts;
    }

    /** Determine applied winning combinations for each symbol */
    public static Map<String, List<String>> getAppliedWinningCombinations(Map<String, Integer> symbolCounts, Config config) {
        Map<String, List<String>> applied = new HashMap<>();
        for (Map.Entry<String, Integer> entry : symbolCounts.entrySet()) {
            String symbol = entry.getKey();
            int count = entry.getValue();
            String bestWin = getBestSameSymbolWinCombination(count, config);
            if (bestWin != null) {
                applied.put(symbol, Collections.singletonList(bestWin));
            }
        }
        return applied;
    }

    /** Find the best "same_symbols" winning combination for a symbol count */
    private static String getBestSameSymbolWinCombination(int count, Config config) {
        String best = null;
        int maxCount = 0;
        for (Map.Entry<String, WinCombination> winEntry : config.getWinCombinations().entrySet()) {
            WinCombination win = winEntry.getValue();
            if ("same_symbols".equals(win.getWhen()) && count >= win.getCount() && win.getCount() > maxCount) {
                maxCount = win.getCount();
                best = winEntry.getKey();
            }
        }
        return best;
    }

    /** Calculate total reward before applying bonus */
    public static double calculateTotalRewardBeforeBonus(Map<String, List<String>> appliedWinningCombinations, Config config, double betAmount) {
        double total = 0;
        for (Map.Entry<String, List<String>> entry : appliedWinningCombinations.entrySet()) {
            String symbol = entry.getKey();
            String winCombination = entry.getValue().get(0); // Only one per group for now
            double symbolMultiplier = config.getSymbols().get(symbol).getRewardMultiplier();
            double winMultiplier = config.getWinCombinations().get(winCombination).getRewardMultiplier();
            total += betAmount * symbolMultiplier * winMultiplier;
        }
        return total;
    }

    /** Find the bonus symbol in the matrix */
    private static String findBonusSymbol(String[][] matrix, Config config) {
        for (String[] row : matrix) {
            for (String symbol : row) {
                if (config.getSymbols().containsKey(symbol) && "bonus".equals(config.getSymbols().get(symbol).getType())) {
                    return symbol;
                }
            }
        }
        return null; // Should not happen
    }

    /** Calculate final reward with bonus applied if applicable */
    public static double calculateFinalReward(double totalRewardBeforeBonus, String bonusSymbol, Config config) {
        if (totalRewardBeforeBonus == 0 || bonusSymbol == null) {
            return 0;
        }
        Symbol bonus = config.getSymbols().get(bonusSymbol);
        if ("multiply_reward".equals(bonus.getImpact())) {
            return totalRewardBeforeBonus * bonus.getRewardMultiplier();
        } else if ("extra_bonus".equals(bonus.getImpact())) {
            return totalRewardBeforeBonus + bonus.getExtra();
        } else { // "miss" or invalid impact
            return totalRewardBeforeBonus;
        }
    }
}