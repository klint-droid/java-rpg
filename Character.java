public abstract class Character {
    private String name;
    private double hp;
    private double maxHp;
    private double atkPower;
    private double defPower;
    private boolean taunted;
    private int tauntTurns;

    // Constructor
    public Character(String name, double maxHp, double atkPower, double defPower) {
        this.name = name;
        this.hp = maxHp;
        this.maxHp = maxHp;
        this.atkPower = atkPower;
        this.defPower = defPower;
        this.taunted = false;
        this.tauntTurns = 0;
    }

    // Getters and Setters

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

    public void takeDamage(double damage){
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
    // Abstract methods

    public abstract void attack(Character target);
    public abstract void defend();
    public abstract void useSkill(Character target);

}
