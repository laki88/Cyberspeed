# Scratch Game

## Overview
Scratch Game is a simple game where a grid is generated with different symbols based on predefined probabilities. The game calculates rewards based on matching symbols and applies bonuses when applicable. This project includes both the game logic and unit tests.

## Features
- Generates a grid with randomly assigned symbols based on probability settings.
- Calculates winnings based on predefined symbol multipliers and winning combinations.
- Applies bonus symbols that modify the final reward.
- Supports configuration via a JSON file.

## Installation
### Prerequisites
Ensure you have the following installed:
- Java 17 or later
- Maven (for dependency management)

### Setup
1. Clone the repository:
   ```sh
   git clone <repository-url>
   cd ScratchGame
   ```
2. Compile the project:
   ```sh
   mvn clean install
   ```
3. Run the game:
   ```sh
   java -jar target/scratch-game.jar --config <config_file> --betting-amount <amount>
   ```
   Example:
   ```sh
   java -jar target/scratch-game.jar --config config.json --betting-amount 100
   ```

## Configuration
The game is configured via a JSON file that specifies:
- Grid size (rows, columns)
- Symbol types, rewards, and probabilities
- Winning conditions and multipliers
- Bonus symbols and their effects

Example `config.json`:
```json
{
  "rows": 3,
  "columns": 3,
  "symbols": {
    "A": { "type": "standard", "rewardMultiplier": 5.0 },
    "B": { "type": "standard", "rewardMultiplier": 2.0 },
    "+1000": { "type": "bonus", "impact": "extra_bonus", "extra": 1000 }
  },
  "probabilities": {
    "standardSymbols": [
      { "row": 0, "column": 0, "symbols": { "A": 50, "B": 50 } }
    ],
    "bonusSymbols": { "symbols": { "+1000": 100 } }
  },
  "winCombinations": {
    "same_symbol_3_times": { "when": "same_symbols", "count": 3, "rewardMultiplier": 2.0 }
  }
}
```

## Running Tests
To run unit tests:
```sh
mvn test
```

## License
This project is licensed under the Apache 2.0 License.

