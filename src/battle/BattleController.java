package battle;

import actions.AttackAction;
import actions.BattleAction;
import actions.DefendAction;
import actions.SkillAction;
import characters.Character;
import constants.GameConstants;
import enemies.Enemy;
import game.GameState;
import inventory.EmptyInventoryException;
import inventory.InventoryService;
import inventory.StackedItem;
import results.BattleResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the turn-based battle flow for the GUI mode.
 * Extracted from RpgGameUI (lines 558-878) to follow the Single Responsibility Principle.
 *
 * Communicates with the UI through the BattleEventListener interface,
 * so this class has zero knowledge of Swing.
 */
public class BattleController {

    /**
     * Callback interface for the UI to react to battle events.
     * Follows the Dependency Inversion Principle — the controller depends on
     * this abstraction, not on concrete Swing classes.
     */
    public interface BattleEventListener {
        void onLog(String message);
        void onStatusUpdate(String message);
        void onPanelsRefresh();
        void onActionButtonsEnabled(boolean enabled);
        void onBattleEnd(boolean victory);
        Enemy onChooseEnemyTarget(ArrayList<Enemy> aliveEnemies);
        Character onChoosePlayerTarget(ArrayList<Character> players);
        String onChooseItem(List<StackedItem> stackedItems);
        Character onChooseItemTarget(ArrayList<Character> players);
    }

    private final GameState state;
    private final InventoryService inventoryService;
    private final BattleEventListener listener;

    public BattleController(GameState state, InventoryService inventoryService,
                            BattleEventListener listener) {
        this.state = state;
        this.inventoryService = inventoryService;
        this.listener = listener;
    }

    public void nextPlayerTurn() {
        if (!state.hasLivingPlayers()) {
            listener.onBattleEnd(false);
            return;
        }
        if (!state.hasLivingEnemies()) {
            listener.onBattleEnd(true);
            return;
        }

        int nextIndex = state.findNextAlivePlayer(state.getCurrentPlayerIndex());
        state.setCurrentPlayerIndex(nextIndex);

        if (nextIndex < 0) {
            enemyTurn();
            return;
        }

        Character active = state.getPlayers().get(nextIndex);
        if (!active.isAlive()) {
            state.setCurrentPlayerIndex(nextIndex + 1);
            nextPlayerTurn();
            return;
        }

        listener.onStatusUpdate(active.getName() + " is ready. Choose an action.");
        listener.onActionButtonsEnabled(true);
    }

    public void handleAction(int actionType) {
        if (!state.hasLivingPlayers() || !state.hasLivingEnemies()) {
            return;
        }

        Character active = state.getPlayers().get(state.getCurrentPlayerIndex());
        if (active == null || !active.isAlive()) {
            return;
        }

        switch (actionType) {
            case 1 -> performAttack(active);
            case 2 -> performDefend(active);
            case 3 -> performSkill(active);
            case 4 -> performItem();
            case 5 -> performFlee(active);
            default -> {}
        }
    }

    private void performAttack(Character active) {
        Enemy target = listener.onChooseEnemyTarget(state.getAliveEnemies());
        if (target == null) {
            return;
        }

        BattleAction action = new AttackAction(active, target);
        BattleResult result = action.execute();
        listener.onLog(result.getMessage());
        afterPlayerAction(result);
    }

    private void performDefend(Character active) {
        BattleAction action = new DefendAction(active);
        BattleResult result = action.execute();
        listener.onLog(result.getMessage());
        afterPlayerAction(result);
    }

    private void performSkill(Character active) {
        Enemy target = listener.onChooseEnemyTarget(state.getAliveEnemies());
        if (target == null) {
            return;
        }

        BattleAction action = new SkillAction(active, target);
        BattleResult result = action.execute();
        listener.onLog(result.getMessage());

        if (result.getMessage().contains("does not have enough mana")) {
            listener.onStatusUpdate(active.getName() + " needs more mana to use that skill.");
            listener.onPanelsRefresh();
            return;
        }

        afterPlayerAction(result);
    }

    private void performItem() {
        if (state.getInventory().isEmpty()) {
            listener.onStatusUpdate("Inventory is empty.");
            return;
        }

        List<StackedItem> stacked = inventoryService.getStackedItems();
        String selectedName = listener.onChooseItem(stacked);
        if (selectedName == null) {
            return;
        }

        Character target = listener.onChooseItemTarget(state.getPlayers());
        if (target == null) {
            return;
        }

        try {
            inventoryService.useItemByName(selectedName, target);
            listener.onLog("Used " + selectedName + " on " + target.getName() + ".");
        } catch (EmptyInventoryException e) {
            listener.onStatusUpdate(e.getMessage());
            return;
        } catch (IndexOutOfBoundsException e) {
            listener.onStatusUpdate("Invalid item selected.");
            return;
        }

        afterPlayerAction(new BattleResult("Item used.", 0, false, false));
    }

    private void performFlee(Character active) {
        double fleeChance = Math.random() * 100;
        if (fleeChance < GameConstants.FLEE_SUCCESS_CHANCE) {
            listener.onLog(active.getName() + " successfully fled the battle!");
            listener.onStatusUpdate("Fled the battle.");
            listener.onActionButtonsEnabled(false);
            listener.onBattleEnd(false);
            return;
        }

        listener.onLog(active.getName() + " failed to flee.");
        listener.onLog("All living party members take " + GameConstants.FLEE_DAMAGE + " damage.");
        for (Character player : state.getPlayers()) {
            if (player.isAlive()) {
                player.takeDamage(GameConstants.FLEE_DAMAGE);
                listener.onLog(player.getName() + " drops to " + player.getHp() + " HP.");
            }
        }
        listener.onPanelsRefresh();
        enemyTurn();
    }

    private void afterPlayerAction(BattleResult result) {
        listener.onPanelsRefresh();

        if (!state.hasLivingEnemies()) {
            listener.onBattleEnd(true);
            return;
        }

        state.setCurrentPlayerIndex(state.getCurrentPlayerIndex() + 1);
        listener.onStatusUpdate("Action complete: " + result.getMessage());
        nextPlayerTurn();
    }

    private void enemyTurn() {
        listener.onActionButtonsEnabled(false);
        if (!state.hasLivingEnemies()) {
            listener.onBattleEnd(true);
            return;
        }

        listener.onLog("\n--- Enemy Turn ---");
        for (Enemy enemy : state.getEnemies()) {
            if (!enemy.isAlive()) {
                continue;
            }
            BattleResult result = enemy.getAi().decideAction(enemy, state.getPlayers());
            listener.onLog(result.getMessage());
            listener.onPanelsRefresh();
            if (!state.hasLivingPlayers()) {
                break;
            }
        }

        if (!state.hasLivingPlayers()) {
            listener.onBattleEnd(false);
            return;
        }

        state.incrementTurn();
        for (Character player : state.getPlayers()) {
            if (player.isAlive()) {
                player.regenerateMana(10);
            }
        }

        state.setCurrentPlayerIndex(0);
        listener.onPanelsRefresh();
        nextPlayerTurn();
    }
}
