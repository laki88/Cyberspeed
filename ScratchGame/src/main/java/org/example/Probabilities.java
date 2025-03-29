package org.example;

import java.util.List;

class Probabilities {
    private List<StandardSymbolProbability> standardSymbols;
    private BonusSymbolProbability bonusSymbols;

    // Getters and setters
    public List<StandardSymbolProbability> getStandardSymbols() { return standardSymbols; }
    public void setStandardSymbols(List<StandardSymbolProbability> standardSymbols) { this.standardSymbols = standardSymbols; }
    public BonusSymbolProbability getBonusSymbols() { return bonusSymbols; }
    public void setBonusSymbols(BonusSymbolProbability bonusSymbols) { this.bonusSymbols = bonusSymbols; }
}
