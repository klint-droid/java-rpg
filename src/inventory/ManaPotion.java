package inventory;

import characters.Character;

public class ManaPotion extends Item {
    public ManaPotion() {
        super("Mana Potion", "Restores 30 MP", 30);
    }

    @Override
    public void use(Character target) {
        if (!target.isAlive()) {
            System.out.println("Character is dead.");
            return;
        }

        target.regenerateMana(getEffectValue());
        System.out.println(target.getName() + " restored " + getEffectValue() + " MP.");
    }
}

