package ui;

import characters.Character;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class CharacterSheetFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private final Character character;

    public CharacterSheetFrame(Character character) {
        super(character.getName() + " - Details");
        this.character = character;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(320, 240);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        JPanel p = new JPanel(new GridLayout(5,1));
        p.add(new JLabel("Name: " + character.getName()));
        p.add(new JLabel("Class: " + character.getClass().getSimpleName()));
        p.add(new JLabel("HP: " + (int)character.getHp() + "/" + (int)character.getMaxHp()));
        p.add(new JLabel("MP: " + (int)character.getMana() + "/" + (int)character.getMaxMana()));
        p.add(new JLabel("ATK: " + (int)character.getAtkPower() + "  DEF: " + (int)character.getDefPower()));

        add(p, BorderLayout.CENTER);
    }
}
