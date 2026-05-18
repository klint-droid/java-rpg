package inventory;

import characters.Character;
import java.util.ArrayList;

public class Inventory {
    private final ArrayList<Item> items;

    public Inventory() {
        items = new ArrayList<>();
    }

    public void addStarterItems(){
        items.add(new HealthPotion());
        items.add(new MegaPotion());
        items.add(new ManaPotion());
        items.add(new RevivePotion());
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

    public boolean useItem(int index, Character target) throws EmptyInventoryException{

        if(items.isEmpty()){
            throw new EmptyInventoryException("Inventory is empty.");
        }

        Item item = items.get(index);

        boolean success = item.use(target);

        if (success) {
            items.remove(index);
        }

        return success;
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

