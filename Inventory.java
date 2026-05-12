import java.util.ArrayList;
public class Inventory {
    private ArrayList<Item> items;

    public Inventory() {
        items = new ArrayList<Item>();
    }

    public void addStarterItems(){
        items.add(new Item("Health Potion", "Restores 20 HP", 20));
        items.add(new Item("Mana Potion", "Restores Magical Energy", 10));
        items.add(new Item("Revive Potion", "Revives a Dead Character", 0));
    }

    public void displayItems(){
        if(items.isEmpty()){
            System.out.println("Inventory is empty.");
            return;
        }

        for(int i = 0; i < items.size(); i++){
            Item item = items.get(i);

            System.out.println((i + 1) + ". " + item.getName() + " - " + item.getDescription());
        }
    }

    public void useItem(int index, Character target) throws EmptyInventoryException{

        if(items.isEmpty()){
            throw new EmptyInventoryException("Inventory is empty.");
        }

        Item item = items.get(index);

        target.heal(item.getEffectValue());

        System.out.println(target.getName() + " healed for " + item.getEffectValue() + " HP.");

        items.remove(index);
    }

    public boolean isEmpty(){
        return items.isEmpty();
    }

    public void addItem(Item item){
        items.add(item);
    }

    public ArrayList<Item> getItems(){
        return items;
    }
}
