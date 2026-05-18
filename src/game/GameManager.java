package game;

import java.util.ArrayList;
import java.util.Scanner;

import battle.BattleSystem;
import characters.Character;
import enemies.Enemy;
import inventory.Inventory;
import shop.Shop;
import constants.GameConstants;
import factory.CharacterFactory;
import ui.ConsoleUI;

/**
 * Console-mode game manager. Now uses GameState and WaveFactory
 * to eliminate duplicated state fields and wave creation logic.
 */
public class GameManager {
    private GameState state;
    private Scanner scanner;
    private ConsoleUI ui;

    public GameManager(Scanner scanner) {
        state = new GameState();
        state.setInventory(new Inventory());
        state.getInventory().addStarterItems();
        state.setCurrentWave(1);
        state.setEnemiesDefeated(0);
        state.setGold(GameConstants.STARTING_GOLD);
        this.scanner = scanner;
        this.ui = new ConsoleUI();
    }

    private void createParty(){
        System.out.println("------ CREATE PARTY ------" + "\n");
        double choicesMade = 0;

        while(choicesMade < 2){
            System.out.println("\n Choose characters: " + (choicesMade + 1));
            System.out.println("1. Warrior");
            System.out.println("2. Mage");
            System.out.println("3. Archer");

            int choice;

            try{
                choice = Integer.parseInt(scanner.nextLine());
            } catch(NumberFormatException e){
                ui.showMessage("Invalid input. Please enter a number.");
                continue;
            }

            System.out.println("\n Enter character name: ");
            String name = scanner.nextLine();

            Character newCharacter;

            try {
                newCharacter = CharacterFactory.createCharacter(choice, name);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                continue;
            }

            boolean duplicate = false;

            for(Character player : state.getPlayers()){
                if(player.getCharacterType() == newCharacter.getCharacterType()){
                    duplicate = true;
                    break;
                }
            }

            if(duplicate){
                System.out.println("Character already in party.");
                continue;
            }

            state.getPlayers().add(newCharacter);

            System.out.println(name + " added to party.");

            choicesMade++;
        }

        System.out.println("\n Finished creating party ");

        System.out.println("\n Your party: ");

        for(Character player : state.getPlayers()){
            System.out.println("- " + player.getName() + " (" + player.getClass().getSimpleName() + ")");
        }
    }

    public void startGame(){
        if(state.getPlayers().isEmpty()){
            createParty();
        }

        while(state.getCurrentWave() <= 4 && state.hasLivingPlayers()){
            System.out.println("====== Wave " + state.getCurrentWave() + " =======" + "\n");
            ArrayList<Enemy> enemies = WaveFactory.createWave((int) state.getCurrentWave());

            double enemyCount = enemies.size();

            BattleSystem battle = new BattleSystem(state.getPlayers(), enemies, state.getInventory(), scanner, ui);

            battle.startBattle();

            state.addEnemiesDefeated(enemyCount);

            if(state.hasLivingPlayers()){
                System.out.println("\n==================");
                System.out.println("        WAVE " + state.getCurrentWave() + " CLEARED!!!");
                System.out.println("Enemies Defeated: " + state.getEnemiesDefeated());
                System.out.println("==================");
                state.addGold(GameConstants.WAVE_REWARD);
                
                System.out.println("You earn 100 gold!");

                System.out.println("Current Gold: " + state.getGold());

                Shop shop = new Shop(state.getInventory(), state.getGold(), scanner);
                state.setGold(shop.openShop());

                SaveManager saveManager = new SaveManager();
                saveManager.saveGame(state.getCurrentWave(), state.getGold(), state.getPlayers(), state.getInventory());
                state.setCurrentWave(state.getCurrentWave() + 1);
            }       
        }

        System.out.println("Game Over!");
        displayFinalResult();
    }

    private void displayFinalResult(){
        System.out.println("==== GAME OVER ====" + "\n");
        
        if(state.hasLivingPlayers()){
            System.out.println("VICTORY");
        } else {
            System.out.println("DEFEAT");
        }

        System.out.println("Waves Cleared: " + (state.getCurrentWave() - 1));
        System.out.println("Enemies Defeated: " + state.getEnemiesDefeated());
        System.out.println("Gold Earned: " + state.getGold());

        System.out.println("==================");
    }
    
    public void loadSaveGame(SaveData saveData){
        state.setCurrentWave(saveData.getCurrentWave());
        state.setGold(saveData.getGold());
        state.setPlayers(saveData.getPlayers());
        state.setInventory(saveData.getInventory());
    }

    public void startLoadedGame(){
        System.out.println("Loading saved game...");
        startGame();
    }
}
