import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class SaveManager {
    private Scanner scanner = new Scanner(System.in);
    public void saveGame(int currentWave, int gold, ArrayList<Character> players, Inventory inventory){
        try {
            FileWriter writer = new FileWriter("game-state.txt");
            writer.write(currentWave + "\n");
            writer.write(gold + "\n");
            writer.write(players.size() + "\n");

            for(Character player : players) {
                writer.write(player.getClass().getSimpleName() + "\n");
                writer.write(player.getName() + "\n");
                writer.write(player.getHp() + "\n");
            }

            writer.write(inventory.getItems().size());

            for(Item item : inventory.getItems()){
                writer.write(item.getName() + "\n");
                writer.write(item.getDescription() + "\n");
                writer.write(item.getEffectValue() + "\n");
            }

            writer.close();

            System.out.println("Game saved successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while saving the game.");
        }
    } 

    public SaveData loadGame(){
        try {
            scanner = new Scanner(new File("game-state.txt"));

            int currentWave = Integer.parseInt(scanner.nextLine());
            int gold = Integer.parseInt(scanner.nextLine());
            int playerCount = Integer.parseInt(scanner.nextLine());

            ArrayList<Character> players = new ArrayList<>();

            for(int i = 0; i < playerCount; i++){
                String classType = scanner.nextLine();
                String name = scanner.nextLine();
                double hp = Double.parseDouble(scanner.nextLine());

                Character player = null;

                switch (classType) {
                    case "Warrior":
                        player = new Warrior(name);
                        break;
                    case "Mage":
                        player = new Mage(name);
                        break;
                    case "Archer":
                        player = new Archer(name);
                        break;
                }

                player.setHp(hp);
                players.add(player);   
            }
            Inventory inventory = new Inventory();

            int itemCount = Integer.parseInt(scanner.nextLine());

            for(int i = 0; i < itemCount; i++){
                String itemName = scanner.nextLine();
                String itemDescription = scanner.nextLine();
                int itemEffectValue = Integer.parseInt(scanner.nextLine());

                Item item = new Item(itemName, itemDescription, itemEffectValue);
                inventory.addItem(item);
            }

            scanner.close();

            return new SaveData(currentWave, gold, players, inventory);
        } catch (Exception e) {
            System.out.println("An error occurred while loading the game.");
        }

        return null;
    }
}
