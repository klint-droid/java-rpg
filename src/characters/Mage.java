package characters;
import constants.GameConstants;
import enums.CharacterType;
import results.BattleResult;
public class Mage extends Character {

    public Mage(String name) {
        super(name, 200, 100, 20, 5, CharacterType.MAGE);
    }

    @Override
    public BattleResult attack(Character target){
            
        if(didMiss(GameConstants.MAGE_MISS_CHANCE)){

            return new BattleResult(getName() + " misses the attack.", 0, false, true);
        }

        double damage =
            calculateDamage(target, 1.2);

        boolean criticalHit = false;

        if(didCrit(GameConstants.MAGE_CRIT_CHANCE)){

            damage *= 2;

            criticalHit = true;
        }

        target.takeDamage(damage);
        boolean targetSlain = !target.isAlive();

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
            false,
            targetSlain
        );
    }

    @Override
    public void defend(){
        setDefending(true);
    }

    @Override
    public BattleResult useSkill(Character target){
        if(!consumeMana(20)){
            return new BattleResult(getName() + " tried to cast Fireball but does not have enough mana.", 0, false, false);
        }

        double damage =
            calculateDamage(target, 1.8);

        target.takeDamage(damage);
        boolean targetSlain = !target.isAlive();

        return new BattleResult(
            getName()
            + " used Fireball on "
            + target.getName()
            + " for "
            + damage
            + " damage.",
            damage,
            false,
            false,
            targetSlain
        );
    }
}

