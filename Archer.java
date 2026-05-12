import java.util.Random;
public class Archer extends Character {
    
    private Random random = new Random();

    public Archer(String name){
        super(name, 100, 40, 5);
    }

    @Override
    public void attack(Character target){

        int roll = random.nextInt(100);

        if(roll < 10){
            System.out.println(getName() + " misses the attack.");
            return;
        }

        double damage = getAtkPower() - target.getDefPower();
        
        if (roll >= 75) {
            damage *= 2;
            System.out.println("Critical headshot hit!");
        }
        if(damage < 0) damage = 0;

        double critChance = random.nextDouble();

        if(critChance < 15.0){
            damage *= 2;
            System.out.println("Critical hit!");
        }
        
        target.takeDamage(damage);

        System.out.println(getName() + " shoots an arrow to " + target.getName() + " for " + damage + " damage.");
    }

    @Override
    public void defend(){
        
        System.out.println(getName() + " prepares to dodge the next attack.");
    }
    
    @Override
    public void useSkill(Character target){

        double totalDamage = 0;

        for(int i = 1; i <= 3; i++){
            double damage = (getAtkPower() / 3) * i - target.getDefPower();
            
            if(damage < 0) damage = 0;
            
            target.takeDamage(damage);

            totalDamage += damage;

            System.out.println("Arrow " + i + " hits " + target.getName() + " for " + damage + " damage.");
        }

        System.out.println(getName() + " use Triple Shot on " + target.getName() + " for a total of " + totalDamage + " damage.");
    }

}
