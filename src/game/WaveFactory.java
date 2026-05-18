package game;

import java.util.ArrayList;
import enemies.Enemy;

/**
 * Single source of truth for creating enemy waves.
 * Previously duplicated in RpgGameUI.createWave() and GameManager.createWave().
 */
public class WaveFactory {

    private WaveFactory() {}

    public static ArrayList<Enemy> createWave(int wave) {
        ArrayList<Enemy> waveEnemies = new ArrayList<>();
        switch (wave) {
            case 1 -> waveEnemies.add(new Enemy("Goblin", "Goblin", 120, 20, 5));
            case 2 -> {
                waveEnemies.add(new Enemy("Orc", "Orc", 150, 30, 10));
                waveEnemies.add(new Enemy("Goblin Archer", "Goblin", 200, 25, 5));
            }
            case 3 -> waveEnemies.add(new Enemy("Dark Mage", "Dark Mage", 300, 40, 8));
            case 4 -> waveEnemies.add(new Enemy("Dragon", "Boss", 350, 100, 20));
            default -> waveEnemies.add(new Enemy("Shadow Beast", "Elite", 280, 50, 12));
        }
        return waveEnemies;
    }
}
