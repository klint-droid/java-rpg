package ui;

import characters.Character;
import enemies.Enemy;
import inventory.Item;
import inventory.StackedItem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicProgressBarUI;

/**
 * Factory methods for creating UI cards (player, enemy, inventory item).
 * Extracted from RpgGameUI (lines 1101-1213) to follow Single Responsibility.
 * Contains only rendering logic — no game state or business logic.
 *
 * Enhanced with RPG-themed glowing panels, gradient backgrounds, and
 * styled health/mana bars.
 */
public class CardRenderer {

    private final AssetManager assetManager;

    // --- RPG Color Palette ---
    private static final Color CARD_BG = new Color(28, 30, 42);
    private static final Color CARD_BG_LIGHT = new Color(38, 42, 58);
    private static final Color ENEMY_CARD_BG = new Color(50, 25, 30);
    private static final Color ENEMY_CARD_BORDER = new Color(220, 70, 70, 180);
    private static final Color PLAYER_GLOW = new Color(100, 160, 255, 120);
    private static final Color HP_GREEN = new Color(80, 210, 120);
    private static final Color HP_RED = new Color(220, 60, 60);
    private static final Color MP_BLUE = new Color(100, 150, 255);
    private static final Color GOLD_ACCENT = new Color(255, 215, 80);
    private static final Color DEAD_OVERLAY = new Color(80, 20, 20, 160);
    private static final Color TEXT_PRIMARY = new Color(240, 240, 250);
    private static final Color TEXT_SECONDARY = new Color(180, 185, 210);
    private static final Color ITEM_BG = new Color(32, 36, 52);

    public CardRenderer(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    /**
     * Creates a styled player card with portrait, name, class, HP bar, and MP bar.
     * Features a glowing blue border and gradient background.
     */
    public JPanel createPlayerCard(Character player) {
        String cls = player.getClass().getSimpleName();
        String clsLower = cls.toLowerCase();
        ImageIcon portrait = assetManager.loadIcon("char_" + clsLower + ".png", 130, 130);

        boolean alive = player.isAlive();

        // Portrait label
        JLabel img = new JLabel(portrait);
        img.setHorizontalAlignment(SwingConstants.CENTER);
        img.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(alive ? PLAYER_GLOW : new Color(100, 40, 40, 150), 3),
            BorderFactory.createEmptyBorder(2, 2, 2, 2)));

