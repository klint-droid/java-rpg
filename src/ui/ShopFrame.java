package ui;

import constants.GameConstants;
import inventory.HealthPotion;
import inventory.Inventory;
import inventory.ManaPotion;
import inventory.MegaPotion;
import inventory.RevivePotion;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class ShopFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private final Inventory inventory;
    private final RpgGameUI parent;
    private double gold;
    private JLabel goldLabel;

    public ShopFrame(Inventory inventory, double gold, RpgGameUI parent) {
        super("Shop");
        this.inventory = inventory;
        this.gold = gold;
        this.parent = parent;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(340, 260);
        setLocationRelativeTo(parent);
        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridLayout(5,1,6,6));
        JButton h = new JButton("Health Potion - " + GameConstants.HEALTH_POTION_PRICE + " gold");
        JButton m = new JButton("Mega Potion - " + GameConstants.MEGA_POTION_PRICE + " gold");
        JButton mp = new JButton("Mana Potion - " + GameConstants.MANA_POTION_PRICE + " gold");
        JButton r = new JButton("Revive Potion - " + GameConstants.REVIVE_POTION_PRICE + " gold");
        JButton close = new JButton("Close");

        h.addActionListener(e -> buy(new HealthPotion(), GameConstants.HEALTH_POTION_PRICE));
        m.addActionListener(e -> buy(new MegaPotion(), GameConstants.MEGA_POTION_PRICE));
        mp.addActionListener(e -> buy(new ManaPotion(), GameConstants.MANA_POTION_PRICE));
        r.addActionListener(e -> buy(new RevivePotion(), GameConstants.REVIVE_POTION_PRICE));
        close.addActionListener(e -> dispose());

        panel.add(h);
        panel.add(m);
        panel.add(mp);
        panel.add(r);
        panel.add(close);

        goldLabel = new JLabel("Gold: " + gold);
        add(goldLabel, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
    }

    private void buy(inventory.Item item, double price) {
        if (gold >= price) {
            inventory.addItem(item);
            gold -= price;
            goldLabel.setText("Gold: " + gold);
            JOptionPane.showMessageDialog(this, "Bought " + item.getName());
            parent.setGold(gold);
            parent.refreshPanels();
        } else {
            JOptionPane.showMessageDialog(this, "Not enough gold.");
        }
    }
}

