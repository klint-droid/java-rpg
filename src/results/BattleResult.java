package results;

public class BattleResult {
    private String message;
    private double damage;
    private boolean critical;
    private boolean missed;

    public BattleResult(String message, double damage, boolean critical, boolean missed) {
        this.message = message;
        this.damage = damage;
        this.critical = critical;
        this.missed = missed;
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
}

