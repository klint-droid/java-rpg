package actions;

import characters.Character;
import results.BattleResult;
public class AttackAction implements BattleAction{
    private Character attacker;
    private Character target;

    public AttackAction(Character attacker, Character target) {
        this.attacker = attacker;
        this.target = target;
    }

    @Override
    public BattleResult execute() {
        return attacker.attack(target);
    }
}
