public class Mage extends Character {

    public Mage(String name) {
        super(name, 100, 40, 5);
    }

    @Override
    public void attack(Character target){
            
        if(didMiss(10)){

            System.out.println(getName() + " misses the spell.");
            return;
        }

        double damage =
            calculateDamage(target, 1.2);

        if(didCrit(20)){

            damage *= 2;

            System.out.println(
                getName()
                + " landed a magical critical hit!"
            );
        }

        target.takeDamage(damage);

        System.out.println(
            getName()
            + " casts Fireball on "
            + target.getName()
            + " for "
            + damage
            + " damage."
        );
    }

    @Override
    public void defend(){
        setDefending(true);

        System.out.println(getName() + " casts a magic shield!");
    }

    @Override
    public void useSkill(Character target){

        double damage =
            calculateDamage(target, 2.5);

        target.takeDamage(damage);

        System.out.println(
            getName()
            + " casts Inferno Blast on "
            + target.getName()
            + " for "
            + damage
            + " damage."
        );
    }
}
