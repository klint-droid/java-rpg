package factory;
import characters.Archer;
import characters.Character;
import characters.Mage;
import characters.Warrior;
public class CharacterFactory {
    private CharacterFactory() {}

    public static Character createCharacter(int choice, String name) {
        switch (choice) {
            case 1:
                return new Warrior(name);
            case 2:
                return new Mage(name);
            case 3:
                return new Archer(name);
            default:
                throw new IllegalArgumentException("Invalid choice");
        }
    }

    public static Character creatCharacterByType(String type, String name){
        switch (type.toUpperCase()) {
            case "WARRIOR":
                return new Warrior(name);
            case "MAGE":
                return new Mage(name);
            case "ARCHER":
                return new Archer(name);
        
            default:
                throw new IllegalArgumentException("Invalid type");
        }
    }
}

