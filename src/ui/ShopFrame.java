package ui;

import constants.GameConstants;
import game.GameState;
import inventory.HealthPotion;
import inventory.Item;
import inventory.ManaPotion;
import inventory.MegaPotion;
import inventory.RevivePotion;
import shop.ShopService;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Standalone shop window. Uses ShopService for purchase logic
 * instead of duplicating buy code.
 */
public class ShopFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private final GameState state;
    private final AssetManager assetManager;
    private final RpgGameUI parent;
    private JLabel goldLabel;

    public ShopFrame(GameState state, AssetManager assetManager, RpgGameUI parent) {
        super("Shop");
        this.state = state;
        this.assetManager = assetManager;
        this.parent = parent;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(340, 260);
        setLocationRelativeTo(parent);
        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridLayout(5, 1, 6, 6));
        JButton h = createShopButton("Health Potion", GameConstants.HEALTH_POTION_PRICE, "item_health.png");
        JButton m = createShopButton("Mega Potion", GameConstants.MEGA_POTION_PRICE, "item_mega.png");
        JButton mp = createShopButton("Mana Potion", GameConstants.MANA_POTION_PRICE, "item_mana.png");
        JButton r = createShopButton("Revive Potion", GameConstants.REVIVE_POTION_PRICE, "item_revive.png");
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

        goldLabel = new JLabel("Gold: " + state.getGold(), JLabel.CENTER);
        goldLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        goldLabel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(goldLabel, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
    }

    private JButton createShopButton(String label, double price, String iconFile) {
        ImageIcon icon = assetManager.loadIcon(iconFile, 32, 32);
        JButton button = new JButton(label + " - " + price + " gold", icon);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        button.setHorizontalAlignment(JButton.LEFT);
        button.setBackground(new Color(50, 60, 80));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(130, 140, 170), 2),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        return button;
    }

    private void buy(Item item, double price) {
        if (ShopService.buyItem(item, price, state)) {
            goldLabel.setText("Gold: " + state.getGold());
            JOptionPane.showMessageDialog(this, "Bought " + item.getName());
            parent.refreshPanels();
        } else {
            JOptionPane.showMessageDialog(this, "Not enough gold.");
        }
    }
}
