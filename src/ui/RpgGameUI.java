package ui;

import battle.BattleController;
import characters.Character;
import constants.GameConstants;
import enemies.Enemy;
import game.GameController;
import game.GameState;
import inventory.HealthPotion;
import inventory.InventoryService;
import inventory.ManaPotion;
import inventory.MegaPotion;
import inventory.RevivePotion;
import inventory.StackedItem;
import shop.ShopService;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * Main game window — handles only Swing layout, rendering, and user input.
 * All game logic is delegated to GameController, BattleController, and services.
 *
 * Refactored from 1,242 lines to ~400 lines following SOLID principles.
 */
public class RpgGameUI extends JFrame implements
        GameController.GameEventListener,
        BattleController.BattleEventListener {

    private static final long serialVersionUID = 1L;

    // --- Services & Controllers ---
    private final AssetManager assetManager;
    private final CardRenderer cardRenderer;
    private GameState state;
    private GameController gameController;
    private BattleController battleController;
    private InventoryService inventoryService;

    // --- Swing Components ---
    private JLabel waveLabel, goldLabel, turnLabel, scoreLabel, statusLabel;
    private JPanel partyPanel, enemyPanel, inventoryPanel, scenePanel;
    private JTextArea logArea, artArea;
    private JLabel imageLabel;
    private JButton attackButton, defendButton, skillButton, itemButton, fleeButton;
    private JButton shopButton, saveButton;
    private Timer animationTimer;
    private String[] battleFrames;
    private int animationIndex;

    public RpgGameUI() {
        setTitle("RPG Battle GUI");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 760);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(230, 230, 230));
        setLayout(new BorderLayout(8, 8));

        assetManager = new AssetManager();
        cardRenderer = new CardRenderer(assetManager);
        state = new GameState();

        assetManager.ensureAssetsExist();
        initializeComponents();
        showIntroMenu();
    }

    // ========================= LAYOUT SETUP =========================

    private void initializeComponents() {
        waveLabel = createHeaderLabel("Wave: 1");
        goldLabel = createHeaderLabel("Gold: 0");
        turnLabel = createHeaderLabel("Turn: 1");
        scoreLabel = createHeaderLabel("Enemies Defeated: 0");
        statusLabel = createHeaderLabel("Choose a character to begin.");

        JPanel topBar = new JPanel(new GridLayout(1, 5, 10, 10));
        topBar.setBackground(Color.WHITE);
        topBar.add(waveLabel);
        topBar.add(goldLabel);
        topBar.add(turnLabel);
        topBar.add(scoreLabel);
        topBar.add(statusLabel);
        add(topBar, BorderLayout.NORTH);

        partyPanel = new JPanel();
        partyPanel.setBorder(BorderFactory.createTitledBorder("PARTY"));
        partyPanel.setLayout(new BoxLayout(partyPanel, BoxLayout.Y_AXIS));
        partyPanel.setBackground(new Color(245, 245, 245));
        partyPanel.setOpaque(true);
        partyPanel.setPreferredSize(new Dimension(260, 260));

        inventoryPanel = new JPanel();
        inventoryPanel.setBorder(BorderFactory.createTitledBorder("INVENTORY"));
        inventoryPanel.setLayout(new BoxLayout(inventoryPanel, BoxLayout.Y_AXIS));
        inventoryPanel.setBackground(new Color(245, 245, 245));
        inventoryPanel.setOpaque(true);
        inventoryPanel.setPreferredSize(new Dimension(260, 240));

        JPanel leftSide = new JPanel();
        leftSide.setLayout(new BoxLayout(leftSide, BoxLayout.Y_AXIS));
        leftSide.setBackground(new Color(232, 232, 232));
        leftSide.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        leftSide.add(partyPanel);
        leftSide.add(Box.createVerticalStrut(12));
        leftSide.add(inventoryPanel);
        add(leftSide, BorderLayout.WEST);

        enemyPanel = new JPanel();
        enemyPanel.setBorder(BorderFactory.createTitledBorder("ENEMY WAVE 1"));
        enemyPanel.setLayout(new BoxLayout(enemyPanel, BoxLayout.Y_AXIS));
        enemyPanel.setBackground(new Color(245, 245, 245));
        enemyPanel.setOpaque(true);
        enemyPanel.setPreferredSize(new Dimension(320, 260));

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        logArea.setBackground(new Color(250, 250, 250));
        logArea.setForeground(Color.BLACK);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        JScrollPane logPane = new JScrollPane(logArea);
        logPane.setBorder(BorderFactory.createTitledBorder("BATTLE LOG"));
        logPane.setPreferredSize(new Dimension(320, 340));

        JPanel rightSide = new JPanel(new BorderLayout(10, 10));
        rightSide.setBackground(new Color(232, 232, 232));
        rightSide.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        rightSide.add(enemyPanel, BorderLayout.NORTH);
        rightSide.add(logPane, BorderLayout.CENTER);
        add(rightSide, BorderLayout.EAST);

        artArea = new JTextArea();
        artArea.setEditable(false);
        artArea.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
        artArea.setBackground(new Color(255, 255, 240));
        artArea.setForeground(Color.DARK_GRAY);
        artArea.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 2));
        artArea.setPreferredSize(new Dimension(560, 320));

        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setOpaque(true);
        imageLabel.setBackground(new Color(255, 255, 240));
        scenePanel = new JPanel(new CardLayout());
        scenePanel.add(artArea, "ASCII");
        scenePanel.add(imageLabel, "IMG");
        scenePanel.setBackground(new Color(250, 250, 240));
        scenePanel.setPreferredSize(new Dimension(560, 320));

        attackButton = createActionButton("Basic Attack", e -> battleController.handleAction(1));
        defendButton = createActionButton("Defend", e -> battleController.handleAction(2));
        skillButton = createActionButton("Use Skill", e -> battleController.handleAction(3));
        itemButton = createActionButton("Use Item", e -> openInventoryFrame());
        fleeButton = createActionButton("Flee", e -> battleController.handleAction(5));
        shopButton = createActionButton("Open Shop", e -> openShopFrame());
        saveButton = createActionButton("Save", e -> { gameController.saveGame(); JOptionPane.showMessageDialog(this, "Game saved.", "Save", JOptionPane.INFORMATION_MESSAGE); });

        JPanel actionPanel = new JPanel(new GridLayout(2, 2, 12, 12));
        actionPanel.setBackground(Color.WHITE);
        actionPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        actionPanel.add(attackButton);
        actionPanel.add(skillButton);
        actionPanel.add(itemButton);
        actionPanel.add(fleeButton);

        JPanel centerPanel = new JPanel(new BorderLayout(12, 12));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        centerPanel.add(scenePanel, BorderLayout.CENTER);
        centerPanel.add(actionPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));
        footerPanel.add(createSmallUtilityButton("Inventory", e -> openInventoryFrame()));
        footerPanel.add(createSmallUtilityButton("Save", e -> { gameController.saveGame(); JOptionPane.showMessageDialog(this, "Game saved.", "Save", JOptionPane.INFORMATION_MESSAGE); }));
        add(footerPanel, BorderLayout.SOUTH);

        battleFrames = new String[] {
            "  /\\        /\\  \n ( o >  < o ) \n  \\\\  ^  // \n   \\\\___// \n    /   ",
            "   /\\      /\\   \n  ( ^ >  < ^ )  \n   \\\\  o  //  \n    \\\\___//   \n     /   ",
            "    /\\    /\\    \n   ( * >  < * )   \n    \\\\  x  //    \n     \\\\___//     \n      /   ",
            "     /\\  /\\     \n    ( ! >  < ! )    \n     \\\\  *  //     \n      \\\\___//      \n       /   "
        };

        loadButtonIcons();
    }

    private void loadButtonIcons() {
        ImageIcon ic = assetManager.loadIcon("icon.png", 64, 64);
        if (ic != null) setIconImage(ic.getImage());

        setButtonIcon(attackButton, "attack.png", 24);
        setButtonIcon(defendButton, "defend.png", 24);
        setButtonIcon(skillButton, "skill.png", 24);
        setButtonIcon(itemButton, "item.png", 24);
        setButtonIcon(fleeButton, "flee.png", 24);
        setButtonIcon(shopButton, "shop.png", 24);
        setButtonIcon(saveButton, "save.png", 20);
    }

    private void setButtonIcon(JButton button, String filename, int size) {
        ImageIcon icon = assetManager.loadIcon(filename, size, size);
        if (icon != null) button.setIcon(icon);
    }

    // ========================= WIDGET FACTORIES =========================

    private JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        label.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        label.setOpaque(true);
        label.setBackground(Color.WHITE);
        label.setForeground(Color.DARK_GRAY);
        return label;
    }

    private JButton createActionButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        button.addActionListener(listener);
        button.setEnabled(false);
        return button;
    }

    private JButton createSmallUtilityButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        button.setBackground(new Color(245, 245, 245));
        button.setForeground(Color.DARK_GRAY);
        button.setFocusPainted(false);
        button.addActionListener(listener);
        return button;
    }

    // ========================= GAME FLOW =========================

    private void showIntroMenu() {
        String[] options = {"New Game", "Load Game", "Exit"};
        int choice = JOptionPane.showOptionDialog(this,
            "Welcome to the RPG Battle GUI!",
            "RPG Game",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            null, options, options[0]);

        gameController = new GameController(state, this);
        inventoryService = new InventoryService(state.getInventory());

        switch (choice) {
            case 0 -> {
                gameController.startNewGame();
                inventoryService = new InventoryService(state.getInventory());
                startWave();
            }
            case 1 -> {
                if (!gameController.loadSavedGame()) {
                    JOptionPane.showMessageDialog(this, "No saved game found or file is invalid.", "Load Error", JOptionPane.ERROR_MESSAGE);
                    gameController.startNewGame();
                }
                inventoryService = new InventoryService(state.getInventory());
                startWave();
            }
            default -> dispose();
        }
    }

    private void startWave() {
        gameController.startWave();
        animateBattleScene();
        shopButton.setEnabled(false);

        battleController = new BattleController(state, inventoryService, this);
        enableActionButtons(true);
        battleController.nextPlayerTurn();
    }

    // ========================= SCENE ANIMATION =========================

    private void animateBattleScene() {
        CardLayout cl = (CardLayout) scenePanel.getLayout();
        ImageIcon battleIcon = assetManager.loadIcon("battle.png", 560, 320);
        if (battleIcon != null) {
            imageLabel.setIcon(battleIcon);
            imageLabel.setText("");
            cl.show(scenePanel, "IMG");
        } else {
            cl.show(scenePanel, "ASCII");
            if (animationTimer != null && animationTimer.isRunning()) {
                animationTimer.stop();
            }
            animationIndex = 0;
            animationTimer = new Timer(400, (ActionEvent e) -> {
                artArea.setText(battleFrames[animationIndex]);
                animationIndex = (animationIndex + 1) % battleFrames.length;
            });
            animationTimer.start();
        }
    }

    // ========================= PANEL UPDATES =========================

    private void updatePanels() {
        SwingUtilities.invokeLater(() -> {
            waveLabel.setText("Wave: " + state.getCurrentWave());
            goldLabel.setText("Gold: " + state.getGold());
            turnLabel.setText("Turn: " + state.getTurnCount());
            scoreLabel.setText("Enemies Defeated: " + state.getEnemiesDefeated());

            partyPanel.removeAll();
            partyPanel.setLayout(new GridLayout(1, Math.max(1, state.getPlayers().size()), 12, 12));
            partyPanel.setBackground(new Color(18, 18, 28));
            for (Character player : state.getPlayers()) {
                partyPanel.add(cardRenderer.createPlayerCard(player));
            }

            enemyPanel.removeAll();
            ArrayList<Enemy> enemies = state.getEnemies();
            enemyPanel.setLayout(new GridLayout(Math.max(1, enemies.size()), 1, 12, 12));
            enemyPanel.setBackground(new Color(18, 18, 28));
            if (!enemies.isEmpty()) {
                for (Enemy enemy : enemies) {
                    enemyPanel.add(cardRenderer.createEnemyCard(enemy));
                }
            } else {
                JLabel label = new JLabel("No enemies yet.", SwingConstants.CENTER);
                label.setForeground(Color.WHITE);
                enemyPanel.add(label);
            }

            inventoryPanel.removeAll();
            inventoryPanel.setLayout(new GridLayout(0, 1, 8, 8));
            inventoryPanel.setBackground(new Color(18, 18, 28));
            List<StackedItem> stacked = inventoryService.getStackedItems();
            if (stacked.isEmpty()) {
                JLabel label = new JLabel("No items in inventory.", SwingConstants.CENTER);
                label.setForeground(Color.WHITE);
                inventoryPanel.add(label);
            } else {
                for (StackedItem si : stacked) {
                    inventoryPanel.add(cardRenderer.createInventoryItemCard(si));
                }
            }

            partyPanel.revalidate(); partyPanel.repaint();
            enemyPanel.revalidate(); enemyPanel.repaint();
            inventoryPanel.revalidate(); inventoryPanel.repaint();
        });
    }

    private void enableActionButtons(boolean enabled) {
        attackButton.setEnabled(enabled);
        defendButton.setEnabled(enabled);
        skillButton.setEnabled(enabled);
        itemButton.setEnabled(enabled);
        fleeButton.setEnabled(enabled);
    }

    // ========================= SUB-FRAMES =========================

    private void openShopFrame() {
        ShopFrame sf = new ShopFrame(state, assetManager, this);
        sf.setVisible(true);
    }

    private void openInventoryFrame() {
        InventoryFrame inf = new InventoryFrame(inventoryService, assetManager, this);
        inf.setVisible(true);
    }

    // ========================= GameEventListener =========================

    @Override
    public void onLog(String message) {
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    @Override
    public void onStatusUpdate(String message) {
        statusLabel.setText(message);
    }

    @Override
    public void onPanelsRefresh() {
        updatePanels();
    }

    @Override
    public int onCharacterSelection(int slotNumber) {
        return showCharacterSelectionDialog(slotNumber);
    }

    @Override
    public void onActionButtonsEnabled(boolean enabled) {
        enableActionButtons(enabled);
    }

    @Override
    public void onBattleEnd(boolean victory) {
        enableActionButtons(false);
        if (victory) {
            gameController.handleVictory();
            onLog("\nVictory! Wave " + state.getCurrentWave() + " cleared.");
            onStatusUpdate("Wave " + state.getCurrentWave() + " cleared. Visit the shop before the next wave.");
            JOptionPane.showMessageDialog(this,
                "VICTORY!\nEnemies defeated: " + state.getEnemiesDefeated()
                    + "\nTurns taken: " + state.getTurnCount(),
                "Victory", JOptionPane.INFORMATION_MESSAGE);
            updatePanels();
            shopButton.setEnabled(true);
            saveButton.setEnabled(true);

            boolean proceedToNext = false;
            while (!proceedToNext) {
                String[] postOptions = {"Open Shop", "Start Next Wave", "Exit Game"};
                int choice = JOptionPane.showOptionDialog(this,
                    "What would you like to do next?\nGold: " + state.getGold(),
                    "Post-Wave Options",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                    null, postOptions, postOptions[0]);

                switch (choice) {
                    case 0 -> { openShopInline(); updatePanels(); }
                    case 1 -> proceedToNext = true;
                    default -> { dispose(); return; }
                }
            }

            gameController.advanceWave();
            startWave();
        } else {
            onLog("\nDefeat... The party has fallen.");
            onStatusUpdate("Game Over. Refresh to play again.");
            JOptionPane.showMessageDialog(this,
                "DEFEAT...\nEnemies defeated: " + state.getEnemiesDefeated()
                    + "\nTurns taken: " + state.getTurnCount(),
                "Defeat", JOptionPane.ERROR_MESSAGE);
            enableActionButtons(false);
            shopButton.setEnabled(false);
            saveButton.setEnabled(false);
        }
    }

    @Override
    public Enemy onChooseEnemyTarget(ArrayList<Enemy> aliveEnemies) {
        if (aliveEnemies.isEmpty()) return null;
        String[] options = new String[aliveEnemies.size()];
        for (int i = 0; i < aliveEnemies.size(); i++) {
            Enemy e = aliveEnemies.get(i);
            options[i] = e.getName() + " (HP: " + (double) e.getHp() + ")";
        }
        int choice = JOptionPane.showOptionDialog(this, "Choose an enemy target:",
            "Target Selection", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
            null, options, options[0]);
        if (choice < 0 || choice >= aliveEnemies.size()) return null;
        return aliveEnemies.get(choice);
    }

    @Override
    public Character onChoosePlayerTarget(ArrayList<Character> players) {
        return choosePlayerTargetDialog(players);
    }

    @Override
    public String onChooseItem(List<StackedItem> stackedItems) {
        String[] choices = new String[stackedItems.size()];
        for (int i = 0; i < stackedItems.size(); i++) {
            choices[i] = stackedItems.get(i).getDisplayText();
        }
        int choice = JOptionPane.showOptionDialog(this, "Choose an item to use:",
            "Inventory", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
            null, choices, choices[0]);
        if (choice < 0 || choice >= stackedItems.size()) return null;
        return stackedItems.get(choice).getName();
    }

    @Override
    public Character onChooseItemTarget(ArrayList<Character> players) {
        return choosePlayerTargetDialog(players);
    }

    // ========================= SHARED DIALOGS =========================

    public Character choosePlayerTargetDialog(ArrayList<Character> players) {
        if (players == null || players.isEmpty()) return null;
        String[] options = new String[players.size()];
        for (int i = 0; i < players.size(); i++) {
            Character p = players.get(i);
            String status = p.isAlive() ? "HP: " + (double) p.getHp() : "DEAD";
            options[i] = p.getName() + " (" + status + ")";
        }
        int choice = JOptionPane.showOptionDialog(this, "Choose a party member:",
            "Target Selection", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
            null, options, options[0]);
        if (choice < 0 || choice >= players.size()) return null;
        return players.get(choice);
    }

    public Character choosePlayerTargetDialog() {
        return choosePlayerTargetDialog(state.getPlayers());
    }

    private int showCharacterSelectionDialog(int slotNumber) {
        String[] choices = {"Warrior", "Mage", "Archer"};
        String[] imgs = {"char_warrior.png", "char_mage.png", "char_archer.png"};
        String title = "Choose your character " + slotNumber;

        final int[] selectedIndex = {-1};

        JDialog dialog = new JDialog(this, title, true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLayout(new BorderLayout(12, 12));

        JLabel prompt = new JLabel("Select your hero by clicking the image", SwingConstants.CENTER);
        prompt.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        prompt.setForeground(Color.WHITE);
        prompt.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        dialog.add(prompt, BorderLayout.NORTH);

        JPanel cardPanel = new JPanel(new GridLayout(1, choices.length, 16, 16));
        cardPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        cardPanel.setBackground(new Color(20, 20, 28));

        for (int i = 0; i < choices.length; i++) {
            int index = i;
            ImageIcon icon = assetManager.loadIcon(imgs[i], 160, 160);
            JButton btn = new JButton(choices[i], icon);
            btn.setVerticalTextPosition(SwingConstants.BOTTOM);
            btn.setHorizontalTextPosition(SwingConstants.CENTER);
            btn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
            btn.setForeground(Color.WHITE);
            btn.setBackground(new Color(45, 45, 55));
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 255), 2),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
            btn.addActionListener(e -> {
                selectedIndex[0] = index;
                dialog.dispose();
            });
            cardPanel.add(btn);
        }

        dialog.add(cardPanel, BorderLayout.CENTER);
        dialog.setSize(620, 320);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        return selectedIndex[0];
    }

    private void openShopInline() {
        String[] options = {
            "Health Potion (20)", "Mega Potion (50)",
            "Mana Potion (30)", "Revive Potion (100)", "Exit Shop"
        };

        while (true) {
            int choice = JOptionPane.showOptionDialog(this,
                "Gold: " + state.getGold() + "\nChoose a purchase:",
                "Shop", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, options, options[0]);

            if (choice < 0 || choice == 4) break;

            inventory.Item item = switch (choice) {
                case 0 -> new HealthPotion();
                case 1 -> new MegaPotion();
                case 2 -> new ManaPotion();
                case 3 -> new RevivePotion();
                default -> null;
            };
            double price = switch (choice) {
                case 0 -> GameConstants.HEALTH_POTION_PRICE;
                case 1 -> GameConstants.MEGA_POTION_PRICE;
                case 2 -> GameConstants.MANA_POTION_PRICE;
                case 3 -> GameConstants.REVIVE_POTION_PRICE;
                default -> 0;
            };

            if (item != null) {
                if (ShopService.buyItem(item, price, state)) {
                    onLog("Bought " + item.getName() + " for " + price + " gold.");
                    onStatusUpdate(item.getName() + " added to inventory.");
                } else {
                    JOptionPane.showMessageDialog(this, "Not enough gold.", "Shop", JOptionPane.WARNING_MESSAGE);
                }
            }
        }
        updatePanels();
    }

    // ========================= PUBLIC ACCESSORS FOR SUB-FRAMES =========================

    public void refreshPanels() {
        updatePanels();
    }

    public void appendLogPublic(String text) {
        onLog(text);
    }

    public GameState getGameState() {
        return state;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }
}
