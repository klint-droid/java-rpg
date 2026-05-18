package game;
import java.util.ArrayList;

import characters.Character;
import enemies.Enemy;
import inventory.Inventory;

public class SaveData {
    private double currentWave;
    private double gold;
    private ArrayList<Character> players;
    private ArrayList<Enemy> enemies;
    private Inventory inventory;

    public SaveData(double currentWave, double gold, ArrayList<Character> players, ArrayList<Enemy> enemies, Inventory inventory) {
        this.currentWave = currentWave;
        this.gold = gold;
        this.players = players;
        this.enemies = enemies;
        this.inventory = inventory;
    }

    public double getCurrentWave() {
        return currentWave;
    }

    public double getGold() {
        return gold;
    }

    public ArrayList<Character> getPlayers() {
        return players;
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public Inventory getInventory() {
        return inventory;
    }
}

