package inventory;

import characters.Character;

public class MegaPotion
extends Item {

    public MegaPotion(){

        super(
            "Mega Potion",
            "Restores 50 HP",
            50
        );
    }

    @Override
    public void use(Character target){

        if(!target.isAlive()){

            System.out.println(
                "Character is dead."
            );

            return;
        }

        target.heal(
            getEffectValue()
        );

        System.out.println(
            target.getName()
            + " healed for "
            + getEffectValue()
            + " HP."
        );
    }
}
