package game;
import java.util.ArrayList;

import characters.Character;
import inventory.Inventory;
public class SaveData {
    private double currentWave;
    private double gold;
    private ArrayList<Character> players;
    private Inventory inventory;

    public SaveData(double currentWave, double gold, ArrayList<Character> players, Inventory inventory) {
        this.currentWave = currentWave;
        this.gold = gold;
        this.players = players;
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

    public Inventory getInventory() {
        return inventory;
    }
}

