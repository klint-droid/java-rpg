package inventory;

/**
 * Immutable view of stacked items for display purposes.
 * Used by InventoryService to eliminate duplicated stacking logic.
 */
public class StackedItem {
    private final String name;
    private final int count;
    private final Item sample;

    public StackedItem(String name, int count, Item sample) {
        this.name = name;
        this.count = count;
        this.sample = sample;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public Item getSample() {
        return sample;
    }

    public String getDescription() {
        return sample.getDescription();
    }

    public String getDisplayText() {
        return name + " x" + count + " - " + sample.getDescription();
    }
}
