package game;

import java.util.ArrayList;

import characters.Archer;
import characters.Character;
import characters.Mage;
import characters.Warrior;
import constants.GameConstants;


public class GameController {

    /**
     * Callback interface for the UI to react to game-level events.
     */
    public interface GameEventListener {
        void onLog(String message);
        void onStatusUpdate(String message);
        void onPanelsRefresh();
    }

    private final GameState state;
    private final GameEventListener listener;

    public GameController(GameState state, GameEventListener listener) {
        this.state = state;
        this.listener = listener;
    }

    public GameState getState() {
        return state;
    }

    /**
     * Initialize a brand new game.
     */
    public void startNewGame(java.util.List<Integer> selectedClasses) {
        state.setPlayers(new ArrayList<>());
        state.setInventory(new inventory.Inventory());
        state.getInventory().addStarterItems();
        state.setCurrentWave(1);
        state.setEnemiesDefeated(0);
        state.setGold(GameConstants.STARTING_GOLD);
        state.setTurnCount(1);
        state.setCurrentPlayerIndex(0);
        createParty(selectedClasses);
    }

    /**
     * Load a saved game and apply state.
     *
     * @return true if load succeeded, false if no save found
     */
    public boolean loadSavedGame() {
        SaveManager saveManager = new SaveManager();
        SaveData saveData = saveManager.loadGame();

        if (saveData == null) {
            return false;
        }

        state.setPlayers(saveData.getPlayers());
        state.setInventory(saveData.getInventory());
        state.setCurrentWave(saveData.getCurrentWave());
        state.setGold(saveData.getGold());
        state.setEnemies(saveData.getEnemies());
        state.setEnemiesDefeated((saveData.getCurrentWave() - 1));
        state.setTurnCount(saveData.getTurnCount());
        state.setCurrentPlayerIndex(saveData.getCurrentPlayerIndex());
        return true;
    }

    /**
     * Starts the current wave by creating enemies from WaveFactory.
     */
    public void startWave() {
        state.setEnemies(WaveFactory.createWave((int) state.getCurrentWave()));
        state.setTurnCount(1);
        state.setCurrentPlayerIndex(0);
        listener.onLog("\n--- Wave " + state.getCurrentWave() + " begins! ---");
        listener.onPanelsRefresh();
        listener.onStatusUpdate("Wave " + state.getCurrentWave() + " started.");
    }

    /**
     * Handles end-of-battle victory: awards gold based on enemies defeated, increments wave.
     */
    public void handleVictory() {
        state.addEnemiesDefeated(state.getEnemies().size());
        double waveReward = 50 + (state.getEnemies().size() * 25);
        state.addGold(waveReward);
        listener.onLog("\nGOLD REWARD: +" + (int)waveReward + " gold");
    }

    /**
     * Advances to the next wave.
     */
    public void advanceWave() {
        state.setCurrentWave(state.getCurrentWave() + 1);
    }

    /**
     * Saves the current game state.
     */
    public void saveGame() {
        SaveManager saveManager = new SaveManager();
        saveManager.saveGame(state.getCurrentWave(), state.getGold(), state.getTurnCount(), state.getCurrentPlayerIndex(),
                state.getPlayers(), state.getEnemies(), state.getInventory());
    }

    private void createParty(java.util.List<Integer> selectedClasses) {
        String[] classNames = {"Warrior", "Mage", "Archer"};

        for (int classChoice : selectedClasses) {
            Character newPlayer = switch (classChoice) {
                case 0 -> new Warrior();
                case 1 -> new Mage();
                default -> new Archer();
            };

            state.getPlayers().add(newPlayer);
            listener.onLog(newPlayer.getName() + " joined the party as a " + classNames[classChoice] + "!");
            listener.onStatusUpdate("Created " + newPlayer.getName() + ".");
        }
        listener.onPanelsRefresh();
    }
}
