public class Archer extends Character {

    public Archer(String name){
        super(name, 100, 40, 5);
    }

    @Override
    public void attack(Character target){

        if(didMiss(15)){

            System.out.println(getName() + " misses the shot.");
            return;
        }

        double damage =
            calculateDamage(target, 1.0);

        if(didCrit(25)){

            damage *= 2;

            System.out.println("Critical headshot!");
        }

        target.takeDamage(damage);

        System.out.println(
            getName()
            + " shoots "
            + target.getName()
            + " for "
            + damage
            + " damage."
        );
    }

    @Override
    public void defend(){
        
        setDefending(true);
        
        System.out.println(getName() + " takes a defensive stance!");
    }
    
    @Override
    public void useSkill(Character target){

        double totalDamage = 0;

        for(int i = 1; i <= 3; i++){

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

        System.out.println(
            getName()
            + " used Triple Shot for "
            + totalDamage
            + " total damage."
        );
    }

}
