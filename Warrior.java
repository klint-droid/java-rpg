public class Warrior extends Character {

    public Warrior(String name){
        super(name, 150, 30, 20);
    }

    @Override
    public void attack(Character target) {
        
        if(didMiss(10)){
            System.out.println(getName() + " misses the attack.");
            return;
        }

        double damage = calculateDamage(target, 1.0);

        if(didCrit(15)){
            damage *= 2;

            System.out.println(getName() + " landed a critical hit!");
        }
        
        target.takeDamage(damage);

        System.out.println(getName() + " attacks " + target.getName() + " for " + damage + " damage.");
    }

    @Override
    public void defend(){
        
        setDefending(true);

        System.out.println(getName() + " takes a defensive stance!");
    }

    @Override
    public void useSkill(Character target){

        double damage = calculateDamage(target, 2.0);
        
        target.takeDamage(damage);

        System.out.println(getName() + " uses Power Strike on " + target.getName() + " for " + damage + " damage.");
    }
}