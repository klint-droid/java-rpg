import java.util.ArrayList;
import java.util.Scanner;

public class GameManager {
    private ArrayList<Character> players;
    private Inventory inventory;
    private int currentWave;
    private int enemiesDefeated;
    private int gold;
    private Scanner scanner;

    public GameManager(){
        players = new ArrayList<>();
        inventory = new Inventory();
        inventory.addStarterItems();
        currentWave = 1;
        enemiesDefeated = 0;
        gold = 100;
        scanner = new Scanner(System.in);
    }

    private void createParty(){
        System.out.println("------ CREATE PARTY ------" + "\n");
        int choicesMade = 0;

        while(choicesMade < 2){
            System.out.println("\n Choose characters: " + (choicesMade + 1));
            System.out.println("1. Warrior");
            System.out.println("2. Mage");
            System.out.println("3. Archer");

            int choice;

            try{
                choice = Integer.parseInt(scanner.nextLine());
            } catch(NumberFormatException e){
                System.out.println("Invalid input.");
                continue;
            }

            System.out.println("\n Enter character name: ");

            String name = scanner.nextLine();

            Character newCharacter = null;

            switch (choice) {
                case 1:
                    newCharacter = new Warrior(name);
                    break;
                case 2:
                    newCharacter = new Mage(name);
                    break;
                case 3:
                    newCharacter = new Archer(name);
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }

            boolean duplicate = false;

            for(Character player : players){
                if(player.getClass() == newCharacter.getClass()){
                    duplicate = true;
                    break;
                }
            }

            if(duplicate){
                System.out.println("Character already in party.");
                continue;
            }

            players.add(newCharacter);

            System.out.println(name + " added to party.");

            choicesMade++;
        }

        System.out.println("\n Finished creating party ");

        System.out.println("\n Your party: ");

        for(Character player : players){
            System.out.println("- " + player.getName() + " (" + player.getClass().getSimpleName() + ")");
        }
    }

    public void startGame(){
        if(players.isEmpty()){
            createParty();
        }

        while(currentWave <= 4 && hasLivingPlayers()){
            System.out.println("====== Wave " + currentWave + " =======" + "\n");
            ArrayList<Enemy> enemies = createWave(currentWave);

            int enemyCount = enemies.size();

            BattleSystem battle = new BattleSystem(players, enemies, inventory);

            battle.startBattle();

            enemiesDefeated += enemyCount;

            if(hasLivingPlayers()){
                System.out.println("\n==================");
                System.out.println("        WAVE " + currentWave + " CLEARED!!!");
                System.out.println("Enemies Defeated: " + enemiesDefeated);
                System.out.println("==================");
                gold += 100;
                
                System.out.println("You earn 100 gold!");

                System.out.println("Current Gold: " + gold);

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
                enemies.add(new Enemy("Goblin", "Goblin", 120, 20, 5));
                break;
            case 2:
                enemies.add(new Enemy("Orc", "Orc", 150, 30, 10));
                enemies.add(new Enemy("Goblin Archer", "Goblin", 200, 25, 5));
                break;
            case 3:
                enemies.add(new Enemy("Dark Mage", "Dark Mage", 300, 40, 8));
                break;
            case 4:
                enemies.add(new Enemy("Dragon", "Boss", 350, 100, 20));
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
        System.out.println("Gold Earned: " + gold);

        System.out.println("==================");
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
