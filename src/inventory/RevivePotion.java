package inventory;
import characters.Character;
public class RevivePotion extends Item {
    public RevivePotion() {
        super("Revive Potion", "Revives character", 0);
    }

    @Override
    public boolean use (Character target) {
        if(target.isAlive()){
            System.out.println("Character is already alive.");
            return false;
        }
        double reviveHp = target.getMaxHp() * 0.5;
        target.setHp(reviveHp);
        System.out.println(target.getName() + " has been revived with " + reviveHp + " HP.");
        return true;
    }
    
}
