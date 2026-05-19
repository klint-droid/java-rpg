package factory;
import characters.Archer;
import characters.Character;
import characters.Mage;
import characters.Warrior;
public class CharacterFactory {
    private CharacterFactory() {}

    public static Character createCharacter(int choice) {
        switch (choice) {
            case 1:
                return new Warrior();
            case 2:
                return new Mage();
            case 3:
                return new Archer();
            default:
                throw new IllegalArgumentException("Invalid choice");
        }
    }

    public static Character creatCharacterByType(String type){
        switch (type.toUpperCase()) {
            case "WARRIOR":
                return new Warrior();
            case "MAGE":
                return new Mage();
            case "ARCHER":
                return new Archer();
        
            default:
                throw new IllegalArgumentException("Invalid type");
        }
    }
}

