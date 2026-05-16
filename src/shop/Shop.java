package shop;
import java.util.Scanner;

import constants.GameConstants;
import inventory.Inventory;
import inventory.MegaPotion;
import inventory.HealthPotion;
import inventory.RevivePotion;

public class Shop {
    private Inventory inventory;
    private Scanner scanner;
    private int gold;

    public Shop(Inventory inventory, int gold, Scanner scanner) {
        this.inventory = inventory;
        this.gold = gold;

        this.scanner = scanner;
    }

    public int openShop(){
        boolean shopping = true;

        while (shopping) {
            System.out.println("\n === SHOP === \n");
            System.out.println("Gold: " + gold);
            System.out.println("1. Health Potion - 20 gold");
            System.out.println("2. Mega Potion - 50 gold");
            System.out.println("3. Revive Potion - 100 gold");

            System.out.println("4. Exit Shop");
            System.out.println("5. Save Game");

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
                    buyHealthPotion();
                    break;
                case 2:
                    buyMegaPotion();
                    break;
                case 3: 
                    buyRevivePotion();
                    break;
                case 4:
                    shopping = false;
                    break;
                case 5:
                    System.out.println("Saving game...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }

        return gold;
    }

    private void buyHealthPotion(){
        if(gold >= GameConstants.HEALTH_POTION_PRICE){
            inventory.addItem(new HealthPotion());
            gold -= 20;
            System.out.println("You bought a Health Potion for 20 gold.");
        } else {
            System.out.println("You don't have enough gold to buy a Health Potion.");
        }
    }

    private void buyMegaPotion(){
        if(gold >= GameConstants.MEGA_POTION_PRICE){
            inventory.addItem(new MegaPotion());
            gold -= 50;
            System.out.println("You bought a Mega Potion for 50 gold.");
        } else {
            System.out.println("You don't have enough gold to buy a Mega Potion.");
        }
    }

    private void buyRevivePotion(){
        if(gold >= GameConstants.REVIVE_POTION_PRICE){
            inventory.addItem(new RevivePotion());
            gold -= 100;
            System.out.println("You bought a Revive Potion for 100 gold.");
        } else {
            System.out.println("You don't have enough gold to buy a Revive Potion.");
        }
    }
}
