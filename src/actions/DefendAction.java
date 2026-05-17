package actions;

import characters.Character;
import results.BattleResult;
public class DefendAction implements BattleAction {
    private Character character;

    public DefendAction(Character character) {
        this.character = character;
    }

    @Override
    public BattleResult execute() {
        character.defend();
        return new BattleResult(character.getName() + " defends.", 0, false, false);
    }
}

