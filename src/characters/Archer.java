package characters;
import constants.GameConstants;
import enums.CharacterType;
import results.BattleResult;
public class Archer extends Character {

    public Archer(String name){
        super(name, 100, 40, 40, 5, CharacterType.ARCHER);
    }

    @Override
    public BattleResult attack(Character target){

        if(didMiss(GameConstants.ARCHER_MISS_CHANCE)){

            return new BattleResult(getName() + " misses the attack.", 0, false, true);
        }

        double damage =
            calculateDamage(target, 1.0);

        boolean criticalHit = false;

        if(didCrit(GameConstants.ARCHER_CRIT_CHANCE)){

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
        if(!consumeMana(18)){
            return new BattleResult(getName() + " tried to use Arrow Rain but does not have enough mana.", 0, false, false);
        }

        double totalDamage = 0;

        for(double i = 1; i <= 3; i++){

            double damage =
                calculateDamage(target, 0.7);

            target.takeDamage(damage);

            totalDamage += damage;

            System.out.println(
                "Arrow "
                + i
                + " hits for "
                + damage
                + " damage."
            );
        }

        return new BattleResult(
            getName()
            + " used Arrow Rain on "
            + target.getName()
            + " for "
            + totalDamage
            + " damage.",
            totalDamage,
            false,
            false
        );
    }

}