        // Name + class label
        JLabel nameLabel = new JLabel(player.getName(), SwingConstants.CENTER);
        nameLabel.setForeground(alive ? TEXT_PRIMARY : HP_RED);
        nameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));

        JLabel classLabel = new JLabel(cls.toUpperCase(), SwingConstants.CENTER);
        classLabel.setForeground(alive ? GOLD_ACCENT : TEXT_SECONDARY);
        classLabel.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 11));

        // Status label for dead characters
        JLabel statusLabel = null;
        if (!alive) {
            statusLabel = new JLabel("\u2620 FALLEN", SwingConstants.CENTER);
            statusLabel.setForeground(HP_RED);
            statusLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        }

        // HP Bar
        JProgressBar hpBar = createStyledProgressBar(
            (int) player.getHp(), (int) player.getMaxHp(),
            alive ? HP_GREEN : HP_RED,
            "HP " + (int) player.getHp() + "/" + (int) player.getMaxHp());

        // MP Bar
        JProgressBar manaBar = createStyledProgressBar(
            (int) player.getMana(), (int) player.getMaxMana(),
            MP_BLUE,
            "MP " + (int) player.getMana() + "/" + (int) player.getMaxMana());

        // Stats panel
        JPanel statPanel = new JPanel(new GridLayout(2, 1, 2, 2));
        statPanel.setOpaque(false);
        statPanel.add(hpBar);
        statPanel.add(manaBar);

        // Header (name + class)
        JPanel header = new JPanel(new GridLayout(2, 1, 0, 0));
        header.setOpaque(false);
        header.add(nameLabel);
        header.add(classLabel);

        // Build card with gradient background
        JPanel card = new GradientPanel(CARD_BG, CARD_BG_LIGHT);
        card.setLayout(new BorderLayout(6, 6));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(alive ? new Color(80, 130, 220, 100) : new Color(120, 40, 40, 100), 2),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        card.add(header, BorderLayout.NORTH);
        card.add(img, BorderLayout.CENTER);
        if (!alive && statusLabel != null) {
            JPanel centerPanel = new JPanel(new BorderLayout());
            centerPanel.setOpaque(false);
            centerPanel.add(img, BorderLayout.CENTER);
            centerPanel.add(statusLabel, BorderLayout.SOUTH);
            card.add(centerPanel, BorderLayout.CENTER);
        } else {
            card.add(img, BorderLayout.CENTER);
        }
        card.add(statPanel, BorderLayout.SOUTH);
        return card;
    }

    /**
     * Creates a styled enemy card with portrait, name, type, and HP bar.
     * Features a menacing red glow border. Shows DEFEATED overlay for dead enemies.
     */
    public JPanel createEnemyCard(Enemy enemy) {
        // Try enemy-specific asset naming: enemy_<name>.png
        String nameKey = enemy.getName().toLowerCase().replaceAll("\\s+", "_");
        String typeKey = enemy.getEnemyType().toLowerCase().replaceAll("\\s+", "_");

        ImageIcon portrait = assetManager.loadIcon("enemy_" + nameKey + ".png", 130, 130);
        if (portrait == null || portrait.getIconWidth() <= 0) {
            portrait = assetManager.loadIcon("enemy_" + typeKey + ".png", 130, 130);
        }

        boolean alive = enemy.isAlive();

        // Portrait label
        JLabel img = new JLabel(portrait);
        img.setHorizontalAlignment(SwingConstants.CENTER);
        img.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(alive ? ENEMY_CARD_BORDER : new Color(80, 80, 80, 100), 3),
            BorderFactory.createEmptyBorder(2, 2, 2, 2)));

        // Name label
        JLabel nameLabel = new JLabel(enemy.getName(), SwingConstants.CENTER);
        nameLabel.setForeground(alive ? TEXT_PRIMARY : TEXT_SECONDARY);
        nameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));

        // Enemy type subtitle
        JLabel typeLabel = new JLabel(enemy.getEnemyType().toUpperCase(), SwingConstants.CENTER);
        typeLabel.setForeground(alive ? new Color(255, 130, 100) : TEXT_SECONDARY);
        typeLabel.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 11));

        // HP Bar
        JProgressBar hpBar = createStyledProgressBar(
            (int) enemy.getHp(), (int) enemy.getMaxHp(),
            alive ? HP_RED : new Color(80, 80, 80),
            "HP " + (int) enemy.getHp() + "/" + (int) enemy.getMaxHp());

        // Defeated overlay
        JLabel defeatedLabel = null;
        if (!alive) {
            defeatedLabel = new JLabel("\u2620 DEFEATED", SwingConstants.CENTER);
            defeatedLabel.setForeground(new Color(180, 60, 60));
            defeatedLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        }

        // Header
        JPanel header = new JPanel(new GridLayout(2, 1, 0, 0));
        header.setOpaque(false);
        header.add(nameLabel);
        header.add(typeLabel);

        // Build card
        JPanel card = new GradientPanel(ENEMY_CARD_BG, new Color(60, 32, 38));
        card.setLayout(new BorderLayout(6, 6));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(alive ? new Color(200, 60, 60, 120) : new Color(60, 60, 60, 100), 2),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        card.add(header, BorderLayout.NORTH);

        if (!alive && defeatedLabel != null) {
            JPanel centerPanel = new JPanel(new BorderLayout());
            centerPanel.setOpaque(false);
            centerPanel.add(img, BorderLayout.CENTER);
            centerPanel.add(defeatedLabel, BorderLayout.SOUTH);
            card.add(centerPanel, BorderLayout.CENTER);
        } else {
            card.add(img, BorderLayout.CENTER);
        }

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
     * Creates a styled inventory item card with larger icon, rarity-colored border,
     * name, count, and description.
     */
    public JPanel createInventoryItemCard(Item item, int count) {
        String keyName = item.getName().toLowerCase();
        String iconFile = "item_generic.png";
        Color rarityColor = new Color(110, 120, 140);

        if (keyName.contains("health")) {
            iconFile = "item_health.png";
            rarityColor = HP_GREEN;
        } else if (keyName.contains("mega")) {
            iconFile = "item_mega.png";
            rarityColor = new Color(180, 80, 220);
        } else if (keyName.contains("mana")) {
            iconFile = "item_mana.png";
            rarityColor = MP_BLUE;
        } else if (keyName.contains("revive")) {
            iconFile = "item_revive.png";
            rarityColor = GOLD_ACCENT;
        }

        ImageIcon icn = assetManager.loadIcon(iconFile, 48, 48);
        JLabel iconLabel = new JLabel(icn);
        iconLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(rarityColor, 2),
            BorderFactory.createEmptyBorder(3, 3, 3, 3)));

        JLabel text = new JLabel(item.getName() + " x" + count);
        text.setForeground(TEXT_PRIMARY);
        text.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));

        JLabel desc = new JLabel(item.getDescription());
        desc.setForeground(TEXT_SECONDARY);
        desc.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));

        JPanel textPanel = new JPanel(new BorderLayout(4, 2));
        textPanel.setOpaque(false);
        textPanel.add(text, BorderLayout.NORTH);
        textPanel.add(desc, BorderLayout.SOUTH);

        JPanel card = new JPanel(new BorderLayout(10, 8));
        card.setBackground(ITEM_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(rarityColor.getRed(), rarityColor.getGreen(), rarityColor.getBlue(), 80), 1),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        card.add(iconLabel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    // ========================= HELPERS =========================

    /**
     * Creates a styled progress bar with custom foreground color and text.
     */
    private JProgressBar createStyledProgressBar(int value, int max, Color fg, String text) {
        JProgressBar bar = new JProgressBar(0, Math.max(1, max));
        bar.setValue(Math.max(0, value));
        bar.setStringPainted(true);
        bar.setString(text);
        bar.setForeground(fg);
        bar.setBackground(new Color(30, 30, 40));
        bar.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
        bar.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 80), 1));
        bar.setPreferredSize(new Dimension(0, 20));

        // Custom UI for rounded corners effect
        bar.setUI(new BasicProgressBarUI() {
            @Override
            protected void paintDeterminate(Graphics g, javax.swing.JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = c.getWidth();
                int h = c.getHeight();

                // Background
                g2.setColor(bar.getBackground());
                g2.fillRoundRect(0, 0, w, h, 6, 6);

                // Foreground fill
                int fillW = (int) (w * ((double) bar.getValue() / bar.getMaximum()));
                if (fillW > 0) {
                    g2.setColor(bar.getForeground());
                    g2.fillRoundRect(0, 0, fillW, h, 6, 6);
                }

                // Text
                paintString(g2, 0, 0, w, h, 0, bar.getInsets());
                g2.dispose();
            }
        });

        return bar;
    }

    /**
     * A JPanel subclass that paints a vertical gradient background.
     */
    private static class GradientPanel extends JPanel {
        private final Color color1;
        private final Color color2;

        GradientPanel(Color c1, Color c2) {
            this.color1 = c1;
            this.color2 = c2;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
