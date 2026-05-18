package inventory;

import characters.Character;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Service layer for inventory operations.
 * Eliminates the duplicated item-stacking logic that was copy-pasted 3 times:
 *   - RpgGameUI.performItem()
 *   - RpgGameUI.updatePanels()
 *   - InventoryFrame.updateList()
 */
public class InventoryService {

    private final Inventory inventory;

    public InventoryService(Inventory inventory) {
        this.inventory = inventory;
    }

    /**
     * Returns a stacked view of inventory items (grouped by name with counts).
     */
    public List<StackedItem> getStackedItems() {
        Map<String, Integer> counts = new LinkedHashMap<>();
        Map<String, Item> examples = new LinkedHashMap<>();

        for (Item item : inventory.getItems()) {
            String key = item.getName();
            counts.put(key, counts.getOrDefault(key, 0) + 1);
            examples.putIfAbsent(key, item);
        }

        List<StackedItem> stacked = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            Item sample = examples.get(entry.getKey());
            stacked.add(new StackedItem(entry.getKey(), entry.getValue(), sample));
        }
        return stacked;
    }

    /**
     * Uses the first item matching the given name on the target character.
     * Returns the name of the item used.
     *
     * @throws EmptyInventoryException if inventory is empty
     * @throws IndexOutOfBoundsException if no item with that name exists
     */
    public String useItemByName(String itemName, Character target)
            throws EmptyInventoryException {
        if (inventory.isEmpty()) {
            throw new EmptyInventoryException("Inventory is empty.");
        }

        List<Item> items = inventory.getItems();
        int removeIndex = -1;
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getName().equals(itemName)) {
                removeIndex = i;
                break;
            }
        }

        if (removeIndex == -1) {
            throw new IndexOutOfBoundsException("Item not found: " + itemName);
        }

        inventory.useItem(removeIndex, target);
        return itemName;
    }

    public Inventory getInventory() {
        return inventory;
    }
}
