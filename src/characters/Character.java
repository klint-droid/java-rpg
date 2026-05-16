package characters;
import java.util.Random;
import enums.CharacterType;
import results.BattleResult;
public abstract class Character {
    private String name;
    private double hp;
    private double maxHp;
    private double atkPower;
    private double defPower;
    private boolean taunted;
    private int tauntTurns;
    private boolean defending;
    private CharacterType characterType;
    protected Random random = new Random();

    // Constructor
    public Character(String name, double maxHp, double atkPower, double defPower, CharacterType characterType) {
        this.name = name;
        this.hp = maxHp;
        this.maxHp = maxHp;
        this.atkPower = atkPower;
        this.defPower = defPower;
        this.taunted = false;
        this.tauntTurns = 0;
        this.defending = false;
        this.characterType = characterType;
    }

    // Getters and Setters

    public CharacterType getCharacterType() {
        return characterType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getHp() {
        return hp;
    }

    public void setHp(double hp) {
        this.hp = hp;
    }

    public double getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(double maxHp) {
        this.maxHp = maxHp;
    }

    public double getAtkPower() {
        return atkPower;
    }

    public void setAtkPower(double atkPower) {
        this.atkPower = atkPower;
    }

    public double getDefPower() {
        return defPower;
    }

    public void setDefPower(double defPower) {
        this.defPower = defPower;
    }

    public boolean isDefending() {
        return defending;
    }

    public void setDefending(boolean defending) {
        this.defending = defending;
    }

    public boolean isTaunted() {
        return taunted;
    }

    public void setTaunted(boolean taunted) {
        this.taunted = taunted;
    }

    public int getTauntTurns() {
        return tauntTurns;
    }

    public void setTauntTurns(int tauntTurns) {
        this.tauntTurns = tauntTurns;
    }

    // Common methods
    protected boolean chanceSuccess(int percent){
        Random random = new Random();
        return random.nextInt(100) < percent;
    }

    public void takeDamage(double damage){
        if(defending){
            damage *= 0.5;

            System.out.println(name + " reduces damage by defending.");

            defending = false;
        }

        hp -= damage;
        if(hp < 0){
            hp = 0;
        }
    }

    public void heal(double amount){
        hp += amount;
        if(hp > maxHp){
            hp = maxHp;
        }
    }

    public boolean isAlive(){
        return hp > 0;
    }

    public void reduceTauntTurns(){
        if(tauntTurns > 0){
            tauntTurns--;
            if(tauntTurns == 0){
                taunted = false;

                System.out.println(getName() + " is no longer taunted.");
            }
        }
    }
    public String displayStats(){
        return "Name: " + name + "\nHP: " + hp + "/" + maxHp + "\nAttack Power: " + atkPower + "\nDefense Power: " + defPower;
    }

    protected boolean didMiss(int missChance){
        return random.nextInt(100) < missChance;
    }

    protected boolean didCrit(int critChance){
        return random.nextInt(100) < critChance;
    }

    protected double calculateDamage(Character target, double multiplier){
        double damage = (getAtkPower() * multiplier) - target.getDefPower();

        return Math.max(1, damage);
    }
    // Abstract methods

    public abstract BattleResult attack(Character target);
    public abstract void defend();
    public abstract BattleResult useSkill(Character target);

}
