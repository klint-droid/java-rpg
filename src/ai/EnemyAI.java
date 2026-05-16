package ai;

import java.util.ArrayList;

import enemies.Enemy;
import characters.Character;
import results.BattleResult;

public interface EnemyAI {

    BattleResult decideAction(
        Enemy enemy,
        ArrayList<Character> players
    );
}