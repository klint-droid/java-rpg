# Simple RPG Game with Java Swing and OOP

A classic 2D top-down RPG built with Java Swing. Players can explore a world, fight monsters, level up, and manage inventory.

## Features

-   **Character Classes**: Warrior, Mage, Archer
-   **Enemies**: Varied monsters with different stats
-   **Combat System**: Turn-based battles with attack, skill, defend, and flee options
-   **Leveling**: Gain EXP and increase stats upon leveling up
-   **Inventory System**: Manage items including health, mana, mega, and revive potions
-   **Shop**: Buy and sell items
-   **Save/Load**: Persist game progress to JSON files
-   **AI**: Simple enemy AI to handle battles

## Technical Details

-   **Language**: Java 17
-   **GUI**: Java Swing
-   **Structure**: Object-Oriented Programming with proper separation of concerns
-   **Serialization**: JSON format for save data

## Getting Started

### Prerequisites

-   **Java Development Kit (JDK)**: Version 17 or higher

### Installation

No special installation is required. Ensure you have Java 17 installed.

### Building

1.  Compile the source code:
    ```powershell
    javac -d out (Get-ChildItem -Recurse -Filter *.java src).FullName
    ```

### Running

After successful compilation, run the game:

```powershell
java -cp out main.Main  
```

## Usage

The game starts with a main menu where you can:

-   Start a new game
-   Load a saved game
-   Exit

### Controls

-   **Interaction**: Click buttons in the UI

## Assets

- **UI Buttons**: `attack.png`, `defend.png`, `flee.png`, `skill.png`, `item.png`, `save.png`, `shop.png`
- **Game Icon**: `icon.png`
- **Battle Animation**: `battle.gif`

## License

[MIT](LICENSE)