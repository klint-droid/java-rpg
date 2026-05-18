package ui;

import inventory.EmptyInventoryException;
import inventory.InventoryService;
import inventory.StackedItem;

import java.awt.BorderLayout;
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
 */
public class InventoryFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private final InventoryService inventoryService;
    private final AssetManager assetManager;
    private final RpgGameUI parent;

    private DefaultListModel<String> model;
    private JList<String> list;
    private List<String> keys;

    public InventoryFrame(InventoryService inventoryService, AssetManager assetManager, RpgGameUI parent) {
        super("Inventory");
        this.inventoryService = inventoryService;
        this.assetManager = assetManager;
        this.parent = parent;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(360, 420);
        setLocationRelativeTo(parent);
        initUI();
    }

    private void initUI() {
        model = new DefaultListModel<>();
        list = new JList<>(model);
        list.setVisibleRowCount(10);
        // renderer to show small icons next to stacked item text
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> listComp, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(listComp, value, index, isSelected, cellHasFocus);
                lbl.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
                lbl.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
                if (index >= 0 && index < keys.size()) {
                    String name = keys.get(index);
                    String iconFile = name.toLowerCase().replaceAll("\\s+", "_") + ".png";
                    ImageIcon ic = assetManager.loadIcon(iconFile, 28, 28);
                    if (ic != null) lbl.setIcon(ic);
                } else {
                    lbl.setIcon(null);
                }
                return lbl;
            }
        });

        updateList();

        JScrollPane sp = new JScrollPane(list);

        JButton useBtn = new JButton("Use on... (select target)");
        useBtn.addActionListener(e -> {
            int idx = list.getSelectedIndex();
            if (idx < 0) return;
            String key = keys.get(idx);
            characters.Character target = parent.choosePlayerTargetDialog();
            if (target == null) return;
            try {
                inventoryService.useItemByName(key, target);
                parent.appendLogPublic("Used " + key + " on " + target.getName() + " (from Inventory window)");
                parent.refreshPanels();
                updateList();
            } catch (EmptyInventoryException ex) {
                JOptionPane.showMessageDialog(InventoryFrame.this, ex.getMessage());
            } catch (IndexOutOfBoundsException ex) {
                JOptionPane.showMessageDialog(InventoryFrame.this, "Item not found.");
            }
        });

        JButton close = new JButton("Close");
        close.addActionListener(e -> dispose());

        JPanel bottom = new JPanel(new GridLayout(1, 2, 8, 8));
        bottom.add(useBtn);
        bottom.add(close);

        add(new JLabel("Items (stacked):"), BorderLayout.NORTH);
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
