package main;

import javax.swing.SwingUtilities;
import ui.RpgGameUI;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RpgGameUI gui = new RpgGameUI();
            gui.setVisible(true);
        });
    }
}

