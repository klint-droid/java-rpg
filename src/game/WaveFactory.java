package game;

import java.util.ArrayList;
import enemies.*;

/**
 * Single source of truth for creating enemy waves.
 * Previously duplicated in RpgGameUI.createWave() and GameManager.createWave().
 */
public class WaveFactory {

    private WaveFactory() {}

    public static ArrayList<Enemy> createWave(int wave) {
        ArrayList<Enemy> waveEnemies = new ArrayList<>();
        switch (wave) {
            case 1 -> waveEnemies.add(new Goblin());
            case 2 -> {
                waveEnemies.add(new Orc());
                waveEnemies.add(new GoblinArcher());
            }
            case 3 -> waveEnemies.add(new DarkMage());
            case 4 -> waveEnemies.add(new Dragon());
            default -> waveEnemies.add(new ShadowBeast());
        }
        return waveEnemies;
    }
}
