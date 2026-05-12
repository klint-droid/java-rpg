import java.util.Random;

public class Mage extends Character {
    private Random random = new Random();
    private double magicShieldBoost = 5;

    public Mage(String name) {
        super(name, 100, 40, 5);
    }

    @Override
    public void attack(Character target){

        int roll = random.nextInt(100);

        if(roll < 10){
            System.out.println(getName() + " misses the attack.");
            return;
        }

        double damage = (getAtkPower() + 10) - target.getDefPower();

        if(roll >= 80){
            damage *= 2;
            System.out.println(getName() + " landed a magical critical hit!");
        }

        if(damage < 0) damage = 0;
        
        target.takeDamage(damage);

        System.out.println(getName() + " casts a fireball to " + target.getName() + " for " + damage + " damage.");
    }

    @Override
    public void defend(){
        double boostedDefPower = getDefPower() + magicShieldBoost;

        System.out.println(getName() + " cast a magic shield with boosted defense power of " + boostedDefPower);
    }

    @Override
    public void useSkill(Character target) {

        double damage = (getAtkPower() * 2);

        if(damage < 0) damage = 0;
        
        target.takeDamage(damage);

        System.out.println(getName() + " casts a fireball on " + target.getName() + " for " + damage + " damage.");
    }
}
