package characters;
import constants.GameConstants;
import enums.CharacterType;
import results.BattleResult;
public class Warrior extends Character {

    public Warrior(String name){
        super(name, 450, 100, 50, 20, CharacterType.WARRIOR);
    }

    @Override
    public BattleResult attack(Character target) {
        
        if(didMiss(GameConstants.WARRIOR_MISS_CHANCE)){
            return new BattleResult(getName() + " misses the attack.", 0, false, true);
        }

        double damage = calculateDamage(target, 1.0);

        boolean criticalHit = false;

        if(didCrit(GameConstants.WARRIOR_CRIT_CHANCE)){
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
        if(!consumeMana(15)){
            return new BattleResult(getName() + " tried to use Shield Bash but does not have enough mana.", 0, false, false);
        }

        double damage = calculateDamage(target, 2.0);
        
        target.takeDamage(damage);
        boolean targetSlain = !target.isAlive();

        return new BattleResult(
            getName()
            + " used Shield Bash on "
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
