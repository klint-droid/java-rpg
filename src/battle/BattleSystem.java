package battle;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import actions.AttackAction;
import actions.BattleAction;
import actions.DefendAction;
import actions.SkillAction;
import characters.Character;
import constants.GameConstants;
import enemies.Enemy;
import inventory.EmptyInventoryException;
import inventory.Inventory;
import results.BattleResult;
import ui.ConsoleUI;

/**
 * Console-mode battle system. Uses shared hasLivingPlayers/hasLivingEnemies
 * helpers for consistency, while keeping the scanner-based interaction intact.
 */
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
                    ui.showMessage(player.getName() + " misses the attack due to being taunted.");
                } else {
                    BattleAction action = new AttackAction(player, target);
                    BattleResult result = action.execute();
                    ui.showMessage(formatResultMessage(result));
                }

                player.reduceTauntTurns();
                continue;
            }
            
            ui.showMessage("Choose an action:");
            ui.showMessage("1. Attack");
            ui.showMessage("2. Defend");
            ui.showMessage("3. Use Skill");
            ui.showMessage("4. Use Item");
            ui.showMessage("5. Flee");

            ui.showMessage("Enter your choice: ");

            int choice;

            try{
                choice = Integer.parseInt(scanner.nextLine());
            } catch(NumberFormatException e){
                ui.showMessage("Invalid input. Please enter a number.");
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
                    ui.showMessage("Invalid choice.");
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
        displayActionResult(result);
        return hasLivingEnemies();
    }

    private void handleDefendAction(Character player){
        BattleAction defendAction = new DefendAction(player);
        BattleResult result = defendAction.execute();
        displayActionResult(result);
    }

    private boolean handleSkillAction(Character player){
        Enemy target = chooseTarget();

        if(target == null){
            return false;
        }

        BattleAction skillAction = new SkillAction(player, target);
        BattleResult result = skillAction.execute();
        displayActionResult(result);
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
            ui.showMessage("Choose an item to use:");
            int itemChoice;

            try{
                itemChoice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e){
                ui.showMessage("Invalid item input.");
                return;
            }

            Character targetPlayer = choosePlayerTarget();
            inventory.useItem(itemChoice - 1, targetPlayer);
            displayPartyStatus();
        } catch (EmptyInventoryException e) {
            ui.showMessage(e.getMessage());
        } catch(IndexOutOfBoundsException e){
            ui.showMessage("Invalid item choice.");
        }
    }
    private void displayEnemies(){
        ui.showMessage("\n=== Enemies ===");
        for(int i = 0; i < enemies.size(); i++){
            Enemy enemy = enemies.get(i);

            String status = enemy.isAlive() ? "Alive" : "Dead";
            ui.showMessage((i + 1) + ". " + enemy.getName() + " | HP: " + enemy.getHp() + " | Status: " + status);
        }

        ui.showMessage("===============\n");
    }

    private void displayPartyStatus(){
        ui.showMessage("\n === Party Status === ");

        for(Character player : players){
            String status = player.isAlive() ? "Alive" : "Dead";

            ui.showMessage(player.getName() + " | HP: " + player.getHp() + " | Status: " + status);
        }

        ui.showMessage("=====================");
    }
    private Enemy chooseTarget(){

        while(true){
            if(!hasLivingEnemies()){
                return null;
            }

            displayEnemies();

            ui.showMessage("Choose target:");

            try{

                int targetChoice = Integer.parseInt(scanner.nextLine());

                if(targetChoice < 1 || targetChoice > enemies.size()){

                    ui.showMessage("Invalid target choice.");
                    continue;
                }

                Enemy target = enemies.get(targetChoice - 1);

                if(!target.isAlive()){

                    ui.showMessage("That enemy is already defeated.");
                    continue;
                }

                return target;

            } catch(NumberFormatException e){
                ui.showMessage("Invalid input.");
            }
        }
    }

    private Character choosePlayerTarget(){
        while(true){
            ui.showMessage("\n Choose Party Member: ");
            for(int i = 0; i < players.size(); i++){
                Character player = players.get(i);

                ui.showMessage((i + 1) + ". " + player.getName() + " | HP: " + player.getHp());
            }

            try {
                int choice = Integer.parseInt(scanner.nextLine());

                if(choice < 1 || choice > players.size()){
                    ui.showMessage("Invalid choice.");
                    continue;
                }

                return players.get(choice - 1);
            } catch(NumberFormatException e){
                ui.showMessage("Invalid input.");
            }
        }
    }

    private void enemyTurn(){
        ui.showMessage("\n\nEnemies turn: ");
        for(Enemy enemy : enemies){
            if(!enemy.isAlive()){
                continue;
            }
            BattleResult result = enemy.getAi().decideAction(enemy, players);
            displayActionResult(result);
        }
    }

    private void displayBattleResults(){
        if(hasLivingPlayers()){
            ui.showMessage("\n\nYou won the battle!");
            ui.showMessage("Turns taken: " + turnCount);
        } else {
            ui.showMessage("\n\nYou lost the battle.");
        }
    }

    public boolean fleeBattle(){
        double fleeChance = random.nextInt(100);

        if(fleeChance < GameConstants.FLEE_SUCCESS_CHANCE){
            ui.showMessage("You fled the battle.");
            return true;
        }

        ui.showMessage("You failed to flee the battle.");
        ui.showMessage("All living party members take " + (int) GameConstants.FLEE_DAMAGE + " damage.");

        for(Character player : players){
            if(player.isAlive()){
                player.takeDamage(GameConstants.FLEE_DAMAGE);
                ui.showMessage(player.getName() + " takes " + (int) GameConstants.FLEE_DAMAGE + " damage and has " + (int) player.getHp() + " HP left.");
                if(!player.isAlive()){
                    ui.showMessage(player.getName() + " has been slain while fleeing!");
                }
            }
        }

        return false;
    }

    private void displayActionResult(BattleResult result) {
        ui.showMessage(formatResultMessage(result));
        if (result.isTargetSlain()) {
            ui.showMessage("The target has been slain!");
        }
    }

    /**
     * Format a battle result message for console output, including slain notifications.
     */
    private String formatResultMessage(BattleResult result) {
        String message = result.getMessage();
        if (result.getDamage() > 0 && !message.toLowerCase().contains("damage")) {
            message += " (" + (int) result.getDamage() + " damage)";
        }
        if (result.isTargetSlain() && !message.toLowerCase().contains("slain")) {
            message += " The target has been slain!";
        }
        return message;
    }

    private void showTurnBanner(){
        ui.showDividerLine();
        ui.showMessage("Turn " + turnCount);
        ui.showDividerLine();
    }
}
