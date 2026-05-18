package shop;

import java.util.Scanner;

import constants.GameConstants;
import inventory.Inventory;
import inventory.MegaPotion;
import inventory.HealthPotion;
import inventory.RevivePotion;
import inventory.ManaPotion;
import game.GameState;

/**
 * Console-mode shop. Uses ShopService for purchase logic
 * to eliminate duplicated buy methods.
 */
public class Shop {
    private Inventory inventory;
    private Scanner scanner;
    private double gold;

    public Shop(Inventory inventory, double gold, Scanner scanner) {
        this.inventory = inventory;
        this.gold = gold;
        this.scanner = scanner;
    }

    public double openShop(){
        // Create a temporary GameState to use ShopService
        GameState tempState = new GameState();
        tempState.setGold(gold);
        tempState.setInventory(inventory);

        boolean shopping = true;

        while (shopping) {
            System.out.println("\n === SHOP === \n");
            System.out.println("Gold: " + tempState.getGold());
            System.out.println("1. Health Potion - " + GameConstants.HEALTH_POTION_PRICE + " gold");
            System.out.println("2. Mega Potion - " + GameConstants.MEGA_POTION_PRICE + " gold");
            System.out.println("3. Mana Potion - " + GameConstants.MANA_POTION_PRICE + " gold");
            System.out.println("4. Revive Potion - " + GameConstants.REVIVE_POTION_PRICE + " gold");

            System.out.println("5. Exit Shop");
            System.out.println("6. Save Game");

            System.out.println("Enter your choice: ");

            int choice;

            try{
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e){
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1:
                    buyItem(new HealthPotion(), GameConstants.HEALTH_POTION_PRICE, tempState);
                    break;
                case 2:
                    buyItem(new MegaPotion(), GameConstants.MEGA_POTION_PRICE, tempState);
                    break;
                case 3:
                    buyItem(new ManaPotion(), GameConstants.MANA_POTION_PRICE, tempState);
                    break;
                case 4:
                    buyItem(new RevivePotion(), GameConstants.REVIVE_POTION_PRICE, tempState);
                    break;
                case 5:
                    shopping = false;
                    break;
                case 6:
                    System.out.println("Saving game...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }

        return tempState.getGold();
    }

    private void buyItem(inventory.Item item, double price, GameState tempState) {
        if (ShopService.buyItem(item, price, tempState)) {
            System.out.println("You bought a " + item.getName() + " for " + price + " gold.");
        } else {
            System.out.println("You don't have enough gold to buy a " + item.getName() + ".");
        }
    }
}
