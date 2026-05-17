package battle;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import characters.Character;
import constants.GameConstants;
import enemies.Enemy;
import inventory.EmptyInventoryException;
import inventory.Inventory;
import results.BattleResult;
import ui.ConsoleUI;
import actions.AttackAction;
import actions.BattleAction;
import actions.DefendAction;
import actions.SkillAction;

public class BattleSystem {
    private ArrayList<Character> players;   
    private ArrayList<Enemy> enemies;
    private Inventory inventory;
    private Scanner scanner;
    private Random random;
    private double turnCount;
    private ConsoleUI ui;

    public BattleSystem(ArrayList<Character> players, ArrayList<Enemy> enemies, Inventory inventory, Scanner scanner, ConsoleUI ui) {
        this.players = players;
        this.enemies = enemies;
        this.inventory = inventory;
        this.scanner = scanner;
        this.random = new Random();
        this.turnCount = 1;
        this.ui = ui;
    }

    public void startBattle(){
        while(hasLivingPlayers() && hasLivingEnemies()){
            showTurnBanner();

            displayPartyStatus();
            displayEnemies();

            playerTurn();

            if(!hasLivingEnemies()){
                break;
            }

            enemyTurn();

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

            ui.showMessage(player.getName() + "'s turn:");

            displayEnemies();

            if(player.isTaunted()){
                ui.showMessage(player.getName() + " is taunted!");

                Enemy target = enemies.get(0);

                double missRoll = random.nextInt(100);

                if(missRoll < 70){
                    System.out.println(player.getName() + " misses the attack due to being taunted.");
                } else {
                    BattleAction action = new AttackAction(player, target);
                    BattleResult result = action.execute();

                    ui.showMessage(result.getMessage());
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

            int choice;

            try{
                choice = Integer.parseInt(scanner.nextLine());
            } catch(NumberFormatException e){
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1:
                    if(!handleAttackAction(player)){
                        return;
                    }
                    break;
                case 2:
                    handleDefendAction(player);
                    break;
                case 3:
                    if(!handleSkillAction(player)){
                        return;
                    }
                    break;
                case 4:
                    handleItemAction();
                    break;
                case 5:
                    handleFleeAction();
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        }
    }

    private boolean handleAttackAction(Character player){
        Enemy target = chooseTarget();

        if(target == null){
            return false;
        }

        BattleAction attackAction = new AttackAction(player, target);

        BattleResult result = attackAction.execute();

        ui.showMessage(result.getMessage());

        return hasLivingEnemies();
    }

    private void handleDefendAction(Character player){
        BattleAction defendAction = new DefendAction(player);
        BattleResult result = defendAction.execute();

        ui.showMessage(result.getMessage());

    }

    private boolean handleSkillAction(Character player){
        Enemy target = chooseTarget();

        if(target == null){
            return false;
        }

        BattleAction skillAction = new SkillAction(player, target);
        BattleResult result = skillAction.execute();

        ui.showMessage(result.getMessage());

        return hasLivingEnemies();
    }

    private void handleFleeAction(){
        boolean escaped = fleeBattle();

        if(escaped){
            ui.showMessage("You escaped the battle");
            ui.showMessage("Battle over");
        }
    }

    private void handleItemAction(){
        try {
            inventory.displayItems();
            System.out.println("Choose an item to use:");
            int itemChoice;

            try{
                itemChoice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e){
                System.out.println("Invalid item input.");
                return;
            }

            Character targetPlayer = choosePlayerTarget();
            inventory.useItem(itemChoice - 1, targetPlayer);
            displayPartyStatus();
        } catch (EmptyInventoryException e) {
            System.out.println(e.getMessage());
        } catch(IndexOutOfBoundsException e){
            System.out.println("Invalid item choice.");
        }   
    }
    private void displayEnemies(){
        System.out.println("\n=== Enemies ===");
        for(int i = 0; i < enemies.size(); i++){
            Enemy enemy = enemies.get(i);

            String status = enemy.isAlive() ? "Alive" : "Dead";

            System.out.println((i + 1) + ". " + enemy.getName() + " | HP: " + enemy.getHp() + " | Status: " + status);
        }

        System.out.println("===============\n");
    }

    private void displayPartyStatus(){
        System.out.println("\n === Party Status === ");

        for(Character player : players){
            String status = player.isAlive() ? "Alive" : "Dead";

            System.out.println(player.getName() + " | HP: " + player.getHp() + " | Status: " + status);
        }

        System.out.println("=====================");
    }
    private Enemy chooseTarget(){

        while(true){
            if(!hasLivingEnemies()){
                return null;
            }

            displayEnemies();

            System.out.print("Choose target: ");

            try{

                int targetChoice = Integer.parseInt(scanner.nextLine());

                if(targetChoice < 1 || targetChoice > enemies.size()){

                    System.out.println("Invalid target choice.");
                    continue;
                }

                Enemy target = enemies.get(targetChoice - 1);

                if(!target.isAlive()){

                    System.out.println("That enemy is already defeated.");
                    continue;
                }

                return target;

            } catch(NumberFormatException e){

                System.out.println("Invalid input.");
            }
        }
    }

    private Character choosePlayerTarget(){
        while(true){
            System.out.println("\n Choose Party Member: ");
            for(int i = 0; i < players.size(); i++){
                Character player = players.get(i);

                System.out.println((i + 1) + ". " + player.getName() + " | HP: " + player.getHp());
            }

            try {
                int choice = Integer.parseInt(scanner.nextLine());

                if(choice < 1 || choice > players.size()){
                    System.out.println("Invalid choice.");
                    continue;
                }

                return players.get(choice - 1);
            } catch(NumberFormatException e){
                System.out.println("Invalid input.");
            }
        }
    }

    private void enemyTurn(){
        System.out.println("\n\nEnemies turn: ");
        for(Enemy enemy : enemies){
            if(!enemy.isAlive()){
                continue;
            }
            BattleResult result = enemy.getAi().decideAction(enemy, players);

        ui.showMessage(result.getMessage());
        }
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
        double fleeChance = random.nextInt(100);

        if(fleeChance < GameConstants.FLEE_SUCCESS_CHANCE){
            System.out.println("You fled the battle.");
            return true;
        }

        System.out.println("You failed to flee the battle.");
        System.out.println("All party members take damage.");

        for(Character player : players){
            if(player.isAlive()){
                player.takeDamage(GameConstants.FLEE_DAMAGE);
                System.out.println(player.getName() + " takes 15 damage.");
            }
        }

        return false;
    }

    private void showTurnBanner(){
        ui.showDividerLine();
        System.out.println("Turn " + turnCount);
        ui.showDividerLine();
    }
}

