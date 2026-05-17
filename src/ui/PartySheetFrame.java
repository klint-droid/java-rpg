package ui;

import characters.Character;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PartySheetFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private final List<Character> party;

    public PartySheetFrame(List<Character> party) {
        super("Party Status");
        this.party = party;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(380, 260);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        JPanel container = new JPanel(new BorderLayout(8, 8));
        JPanel statusGrid = new JPanel(new GridLayout(party.size(), 1, 4, 4));

        for (Character c : party) {
            String status = c.isAlive() ? "HP: " + (double) c.getHp() + "/" + (double) c.getMaxHp()
                : "DEAD";
            String mana = "MP: " + (double) c.getMana() + "/" + (double) c.getMaxMana();
            String atk = "ATK: " + (double) c.getAtkPower();
            String def = "DEF: " + (double) c.getDefPower();
            JLabel characterLabel = new JLabel(
                c.getName() + " (" + c.getClass().getSimpleName() + ") - " + status + " | " + mana + " | " + atk + " | " + def);
            statusGrid.add(characterLabel);
        }

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());

        container.add(statusGrid, BorderLayout.CENTER);
        container.add(closeButton, BorderLayout.SOUTH);
        add(container);
    }
}

