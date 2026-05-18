package shop;

import game.GameState;
import inventory.Item;

/**
 * Unified purchase logic for the shop.
 * Previously duplicated across: RpgGameUI.openShop(), RpgGameUI.buyItem(),
 * ShopFrame.buy(), and Shop.buyHealthPotion/buyMegaPotion/buyRevivePotion.
 */
public class ShopService {

    /**
     * Attempts to buy an item. Deducts gold from game state and adds item to inventory.
     *
     * @return true if purchase succeeded, false if insufficient gold
     */
    public static boolean buyItem(Item item, double price, GameState state) {
        if (state.getGold() >= price) {
            state.getInventory().addItem(item);
            state.addGold(-price);
            return true;
        }
        return false;
    }
}
