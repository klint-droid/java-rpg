package ui;

import characters.Character;
import enemies.Enemy;
import inventory.Item;
import inventory.StackedItem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

/**
 * Factory methods for creating UI cards (player, enemy, inventory item).
 * Extracted from RpgGameUI (lines 1101-1213) to follow Single Responsibility.
 * Contains only rendering logic — no game state or business logic.
 */
public class CardRenderer {

    private final AssetManager assetManager;

    public CardRenderer(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    /**
     * Creates a styled player card with portrait, name, HP bar, and MP bar.
     */
    public JPanel createPlayerCard(Character player) {
        String cls = player.getClass().getSimpleName().toLowerCase();
        ImageIcon portrait = assetManager.loadIcon("char_" + cls + ".png", 140, 140);

        JLabel img = new JLabel(portrait);
        img.setHorizontalAlignment(SwingConstants.CENTER);
        img.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 180), 3));

        JLabel nameLabel = new JLabel(player.getName(), SwingConstants.CENTER);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));

        JProgressBar hpBar = new JProgressBar(0, Math.max(1, (int) player.getMaxHp()));
        hpBar.setValue(Math.max(0, (int) player.getHp()));
        hpBar.setStringPainted(true);
        hpBar.setForeground(new Color(120, 220, 140));
        hpBar.setString("HP " + (int) player.getHp() + "/" + (int) player.getMaxHp());
        hpBar.setBackground(new Color(40, 40, 55));

        JProgressBar manaBar = new JProgressBar(0, Math.max(1, (int) player.getMaxMana()));
        manaBar.setValue(Math.max(0, (int) player.getMana()));
        manaBar.setStringPainted(true);
        manaBar.setForeground(new Color(140, 170, 255));
        manaBar.setString("MP " + (int) player.getMana() + "/" + (int) player.getMaxMana());
        manaBar.setBackground(new Color(40, 40, 55));

        JPanel statPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        statPanel.setOpaque(false);
        statPanel.add(hpBar);
        statPanel.add(manaBar);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(nameLabel, BorderLayout.CENTER);

        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setBackground(new Color(35, 38, 50));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 0, Color.BLACK),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        card.add(header, BorderLayout.NORTH);
        card.add(img, BorderLayout.CENTER);
        card.add(statPanel, BorderLayout.SOUTH);
        return card;
    }

    /**
     * Creates a styled enemy card with portrait, name, and HP bar.
     */
    public JPanel createEnemyCard(Enemy enemy) {
        String fileName = enemy.getName().toLowerCase().replaceAll("\\s+", "_") + ".png";
        ImageIcon portrait = assetManager.loadIcon(fileName, 140, 140);
        if (portrait == null || portrait.getIconWidth() <= 0) {
            portrait = assetManager.loadIcon(
                "enemy_" + enemy.getEnemyType().toLowerCase().replaceAll("\\s+", "_") + ".png",
                140, 140);
        }

        JLabel img = new JLabel(portrait);
        img.setHorizontalAlignment(SwingConstants.CENTER);
        img.setBorder(BorderFactory.createLineBorder(new Color(232, 112, 112), 3));

        JLabel nameLabel = new JLabel(enemy.getName(), SwingConstants.CENTER);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));

        JProgressBar hpBar = new JProgressBar(0, Math.max(1, (int) enemy.getMaxHp()));
        hpBar.setValue((int) enemy.getHp());
        hpBar.setStringPainted(true);
        hpBar.setForeground(new Color(232, 112, 112));
        hpBar.setString("HP " + (int) enemy.getHp() + "/" + (int) enemy.getMaxHp());
        hpBar.setBackground(new Color(40, 30, 30));

        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setBackground(new Color(45, 30, 30));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 80, 80), 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        card.add(nameLabel, BorderLayout.NORTH);
        card.add(img, BorderLayout.CENTER);
        card.add(hpBar, BorderLayout.SOUTH);
        return card;
    }

    /**
     * Creates a styled inventory item card with icon, name, count, and description.
     */
    public JPanel createInventoryItemCard(StackedItem stackedItem) {
        return createInventoryItemCard(stackedItem.getSample(), stackedItem.getCount());
    }

    /**
     * Creates a styled inventory item card with icon, name, count, and description.
     */
    public JPanel createInventoryItemCard(Item item, int count) {
        String keyName = item.getName().toLowerCase();
        String iconFile = "item_generic.png";
        if (keyName.contains("health")) iconFile = "item_health.png";
        else if (keyName.contains("mega")) iconFile = "item_mega.png";
        else if (keyName.contains("mana")) iconFile = "item_mana.png";
        else if (keyName.contains("revive")) iconFile = "item_revive.png";

        ImageIcon icn = assetManager.loadIcon(iconFile, 36, 36);
        JLabel iconLabel = new JLabel(icn);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        JLabel text = new JLabel(item.getName() + " x" + count);
        text.setForeground(Color.WHITE);
        text.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));

        JLabel desc = new JLabel(item.getDescription());
        desc.setForeground(new Color(190, 190, 210));
        desc.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));

        JPanel textPanel = new JPanel(new BorderLayout(4, 4));
        textPanel.setOpaque(false);
        textPanel.add(text, BorderLayout.NORTH);
        textPanel.add(desc, BorderLayout.SOUTH);

        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setBackground(new Color(35, 38, 50));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(110, 120, 140), 1),
            BorderFactory.createEmptyBorder(6, 6, 6, 6)));
        card.add(iconLabel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }
}
