import java.util.ArrayList;
public class SaveData {
    private int currentWave;
    private int gold;
    private ArrayList<Character> players;
    private Inventory inventory;

    public SaveData(int currentWave, int gold, ArrayList<Character> players, Inventory inventory) {
        this.currentWave = currentWave;
        this.gold = gold;
        this.players = players;
        this.inventory = inventory;
    }

    public int getCurrentWave() {
        return currentWave;
    }

    public int getGold() {
        return gold;
    }

    public ArrayList<Character> getPlayers() {
        return players;
    }

    public Inventory getInventory() {
        return inventory;
    }
}
