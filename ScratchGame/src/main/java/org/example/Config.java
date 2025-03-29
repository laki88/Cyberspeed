package org.example;

import java.util.Map;

class Config {
    private int columns;
    private int rows;
    private Map<String, Symbol> symbols;
    private Probabilities probabilities;
    private Map<String, WinCombination> winCombinations;

    // Getters and setters
    public int getColumns() { return columns; }
    public void setColumns(int columns) { this.columns = columns; }
    public int getRows() { return rows; }
    public void setRows(int rows) { this.rows = rows; }
    public Map<String, Symbol> getSymbols() { return symbols; }
    public void setSymbols(Map<String, Symbol> symbols) { this.symbols = symbols; }
    public Probabilities getProbabilities() { return probabilities; }
    public void setProbabilities(Probabilities probabilities) { this.probabilities = probabilities; }
    public Map<String, WinCombination> getWinCombinations() { return winCombinations; }
    public void setWinCombinations(Map<String, WinCombination> winCombinations) { this.winCombinations = winCombinations; }
}
