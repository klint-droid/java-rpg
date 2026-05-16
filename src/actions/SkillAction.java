package actions;

import characters.Character;
import results.BattleResult;
public class SkillAction implements BattleAction {
    private Character user;
    private Character target;

    public SkillAction(Character user, Character target) {
        this.user = user;
        this.target = target;
    }

    @Override
    public BattleResult execute() {
       return user.useSkill(target);
    }
}
