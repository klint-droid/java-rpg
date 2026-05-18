package game;

import java.util.ArrayList;
import characters.Character;
import enemies.Enemy;
import inventory.Inventory;

/**
 * Single source of truth for all mutable game state.
 * Extracted from RpgGameUI, GameManager, and BattleSystem to eliminate
 * duplicated state fields and helper methods (hasLivingPlayers, etc.).
 */
public class GameState {
    private ArrayList<Character> players;
    private ArrayList<Enemy> enemies;
    private Inventory inventory;
    private double currentWave;
    private double enemiesDefeated;
    private double gold;
    private double turnCount;
    private int currentPlayerIndex;

    public GameState() {
        this.players = new ArrayList<>();
        this.enemies = new ArrayList<>();
        this.inventory = new Inventory();
        this.currentWave = 1;
        this.enemiesDefeated = 0;
        this.gold = 0;
        this.turnCount = 1;
        this.currentPlayerIndex = 0;
    }

    // --- Living-entity queries (previously duplicated 3x) ---

    public boolean hasLivingPlayers() {
        for (Character player : players) {
            if (player.isAlive()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasLivingEnemies() {
        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) {
                return true;
            }
        }
        return false;
    }

    public int findNextAlivePlayer(int startIndex) {
        for (int i = startIndex; i < players.size(); i++) {
            if (players.get(i).isAlive()) {
                return i;
            }
        }
        return -1;
    }

    public ArrayList<Enemy> getAliveEnemies() {
        ArrayList<Enemy> alive = new ArrayList<>();
        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) {
                alive.add(enemy);
            }
        }
        return alive;
    }

    // --- Getters and Setters ---

    public ArrayList<Character> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<Character> players) {
        this.players = players;
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public void setEnemies(ArrayList<Enemy> enemies) {
        this.enemies = enemies;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public double getCurrentWave() {
        return currentWave;
    }

    public void setCurrentWave(double currentWave) {
        this.currentWave = currentWave;
    }

    public double getEnemiesDefeated() {
        return enemiesDefeated;
    }

    public void setEnemiesDefeated(double enemiesDefeated) {
        this.enemiesDefeated = enemiesDefeated;
    }

    public void addEnemiesDefeated(double count) {
        this.enemiesDefeated += count;
    }

    public double getGold() {
        return gold;
    }

    public void setGold(double gold) {
        this.gold = gold;
    }

    public void addGold(double amount) {
        this.gold += amount;
    }

    public double getTurnCount() {
        return turnCount;
    }

    public void setTurnCount(double turnCount) {
        this.turnCount = turnCount;
    }

    public void incrementTurn() {
        this.turnCount++;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public void setCurrentPlayerIndex(int currentPlayerIndex) {
        this.currentPlayerIndex = currentPlayerIndex;
    }
}
