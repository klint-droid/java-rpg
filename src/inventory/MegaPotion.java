package inventory;

import characters.Character;

public class MegaPotion extends Item {

    public MegaPotion(){
        super(
            "Mega Potion",
            "Restores 50 HP",
            50
        );
    }

    @Override
    public boolean use(Character target){
        if(!target.isAlive()){
            System.out.println("Character is dead.");
            return false;
        }

        target.heal(getEffectValue());

        System.out.println(
            target.getName()
            + " healed for "
            + getEffectValue()
            + " HP."
        );
        return true;
    }
}
