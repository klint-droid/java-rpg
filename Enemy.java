import java.util.Random;
public class Enemy extends Character {
    private Random random = new Random();

    private String enemyType;

    public Enemy(String name, String enemyType, double maxHp, double atkPower, double defPower) {
        super(name, maxHp, atkPower, defPower);
        this.enemyType = enemyType;
    }

    public String getEnemyType() {
        return enemyType;
    }

    @Override
    public void attack(Character target){

        double damage =
            calculateDamage(target, 1.0);

        if(didCrit(10)){

            damage *= 2;

            System.out.println(
                getEnemyType()
                + " landed a critical hit!"
            );
        }

        target.takeDamage(damage);

        System.out.println(
            getEnemyType()
            + " attacks "
            + target.getName()
            + " for "
            + damage
            + " damage."
        );
    }

    @Override
    public void defend(){

        setDefending(true);
        System.out.println(getEnemyType() + " takes a defensive stance!");

    }

    @Override
    public void useSkill(Character target){

        double damage =
            calculateDamage(target, 2.0);

        target.takeDamage(damage);

        System.out.println(
            getEnemyType()
            + " uses Savage Strike on "
            + target.getName()
            + " for "
            + damage
            + " damage."
        );
    }

    public void taunt(Character target){
        int tauntChance = random.nextInt(100);

        if(tauntChance < 50){
            target.setTaunted(true);
            target.setTauntTurns(1);
            System.out.println(getName() + " taunts " + target.getName() + "!");
            System.out.println(target.getName() + " is forced to attack next turn!");
        } else {
            System.out.println(getName() + " fails to taunt " + target.getName() + "!");
        }
    }
    public void enemyAction(Character target){
        int action  = random.nextInt(4);

        switch (action) {
            case 0:
                attack(target);
                break;
            case 1:
                defend();
                break;
            case 2:
                useSkill(target);
                break;
            case 3:
                taunt(target);
                break;
            default:
                break;
        }
    }
}

