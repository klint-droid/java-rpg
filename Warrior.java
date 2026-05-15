import java.util.Random;

public class Warrior extends Character {
    private Random random = new Random();

    public Warrior(String name){
        super(name, 150, 30, 20);
    }

    @Override
    public void attack(Character target) {
        int roll = random.nextInt(100);

        if(roll < 10){
            System.out.println(getName() + " misses the attack.");
            return;
        }

        double damage = getAtkPower() - target.getDefPower();

        if(roll >= 85){
            damage *= 2;
            System.out.println(getName() + " landed a critical hit!");
        }

        if(damage < 0) damage = 0;
        
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

        double damage = (getAtkPower() * 2) - target.getDefPower();

        if(damage < 0) damage = 0;
        
        target.takeDamage(damage);

        System.out.println(getName() + " uses skill on " + target.getName() + " for " + damage + " damage.");
    }
}