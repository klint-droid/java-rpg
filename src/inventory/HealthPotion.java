package inventory;
import characters.Character;
public class HealthPotion extends Item{
    public HealthPotion() {
        super("Health Potion", "Restores 20 HP", 20);
    }

    @Override
    public boolean use(Character target) {
        if(!target.isAlive()){
            System.out.println("Character is dead.");
            return false;
        }
        target.heal(getEffectValue());
        System.out.println(target.getName() + " healed for " + getEffectValue() + " HP.");
        return true;
    }
}
