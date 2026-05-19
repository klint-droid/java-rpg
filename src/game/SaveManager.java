package game;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import characters.Character;
import enemies.Enemy;
import factory.CharacterFactory;
import inventory.HealthPotion;
import inventory.Inventory;
import inventory.Item;
import inventory.ManaPotion;
import inventory.MegaPotion;
import inventory.RevivePotion;

public class SaveManager {
    public void saveGame(double currentWave, double gold, double turnCount, int currentPlayerIndex, ArrayList<Character> players, ArrayList<Enemy> enemies, Inventory inventory){
        try (FileWriter writer = new FileWriter("game-state.txt")) {
            writer.write(currentWave + "\n");
            writer.write(gold + "\n");
            writer.write(turnCount + "\n");
            writer.write(currentPlayerIndex + "\n");
            writer.write(players.size() + "\n");

            for (Character player : players) {
                writer.write(player.getCharacterType().name() + "\n");
                writer.write(player.getName() + "\n");
                writer.write(player.getHp() + "\n");
                writer.write(player.getMana() + "\n");
            }

            // Save enemies
            writer.write(enemies.size() + "\n");
            for (Enemy enemy : enemies) {
                writer.write(enemy.getName() + "\n");
                writer.write(enemy.getEnemyType() + "\n");
                writer.write(enemy.getMaxHp() + "\n");
                writer.write(enemy.getAtkPower() + "\n");
                writer.write(enemy.getDefPower() + "\n");
                writer.write(enemy.getHp() + "\n");
            }

            writer.write(inventory.getItems().size() + "\n");
            for (Item item : inventory.getItems()) {
                writer.write(item.getClass().getSimpleName() + "\n");
            }

            System.out.println("Game saved successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while saving the game.");
        }
    }

    public SaveData loadGame(){
        try (Scanner scanner = new Scanner(new File("game-state.txt"))) {

            double currentWave = Double.parseDouble(scanner.nextLine());
            double gold = Double.parseDouble(scanner.nextLine());
            double turnCount = Double.parseDouble(scanner.nextLine());
            int currentPlayerIndex = Integer.parseInt(scanner.nextLine());
            double playerCount = Double.parseDouble(scanner.nextLine());

            ArrayList<Character> players = new ArrayList<>();

            for(double i = 0; i < playerCount; i++){
                String classType = scanner.nextLine();
                String name = scanner.nextLine();
                double hp = Double.parseDouble(scanner.nextLine());
                
                // Read mana. To support old save files, we could theoretically do a check, but assuming save format is strictly enforced
                double mana = Double.parseDouble(scanner.nextLine());

                Character player = CharacterFactory.creatCharacterByType(classType, name);

                player.setHp(hp);
                player.setMana(mana);
                players.add(player);   
            }

            // Load enemies
            ArrayList<Enemy> enemies = new ArrayList<>();
            double enemyCount = Double.parseDouble(scanner.nextLine());

            for(double i = 0; i < enemyCount; i++){
                String enemyName = scanner.nextLine();
                String enemyType = scanner.nextLine();
                double maxHp = Double.parseDouble(scanner.nextLine());
                double atkPower = Double.parseDouble(scanner.nextLine());
                double defPower = Double.parseDouble(scanner.nextLine());
                double currentHp = Double.parseDouble(scanner.nextLine());

                Enemy enemy = new Enemy(enemyName, enemyType, maxHp, atkPower, defPower);
                enemy.setHp(currentHp);
                enemies.add(enemy);   
            }

            Inventory inventory = new Inventory();

            double itemCount = Double.parseDouble(scanner.nextLine());

            for(double i = 0; i < itemCount; i++){
                String itemType = scanner.nextLine();

                Item item;

                switch(itemType){
                    case "HealthPotion" -> item = new HealthPotion();
                    case "MegaPotion" -> item = new MegaPotion();
                    case "ManaPotion" -> item = new ManaPotion();
                    case "RevivePotion" -> item = new RevivePotion();
                    default -> throw new IllegalArgumentException("Unknown item type: " + itemType);
                }

                inventory.addItem(item);
            }

            scanner.close();

            return new SaveData(currentWave, gold, turnCount, currentPlayerIndex, players, enemies, inventory);
        } catch (Exception e) {
            System.out.println("Save file not found or corrupted.");
            e.printStackTrace();
        }

        return null;
    }
}

