package results;

/**
 * Represents the result of a battle action, including damage, whether it missed,
 * and whether the target was slain by the attack or skill.
 */
public class BattleResult {
    private String message;
    private double damage;
    private boolean critical;
    private boolean missed;
    private boolean targetSlain;

    public BattleResult(String message, double damage, boolean critical, boolean missed) {
        this(message, damage, critical, missed, false);
    }

    public BattleResult(String message, double damage, boolean critical, boolean missed, boolean targetSlain) {
        this.message = message;
        this.damage = damage;
        this.critical = critical;
        this.missed = missed;
        this.targetSlain = targetSlain;
    }

    public String getMessage() {
        return message;
    }

    public double getDamage() {
        return damage;
    }

    public boolean isCritical() {
        return critical;
    }

    public boolean isMissed() {
        return missed;
    }

    public boolean isTargetSlain() {
        return targetSlain;
    }
}

