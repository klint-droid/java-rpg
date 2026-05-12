import java.util.ArrayList;
public class GameManager {
    private ArrayList<Character> players;
    private Inventory inventory;
    private int currentWave;
    private int enemiesDefeated;
    private int gold;

    public GameManager(){
        players = new ArrayList<>();
        inventory = new Inventory();
        inventory.addStarterItems();
        currentWave = 1;
        enemiesDefeated = 0;
        gold = 100;
    }

    private void createParty(){
        players.add(new Warrior("Rex"));
        players.add(new Mage("Klint"));
        players.add(new Archer("Kent"));
    }

    public void startGame(){
        createParty();

        while(currentWave <= 4 && hasLivingPlayers()){
            System.out.println("====== Wave " + currentWave + " =======" + "\n");
            ArrayList<Enemy> enemies = createWave(currentWave);

            BattleSystem battle = new BattleSystem(players, enemies, inventory);

            battle.startBattle();

            enemiesDefeated += enemies.size();

            if(hasLivingPlayers()){
                System.out.println("\nWave " + currentWave + " complete! Enemies defeated: " + enemiesDefeated + "\n");
                gold += 100;
                System.out.println("Gold: 100 added\n");

                Shop shop = new Shop(inventory, gold);
                gold = shop.openShop();

                SaveManager saveManager = new SaveManager();
                saveManager.saveGame(currentWave, gold, players, inventory);
                currentWave++;
            }       
        }

        System.out.println("Game Over!");
        displayFinalResult();
    }

    private ArrayList<Enemy> createWave(int wave){
        ArrayList<Enemy> enemies = new ArrayList<>();

        switch (wave) {
            case 1:
                enemies.add(new Enemy("Goblin", "Goblin", 80, 80, 5));
                break;
            case 2:
                enemies.add(new Enemy("Orc", "Orc", 120, 30, 10));
                enemies.add(new Enemy("Goblin Archer", "Goblin", 70, 25, 5));
                break;
            case 3:
                enemies.add(new Enemy("Dark Mage", "Dark Mage", 100, 40, 8));
                break;
            case 4:
                enemies.add(new Enemy("Dragon", "Boss", 250, 50, 20));
                break;
            default:
                break;
        }

        return enemies;
    }

    private boolean hasLivingPlayers() {
        for (Character player : players) {
            if (player.isAlive()) {
                return true;
            }
        }
        return false;
    }

    private void displayFinalResult(){
        System.out.println("==== GAME OVER ====" + "\n");
        
        if(hasLivingPlayers()){
            System.out.println("VICTORY");
        } else {
            System.out.println("DEFEAT");
        }

        System.out.println("Waves Cleared: " + (currentWave - 1));
        System.out.println("Enemies Defeated: " + enemiesDefeated);
    }
    
    public void loadSaveGame(SaveData saveData){
        currentWave = saveData.getCurrentWave();
        gold = saveData.getGold();
        players = saveData.getPlayers();
        inventory = saveData.getInventory();
    }

    public void startLoadedGame(){
        System.out.println("Loading saved game...");
        startGame();
    }
}
