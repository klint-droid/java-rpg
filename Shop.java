import java.util.Scanner;
public class Shop {
    private Inventory inventory;
    private Scanner scanner;
    private int gold;

    public Shop(Inventory inventory, int gold){
        this.inventory = inventory;
        this.gold = gold;

        scanner = new Scanner(System.in);
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

            int choice = scanner.nextInt();

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
        if(gold >= 20){
            inventory.addItem(new Item("Health Potion", "Restores 20 HP", 20));
            gold -= 20;
            System.out.println("You bought a Health Potion for 20 gold.");
        } else {
            System.out.println("You don't have enough gold to buy a Health Potion.");
        }
    }

    private void buyMegaPotion(){
        if(gold >= 50){
            inventory.addItem(new Item("Mega Potion", "Restores 50 HP", 50));
            gold -= 50;
            System.out.println("You bought a Mega Potion for 50 gold.");
        } else {
            System.out.println("You don't have enough gold to buy a Mega Potion.");
        }
    }

    private void buyRevivePotion(){
        if(gold >= 100){
            inventory.addItem(new Item("Revive Potion", "Revives a Dead Character", 0));
            gold -= 100;
            System.out.println("You bought a Revive Potion for 100 gold.");
        } else {
            System.out.println("You don't have enough gold to buy a Revive Potion.");
        }
    }
}
