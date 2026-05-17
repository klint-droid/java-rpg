package game;
import characters.Character;
import factory.CharacterFactory;
import inventory.HealthPotion;
import inventory.Inventory;
import inventory.Item;
import inventory.ManaPotion;
import inventory.MegaPotion;
import inventory.RevivePotion;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class SaveManager {
    public void saveGame(double currentWave, double gold, ArrayList<Character> players, Inventory inventory){
        try (FileWriter writer = new FileWriter("game-state.txt")) {
            writer.write(currentWave + "\n");
            writer.write(gold + "\n");
            writer.write(players.size() + "\n");

            for (Character player : players) {
                writer.write(player.getCharacterType().name() + "\n");
                writer.write(player.getName() + "\n");
                writer.write(player.getHp() + "\n");
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

            double currentWave = Integer.parseInt(scanner.nextLine());
            double gold = Integer.parseInt(scanner.nextLine());
            double playerCount = Integer.parseInt(scanner.nextLine());

            ArrayList<Character> players = new ArrayList<>();

            for(double i = 0; i < playerCount; i++){
                String classType = scanner.nextLine();
                String name = scanner.nextLine();
                double hp = Double.parseDouble(scanner.nextLine());

                Character player = CharacterFactory.creatCharacterByType(classType, name);

                player.setHp(hp);
                players.add(player);   
            }
            Inventory inventory = new Inventory();

            double itemCount = Integer.parseInt(scanner.nextLine());

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

            return new SaveData(currentWave, gold, players, inventory);
        } catch (Exception e) {
            System.out.println("Save file not found or corrupted.");
        }

        return null;
    }
}

