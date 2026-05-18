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
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Standalone shop window. Uses ShopService for purchase logic
 * instead of duplicating buy code.
 * Enhanced with RPG-themed dark styling.
 */
public class ShopFrame extends JDialog {
    private static final long serialVersionUID = 1L;

    private static final Color DARK_BG = new Color(22, 24, 34);
    private static final Color PANEL_BG = new Color(30, 33, 48);
    private static final Color GOLD_TEXT = new Color(255, 215, 80);
    private static final Color TEXT_PRIMARY = new Color(230, 235, 250);
    private static final Color BUTTON_BG = new Color(45, 50, 68);
    private static final Color BUTTON_BORDER = new Color(80, 90, 120);

    private final GameState state;
    private final AssetManager assetManager;
    private final RpgGameUI parent;
    private JLabel goldLabel;

    public ShopFrame(GameState state, AssetManager assetManager, RpgGameUI parent) {
        super(parent, "\uD83D\uDCB0 Shop", true);
        this.state = state;
        this.assetManager = assetManager;
        this.parent = parent;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(420, 380);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(DARK_BG);
        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridLayout(5, 1, 8, 8));
        panel.setBackground(DARK_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JButton h = createShopButton("Health Potion", "Restores 20 HP",
            GameConstants.HEALTH_POTION_PRICE, "item_health.png", new Color(80, 210, 120));
        JButton m = createShopButton("Mega Potion", "Restores 50 HP",
            GameConstants.MEGA_POTION_PRICE, "item_mega.png", new Color(180, 80, 220));
        JButton mp = createShopButton("Mana Potion", "Restores 30 MP",
            GameConstants.MANA_POTION_PRICE, "item_mana.png", new Color(100, 150, 255));
        JButton r = createShopButton("Revive Potion", "Revives fallen ally",
            GameConstants.REVIVE_POTION_PRICE, "item_revive.png", GOLD_TEXT);

        JButton close = new JButton("Close Shop");
        close.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        close.setBackground(new Color(100, 40, 40));
        close.setForeground(TEXT_PRIMARY);
        close.setFocusPainted(false);
        close.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(160, 60, 60), 2),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));

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

        goldLabel = new JLabel("\uD83D\uDCB0 Gold: " + (int) state.getGold(), JLabel.CENTER);
        goldLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        goldLabel.setForeground(GOLD_TEXT);
        goldLabel.setOpaque(true);
        goldLabel.setBackground(PANEL_BG);
        goldLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 140, 40, 100), 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        add(goldLabel, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
    }

    private JButton createShopButton(String label, String desc, double price, String iconFile, Color accentColor) {
        ImageIcon icon = assetManager.loadIcon(iconFile, 48, 48);
        String buttonText = "<html><b>" + label + "</b> - " + (int) price
            + " gold<br><small>" + desc + "</small></html>";
        JButton button = new JButton(buttonText, icon);
        button.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        button.setHorizontalAlignment(JButton.LEFT);
        button.setBackground(BUTTON_BG);
        button.setForeground(TEXT_PRIMARY);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(accentColor.getRed(), accentColor.getGreen(),
                accentColor.getBlue(), 100), 2),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        return button;
    }

    private void buy(Item item, double price) {
        if (ShopService.buyItem(item, price, state)) {
            goldLabel.setText("\uD83D\uDCB0 Gold: " + (int) state.getGold());
            JOptionPane.showMessageDialog(this, "Bought " + item.getName());
            parent.refreshPanels();
        } else {
            JOptionPane.showMessageDialog(this, "Not enough gold.");
        }
    }
}
