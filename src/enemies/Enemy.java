package enemies;
import java.util.Random;

import constants.GameConstants;
import characters.Character;
import enums.CharacterType;
import results.BattleResult;
import ai.EnemyAI;
import ai.AggressiveAI;

public class Enemy extends Character {
    private Random random = new Random();

    private String enemyType;
    private EnemyAI ai;

    public Enemy(String name, String enemyType, double maxHp, double atkPower, double defPower) {
        super(name, maxHp, atkPower, defPower, CharacterType.ENEMY);
        this.enemyType = enemyType;
        this.ai = new AggressiveAI();
    }

    public String getEnemyType() {
        return enemyType;
    }

    public EnemyAI getAi(){
        return ai;
    }

    public void setAi(EnemyAI ai){
        this.ai = ai;
    }
    
    @Override
    public BattleResult attack(Character target){

        if(didMiss(GameConstants.ENEMY_MISS_CHANCE)){
            return new BattleResult(getName() + " misses the attack.", 0, false, true);
        }
        double damage =
            calculateDamage(target, 1.5);

        boolean criticalHit = false;

        if(didCrit(GameConstants.ENEMY_CRIT_CHANCE)){

            damage *= 2;

            criticalHit = true;
        }

        target.takeDamage(damage);

        return new BattleResult(
            getName()
            + (criticalHit
                ? " landed a critical hit on "
                : " attacked ")
            + target.getName()
            + " for "
            + damage
            + " damage.",
            damage,
            criticalHit,
            false
        );
    }

    @Override
    public void defend(){

        setDefending(true);

    }

    @Override
    public BattleResult useSkill(Character target){

        double damage =
            calculateDamage(target, 2.0);

        target.takeDamage(damage);

        return new BattleResult(
            getName()
            + " used Fireball on "
            + target.getName()
            + " for "
            + damage
            + " damage.",
            damage,
            false,
            false
        );
    }

    public void taunt(Character target){
        int tauntChance = random.nextInt(100);

        if(tauntChance < 50){
            target.setTaunted(true);
            target.setTauntTurns(1);
            System.out.println(getName() + " taunts " + target.getName() + "!");
            System.out.println(target.getName() + " is forced to attack next turn!");
        } else {
            System.out.println(getName() + " fails to taunt " + target.getName() + "!");
        }
    }
    public void enemyAction(Character target){
        int action  = random.nextInt(4);

        switch (action) {
            case 0:
                attack(target);
                break;
            case 1:
                defend();
                break;
            case 2:
                useSkill(target);
                break;
            case 3:
                taunt(target);
                break;
            default:
                break;
        }
    }
}

