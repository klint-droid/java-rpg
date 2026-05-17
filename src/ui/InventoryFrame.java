package ui;

import inventory.EmptyInventoryException;
import inventory.Inventory;
import inventory.Item;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

public class InventoryFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private final Inventory inventory;
    private final RpgGameUI parent;

    private DefaultListModel<String> model;
    private JList<String> list;
    private List<String> keys;

    public InventoryFrame(Inventory inventory, RpgGameUI parent) {
        super("Inventory");
        this.inventory = inventory;
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
                if (index >= 0 && index < keys.size()) {
                    String name = keys.get(index);
                    String iconFile = name.toLowerCase().replaceAll("\\s+", "_") + ".png";
                    ImageIcon ic = parent.loadIcon(iconFile, 20, 20);
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
                int removeIndex = -1;
                List<Item> items = inventory.getItems();
                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).getName().equals(key)) { removeIndex = i; break; }
                }
                if (removeIndex == -1) throw new IndexOutOfBoundsException();
                inventory.useItem(removeIndex, target);
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

        JPanel bottom = new JPanel(new GridLayout(1,2,8,8));
        bottom.add(useBtn);
        bottom.add(close);

        add(new JLabel("Items (stacked):"), BorderLayout.NORTH);
        add(sp, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private void updateList() {
        model.clear();
        keys = new ArrayList<>();
        Map<String, Integer> counts = new LinkedHashMap<>();
        Map<String, Item> example = new LinkedHashMap<>();
        for (Item it : inventory.getItems()) {
            counts.put(it.getName(), counts.getOrDefault(it.getName(), 0) + 1);
            example.putIfAbsent(it.getName(), it);
        }
        for (Map.Entry<String,Integer> e : counts.entrySet()) {
            String name = e.getKey();
            Item sample = example.get(name);
            model.addElement(name + " x" + e.getValue() + " - " + sample.getDescription());
            keys.add(name);
        }
    }
}

