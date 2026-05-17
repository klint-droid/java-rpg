package characters;
import enums.CharacterType;
import java.util.Random;
import results.BattleResult;
public abstract class Character {
    private String name;
    private double hp;
    private double maxHp;
    private double mana;
    private double maxMana;
    private double atkPower;
    private double defPower;
    private boolean taunted;
    private double tauntTurns;
    private boolean defending;
    private CharacterType characterType;
    protected Random random = new Random();

    // Constructor
    public Character(String name, double maxHp, double maxMana, double atkPower, double defPower, CharacterType characterType) {
        this.name = name;
        this.hp = maxHp;
        this.maxHp = maxHp;
        this.mana = maxMana;
        this.maxMana = maxMana;
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

    public double getMana() {
        return mana;
    }

    public void setMana(double mana) {
        this.mana = mana;
    }

    public double getMaxMana() {
        return maxMana;
    }

    public void setMaxMana(double maxMana) {
        this.maxMana = maxMana;
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

    public double getTauntTurns() {
        return tauntTurns;
    }

    public void setTauntTurns(double tauntTurns) {
        this.tauntTurns = tauntTurns;
    }

    // Common methods
    protected boolean chanceSuccess(double percent){
        return this.random.nextInt(100) < percent;
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

    public boolean consumeMana(double amount) {
        if (mana < amount) {
            return false;
        }
        mana -= amount;
        return true;
    }

    public void regenerateMana(double amount) {
        mana += amount;
        if (mana > maxMana) {
            mana = maxMana;
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
        return "Name: " + name
            + "\nHP: " + hp + "/" + maxHp
            + "\nMana: " + mana + "/" + maxMana
            + "\nAttack Power: " + atkPower
            + "\nDefense Power: " + defPower;
    }

    protected boolean didMiss(double missChance){
        return random.nextInt(100) < missChance;
    }

    protected boolean didCrit(double critChance){
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

