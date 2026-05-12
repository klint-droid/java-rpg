import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class BattleSystem {
    private ArrayList<Character> players;   
    private ArrayList<Enemy> enemies;
    private Inventory inventory;
    private Scanner scanner;
    private Random random;
    private int turnCount;

    public BattleSystem(ArrayList<Character> players, ArrayList<Enemy> enemies, Inventory inventory) {
        this.players = players;
        this.enemies = enemies;
        this.inventory = inventory;
        this.scanner = new Scanner(System.in);
        this.random = new Random();
        this.turnCount = 1;
    }

    public void startBattle(){
        while(hasLivingPlayers() && hasLivingEnemies()){
            System.out.println("\n\n========= Turn " + turnCount + " ==========\n\n");

            playerTurn();

            removeDeadEnemies();

            if(!hasLivingEnemies()){
                break;
            }

            enemyTurn();
            removeDeadPlayers();

            turnCount++;
        }

        displayBattleResults();
    }

    private boolean hasLivingPlayers(){
        for(Character player : players){
            if(player.isAlive()){
                return true;
            }
        }
        return false;
    }

    private boolean hasLivingEnemies(){
        for(Enemy enemy : enemies){
            if(enemy.isAlive()){
                return true;
            }
        }
        return false;
    }

    private void playerTurn(){
        for(Character player : players){
            if(!player.isAlive()){
                continue;
            }

            System.out.println(player.getName() + "'s turn:");

            displayEnemies();

            if(player.isTaunted()){
                System.out.println(player.getName() + " is taunted!");

                Enemy target = enemies.get(0);

                int missRoll = random.nextInt(100);

                if(missRoll < 70){
                    System.out.println(player.getName() + " misses the attack due to being taunted.");
                } else {
                    player.attack(target);
                }

                player.reduceTauntTurns();
                continue;
            }
            
            System.out.println("Choose an action:");
            System.out.println("1. Attack");
            System.out.println("2. Defend");
            System.out.println("3. Use Skill");
            System.out.println("4. Use Item");
            System.out.println("5. Flee");

            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();

            Enemy target = enemies.get(0);

            switch (choice) {
                case 1:
                    player.attack(target);
                    break;
                case 2:
                    player.defend();
                    break;
                case 3:
                    player.useSkill(target);
                    break;
                case 4:
                    try {
                        inventory.displayItems();
                        System.out.println("Choose an item to use:");
                        int itemChoice = scanner.nextInt();
                        inventory.useItem(itemChoice - 1, player);
                    } catch (EmptyInventoryException e) {
                        System.out.println(e.getMessage());
                    } catch(IndexOutOfBoundsException e){
                        System.out.println("Invalid item choice.");
                    }   
                    break;
                case 5:
                    boolean escaped = fleeBattle();

                    if(escaped){
                        System.out.println("You escaped the battle.");
                        System.out.println("Battle over.");

                        System.exit(0);
                    }
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        }
    }

    private void displayEnemies(){
        System.out.println("\n Enemies: ");
        for(int i = 0; i < enemies.size(); i++){
            Enemy enemy = enemies.get(i);

            System.out.println(i + 1 + ". " + enemy.getEnemyType() + " - HP: " + enemy.getHp());
        }
    }

    private void enemyTurn(){
        System.out.println("\n\nEnemies turn: ");
        for(Enemy enemy : enemies){
            if(!enemy.isAlive()){
                continue;
            }
            Character target = players.get(random.nextInt(players.size()));
            
            if(target.isAlive()){
                enemy.enemyAction(target);
            }
        }
    }

    private void removeDeadEnemies(){
        enemies.removeIf(enemy -> !enemy.isAlive());
    }

    private void removeDeadPlayers(){
        players.removeIf(player -> !player.isAlive());
    }

    private void displayBattleResults(){
        if(hasLivingPlayers()){
            System.out.println("\n\nYou won the battle!");
            System.out.println("Turns taken: " + turnCount);
        } else {
            System.out.println("\n\nYou lost the battle.");
        }
    }

    public boolean fleeBattle(){
        int fleeChance = random.nextInt(100);

        if(fleeChance < 60){
            System.out.println("You fled the battle.");
            return true;
        }

        System.out.println("You failed to flee the battle.");
        System.out.println("All party members take damage.");

        for(Character player : players){
            if(player.isAlive()){
                player.takeDamage(15);
                System.out.println(player.getName() + " takes 15 damage.");
            }
        }

        return false;
    }
}
