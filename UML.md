classDiagram
    class Main
    class GameManager
    class GameController
    class GameState
    class SaveManager
    class SaveData
    class WaveFactory
    class CharacterFactory
    class BattleSystem
    class BattleController
    class BattleAction
    class AttackAction
    class DefendAction
    class SkillAction
    class ConsoleUI
    class RpgGameUI
    class ShopFrame
    class InventoryFrame
    class PartySheetFrame
    class CharacterSheetFrame
    class CardRenderer
    class AssetManager
    class Shop
    class ShopService
    class InventoryService
    class Inventory
    class Item
    class HealthPotion
    class MegaPotion
    class ManaPotion
    class RevivePotion
    class StackedItem
    class EmptyInventoryException
    class Character
    class Warrior
    class Mage
    class Archer
    class Enemy
    class EnemyAI
    class AggressiveAI
    class GameConstants
    class CharacterType

    Main --> GameManager
    Main --> RpgGameUI
    GameManager --> GameState
    GameManager --> BattleSystem
    GameManager --> ConsoleUI
    GameManager --> CharacterFactory
    GameManager --> Shop
    GameManager --> SaveManager
    GameManager --> WaveFactory

    GameController --> GameState
    GameController --> SaveManager
    GameController --> WaveFactory
    GameController --> CharacterType

    BattleController --> GameState
    BattleController --> InventoryService
    BattleController --> BattleAction
    BattleController --> BattleController.BattleEventListener

    BattleSystem --> Inventory
    BattleSystem --> ConsoleUI
    BattleSystem --> Character
    BattleSystem --> Enemy
    BattleSystem --> BattleAction

    BattleAction <|-- AttackAction
    BattleAction <|-- DefendAction
    BattleAction <|-- SkillAction

    Character <|-- Warrior
    Character <|-- Mage
    Character <|-- Archer
    Character <|-- Enemy

    Enemy --> EnemyAI
    AggressiveAI --|> EnemyAI

    InventoryService --> Inventory
    Inventory --> Item
    Item <|-- HealthPotion
    Item <|-- MegaPotion
    Item <|-- ManaPotion
    Item <|-- RevivePotion
    InventoryService --> StackedItem
    InventoryFrame --> InventoryService
    InventoryFrame --> AssetManager
    InventoryFrame --> RpgGameUI

    ShopFrame --> GameState
    ShopFrame --> AssetManager
    ShopFrame --> RpgGameUI
    ShopFrame --> ShopService

    RpgGameUI --> GameController
    RpgGameUI --> BattleController
    RpgGameUI --> InventoryService
    RpgGameUI --> AssetManager
    RpgGameUI --> CardRenderer
    RpgGameUI --> GameState
    RpgGameUI --> ShopFrame
    RpgGameUI --> InventoryFrame
    RpgGameUI --> PartySheetFrame
    RpgGameUI --> CharacterSheetFrame

    CharacterFactory --> Character
    WaveFactory --> Enemy
    SaveManager --> SaveData
    SaveData --> GameState

    GameState --> Character
    GameState --> Enemy
    GameState --> Inventory
    GameState --> SaveData

    ShopService --> Shop
    ShopService --> GameState

    GameConstants <.. GameManager
    GameConstants <.. BattleSystem
    GameConstants <.. Character
    GameConstants <.. Enemy
    GameConstants <.. ShopFrame
    GameConstants <.. ShopService
