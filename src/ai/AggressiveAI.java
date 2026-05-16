package ai;

import java.util.ArrayList;
import java.util.Random;

import characters.Character;
import enemies.Enemy;
import results.BattleResult;

public class AggressiveAI
implements EnemyAI {

    private Random random;

    public AggressiveAI(){
        random = new Random();
    }

    @Override
    public BattleResult decideAction(
        Enemy enemy,
        ArrayList<Character> players
    ){

        ArrayList<Character> livingPlayers =
            new ArrayList<>();

        for(Character player : players){

            if(player.isAlive()){
                livingPlayers.add(player);
            }
        }

        if(livingPlayers.isEmpty()){

            return new BattleResult(
                "No targets available.",
                0,
                false,
                false
            );
        }

        Character target =
            livingPlayers.get(
                random.nextInt(
                    livingPlayers.size()
                )
            );

        return enemy.attack(target);
    }
}