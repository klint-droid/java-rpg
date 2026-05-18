package ui;

import inventory.EmptyInventoryException;
import inventory.InventoryService;
import inventory.StackedItem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Standalone inventory window. Uses InventoryService for item stacking
 * instead of duplicating the LinkedHashMap grouping logic.
 * Enhanced with RPG-themed dark styling.
 */
public class InventoryFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private static final Color DARK_BG = new Color(22, 24, 34);
    private static final Color LIST_BG = new Color(28, 30, 42);
    private static final Color TEXT_PRIMARY = new Color(230, 235, 250);
    private static final Color TEXT_SECONDARY = new Color(170, 175, 200);
    private static final Color GOLD_TEXT = new Color(255, 215, 80);
    private static final Color BUTTON_BG = new Color(45, 50, 68);

    private final InventoryService inventoryService;
    private final AssetManager assetManager;
    private final RpgGameUI parent;

    private DefaultListModel<String> model;
    private JList<String> list;
    private List<String> keys;

    public InventoryFrame(InventoryService inventoryService, AssetManager assetManager, RpgGameUI parent) {
        super("\uD83C\uDF92 Inventory");
        this.inventoryService = inventoryService;
        this.assetManager = assetManager;
        this.parent = parent;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(400, 460);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(DARK_BG);
        initUI();
    }

    private void initUI() {
        model = new DefaultListModel<>();
        list = new JList<>(model);
        list.setVisibleRowCount(10);
        list.setBackground(LIST_BG);
        list.setForeground(TEXT_PRIMARY);
        list.setSelectionBackground(new Color(60, 70, 100));
        list.setSelectionForeground(GOLD_TEXT);
        // renderer to show icons next to stacked item text
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> listComp, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(listComp, value, index, isSelected, cellHasFocus);
                lbl.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
                lbl.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
                lbl.setOpaque(true);
                if (!isSelected) {
                    lbl.setBackground(LIST_BG);
                    lbl.setForeground(TEXT_PRIMARY);
                }
                if (index >= 0 && index < keys.size()) {
                    String name = keys.get(index).toLowerCase();
                    String iconFile = "item_generic.png";
                    if (name.contains("health")) iconFile = "item_health.png";
                    else if (name.contains("mega")) iconFile = "item_mega.png";
                    else if (name.contains("mana")) iconFile = "item_mana.png";
                    else if (name.contains("revive")) iconFile = "item_revive.png";
                    ImageIcon ic = assetManager.loadIcon(iconFile, 36, 36);
                    if (ic != null) lbl.setIcon(ic);
                } else {
                    lbl.setIcon(null);
                }
                return lbl;
            }
        });

        updateList();

        JScrollPane sp = new JScrollPane(list);
        sp.getViewport().setBackground(LIST_BG);
        sp.setBorder(BorderFactory.createLineBorder(new Color(60, 65, 90), 1));

        JButton useBtn = new JButton("Use on... (select target)");
        useBtn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        useBtn.setBackground(new Color(50, 120, 80));
        useBtn.setForeground(TEXT_PRIMARY);
        useBtn.setFocusPainted(false);
        useBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 160, 100, 150), 2),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        useBtn.addActionListener(e -> {
            int idx = list.getSelectedIndex();
            if (idx < 0) return;
            String key = keys.get(idx);
            characters.Character target = parent.choosePlayerTargetDialog();
            if (target == null) return;
            try {
                boolean success = inventoryService.useItemByName(key, target);
                if (success) {
                    parent.appendLogPublic("Used " + key + " on " + target.getName() + " (from Inventory window)");
                } else {
                    JOptionPane.showMessageDialog(InventoryFrame.this, key + " is not applicable on " + target.getName() + ".");
                }
                parent.refreshPanels();
                updateList();
            } catch (EmptyInventoryException ex) {
                JOptionPane.showMessageDialog(InventoryFrame.this, ex.getMessage());
            } catch (IndexOutOfBoundsException ex) {
                JOptionPane.showMessageDialog(InventoryFrame.this, "Item not found.");
            }
        });

        JButton close = new JButton("Close");
        close.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        close.setBackground(new Color(100, 40, 40));
        close.setForeground(TEXT_PRIMARY);
        close.setFocusPainted(false);
        close.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(160, 60, 60), 2),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        close.addActionListener(e -> dispose());

        JPanel bottom = new JPanel(new GridLayout(1, 2, 10, 10));
        bottom.setBackground(DARK_BG);
        bottom.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        bottom.add(useBtn);
        bottom.add(close);

        JLabel title = new JLabel("\uD83C\uDF92 Items (stacked):", JLabel.LEFT);
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        title.setForeground(GOLD_TEXT);
        title.setOpaque(true);
        title.setBackground(DARK_BG);
        title.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        add(title, BorderLayout.NORTH);
        add(sp, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private void updateList() {
        model.clear();
        keys = new ArrayList<>();
        List<StackedItem> stacked = inventoryService.getStackedItems();
        for (StackedItem si : stacked) {
            model.addElement(si.getDisplayText());
            keys.add(si.getName());
        }
    }
}
