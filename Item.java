public class Item {
    private String name;
    private String description;
    private double effectValue;

    public Item(String name, String description, double effectValue) {
        this.name = name;
        this.description = description;
        this.effectValue = effectValue;
    }

    public String getName() {
    return name;
    }

    public String getDescription() {
        return description;
    }

    public double getEffectValue() {
        return effectValue;
    }
}
