package ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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

/**
 * Main game window — handles only Swing layout, rendering, and user input.
 * All game logic is delegated to GameController, BattleController, and services.
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
    private JPanel toastPanel;
    private JLabel toastLabel;
    private Timer toastTimer;
    private Queue<String> toastQueue;
    private boolean toastShowing;
    private JPanel partyPanel, enemyPanel, inventoryPanel, scenePanel;
    private JTextArea logArea, artArea;
    private JLabel imageLabel;
    private JButton attackButton, defendButton, skillButton, itemButton, fleeButton;
    private JButton shopButton, saveButton;
    private Timer animationTimer;
    private String[] battleFrames;
    private int animationIndex;
    private boolean hasUnsavedChanges = false;

    public RpgGameUI() {
        setTitle("RPG Battle GUI");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 760);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(18, 18, 28));
        setLayout(new BorderLayout(8, 8));

        assetManager = new AssetManager();
        cardRenderer = new CardRenderer(assetManager);
        state = new GameState();
        toastQueue = new LinkedList<>();
        toastShowing = false;

        assetManager.ensureAssetsExist();
        initializeComponents();
        showIntroMenu();
    }

    // ========================= LAYOUT SETUP =========================

    private void initializeComponents() {
        Color darkBg = new Color(18, 18, 28);
        Color panelBg = new Color(24, 26, 36);
        Color headerBg = new Color(30, 32, 45);
        Color headerBorder = new Color(60, 65, 90);
        Color goldText = new Color(255, 215, 80);
        Color textColor = new Color(220, 225, 240);

        waveLabel = createHeaderLabel("Wave: 1");
        goldLabel = createHeaderLabel("Gold: 0");
        turnLabel = createHeaderLabel("Turn: 1");
        scoreLabel = createHeaderLabel("Enemies Defeated: 0");
        statusLabel = createHeaderLabel("Choose a character to begin.");

        toastPanel = new JPanel(new BorderLayout());
        toastPanel.setBackground(new Color(24, 28, 42));
        toastPanel.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        toastLabel = new JLabel(" ", SwingConstants.CENTER);
        toastLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        toastLabel.setForeground(new Color(245, 245, 245));
        toastLabel.setOpaque(true);
        toastLabel.setBackground(new Color(40, 55, 90));
        toastLabel.setBorder(BorderFactory.createLineBorder(new Color(80, 130, 220), 1));
        toastPanel.add(toastLabel, BorderLayout.CENTER);
        toastPanel.setVisible(false);

        JPanel topBar = new JPanel(new GridLayout(1, 5, 6, 6));
        topBar.setBackground(darkBg);
        topBar.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        topBar.add(waveLabel);
        topBar.add(goldLabel);
        topBar.add(turnLabel);
        topBar.add(scoreLabel);
        topBar.add(statusLabel);

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setBackground(darkBg);
        topContainer.add(toastPanel, BorderLayout.NORTH);
        topContainer.add(topBar, BorderLayout.CENTER);
        add(topContainer, BorderLayout.NORTH);

        partyPanel = new JPanel();
        partyPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(80, 130, 220, 100), 2),
            "\u2694 PARTY", javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font(Font.SANS_SERIF, Font.BOLD, 13), goldText));
        partyPanel.setLayout(new BoxLayout(partyPanel, BoxLayout.Y_AXIS));
        partyPanel.setBackground(panelBg);
        partyPanel.setOpaque(true);
        partyPanel.setPreferredSize(new Dimension(280, 280));

        inventoryPanel = new JPanel();
        inventoryPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(180, 140, 60, 100), 2),
            "\uD83C\uDF92 INVENTORY", javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font(Font.SANS_SERIF, Font.BOLD, 13), goldText));
        inventoryPanel.setLayout(new BoxLayout(inventoryPanel, BoxLayout.Y_AXIS));
        inventoryPanel.setBackground(panelBg);
        inventoryPanel.setOpaque(true);
        inventoryPanel.setPreferredSize(new Dimension(280, 240));

        JPanel leftSide = new JPanel();
        leftSide.setLayout(new BoxLayout(leftSide, BoxLayout.Y_AXIS));
        leftSide.setBackground(darkBg);
        leftSide.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        leftSide.add(partyPanel);
        leftSide.add(Box.createVerticalStrut(10));
        leftSide.add(inventoryPanel);
        add(leftSide, BorderLayout.WEST);

        enemyPanel = new JPanel();
        enemyPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 60, 60, 100), 2),
            "\uD83D\uDC80 ENEMIES", javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font(Font.SANS_SERIF, Font.BOLD, 13), new Color(255, 100, 100)));
        enemyPanel.setLayout(new BoxLayout(enemyPanel, BoxLayout.X_AXIS));
        enemyPanel.setBackground(panelBg);
        enemyPanel.setOpaque(true);
        enemyPanel.setPreferredSize(new Dimension(340, 320));

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        logArea.setBackground(new Color(20, 22, 32));
        logArea.setForeground(new Color(200, 210, 230));
        logArea.setCaretColor(new Color(200, 210, 230));
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        JScrollPane logPane = new JScrollPane(logArea);
        logPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(60, 65, 90), 1),
            "\uD83D\uDCDC BATTLE LOG", javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font(Font.SANS_SERIF, Font.BOLD, 13), goldText));
        logPane.setPreferredSize(new Dimension(340, 340));
        logPane.getViewport().setBackground(new Color(20, 22, 32));

        JPanel rightSide = new JPanel(new BorderLayout(10, 10));
        rightSide.setBackground(darkBg);
        rightSide.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        rightSide.add(enemyPanel, BorderLayout.NORTH);
        rightSide.add(logPane, BorderLayout.CENTER);
        add(rightSide, BorderLayout.EAST);

        artArea = new JTextArea();
        artArea.setEditable(false);
        artArea.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
        artArea.setBackground(new Color(20, 22, 32));
        artArea.setForeground(new Color(180, 190, 210));
        artArea.setBorder(BorderFactory.createLineBorder(new Color(60, 65, 90), 2));
        artArea.setPreferredSize(new Dimension(560, 320));

        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setOpaque(true);
        imageLabel.setBackground(new Color(20, 22, 32));
        scenePanel = new JPanel(new CardLayout());
        scenePanel.add(artArea, "ASCII");
        scenePanel.add(imageLabel, "IMG");
        scenePanel.setBackground(new Color(20, 22, 32));
        scenePanel.setPreferredSize(new Dimension(560, 320));

        attackButton = createActionButton("\u2694 Attack", new Color(180, 50, 50), e -> battleController.handleAction(1));
        defendButton = createActionButton("\uD83D\uDEE1 Defend", new Color(50, 100, 180), e -> battleController.handleAction(2));
        skillButton = createActionButton("\u2728 Skill", new Color(140, 60, 180), e -> battleController.handleAction(3));
        itemButton = createActionButton("\uD83C\uDF7A Item", new Color(180, 120, 40), e -> openInventoryFrame());
        shopButton = createActionButton("\uD83D\uDCB0 Shop", new Color(50, 150, 80), e -> openShopFrame());
        fleeButton = createActionButton("\uD83C\uDFC3 Flee", new Color(100, 100, 110), e -> battleController.handleAction(5));
        saveButton = createActionButton("\uD83D\uDCBE Save", new Color(60, 130, 160), e -> {
            gameController.saveGame();
            hasUnsavedChanges = false;
            JOptionPane.showMessageDialog(this, "Game saved.", "Save", JOptionPane.INFORMATION_MESSAGE);
        });

        JPanel actionPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        actionPanel.setBackground(darkBg);
        actionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        actionPanel.add(attackButton);
        actionPanel.add(defendButton);
        actionPanel.add(skillButton);
        actionPanel.add(itemButton);
        actionPanel.add(shopButton);
        actionPanel.add(fleeButton);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(darkBg);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        centerPanel.add(scenePanel, BorderLayout.CENTER);
        centerPanel.add(actionPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        footerPanel.setBackground(darkBg);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));
        footerPanel.add(createSmallUtilityButton("Inventory", e -> openInventoryFrame()));
        footerPanel.add(createSmallUtilityButton("Save", e -> { gameController.saveGame(); JOptionPane.showMessageDialog(this, "Game saved.", "Save", JOptionPane.INFORMATION_MESSAGE); }));
        footerPanel.add(createSmallUtilityButton("Back to Main Menu", e -> handleBackToMainMenu()));
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
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        label.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 65, 90), 1),
            BorderFactory.createEmptyBorder(4, 6, 4, 6)));
        label.setOpaque(true);
        label.setBackground(new Color(30, 32, 45));
        label.setForeground(new Color(220, 225, 240));
        return label;
    }

    private JButton createActionButton(String text, ActionListener listener) {
        return createActionButton(text, new Color(60, 65, 80), listener);
    }

    private JButton createActionButton(String text, Color bgColor, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(new Color(240, 240, 250));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(bgColor.getRed() + 40 > 255 ? 255 : bgColor.getRed() + 40,
                bgColor.getGreen() + 40 > 255 ? 255 : bgColor.getGreen() + 40,
                bgColor.getBlue() + 40 > 255 ? 255 : bgColor.getBlue() + 40, 150), 2),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        button.addActionListener(listener);
        button.setEnabled(false);
        return button;
    }

    private JButton createSmallUtilityButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        button.setBackground(new Color(35, 38, 52));
        button.setForeground(new Color(200, 205, 220));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 65, 90), 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        button.addActionListener(listener);
        return button;
    }

    // ========================= GAME FLOW =========================

    private void showIntroMenu() {
        JDialog introDialog = new JDialog(this, "RPG by Klint, Kent & Rex", true);
        introDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        introDialog.setSize(700, 550);
        introDialog.setLocationRelativeTo(this);
        introDialog.setResizable(false);

        Color darkBg = new Color(18, 18, 28);
        Color accentColor = new Color(80, 130, 220);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(darkBg);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Title
        JLabel titleLabel = new JLabel("⚔️ EPIC RPG BATTLE ⚔️");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 32));
        titleLabel.setForeground(new Color(255, 215, 80));
        titleLabel.setAlignmentX(0.5f);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Turn-based Strategic Combat");
        subtitleLabel.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 14));
        subtitleLabel.setForeground(new Color(200, 200, 220));
        subtitleLabel.setAlignmentX(0.5f);

        // Description
        JLabel descLabel = new JLabel("<html><center>Lead your party to victory against fearsome enemies.<br>Manage resources, choose actions wisely.</center></html>");
        descLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        descLabel.setForeground(new Color(180, 180, 200));
        descLabel.setAlignmentX(1.0f);

        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(8));
        mainPanel.add(subtitleLabel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(descLabel);
        mainPanel.add(Box.createVerticalStrut(40));

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(darkBg);
        buttonPanel.setAlignmentX(0.5f);

        int[] result = {-1};

        JButton newGameBtn = new JButton("🎮 NEW GAME");
        newGameBtn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        newGameBtn.setBackground(new Color(50, 150, 80));
        newGameBtn.setForeground(Color.WHITE);
        newGameBtn.setFocusPainted(false);
        newGameBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 200, 130), 2),
            BorderFactory.createEmptyBorder(12, 40, 12, 40)));
        newGameBtn.setMaximumSize(new Dimension(300, 50));
        newGameBtn.setAlignmentX(0.5f);
        newGameBtn.addActionListener(e -> {
            result[0] = 0;
            introDialog.dispose();
        });

        JButton loadGameBtn = new JButton("📂 LOAD GAME");
        loadGameBtn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        loadGameBtn.setBackground(new Color(60, 100, 160));
        loadGameBtn.setForeground(Color.WHITE);
        loadGameBtn.setFocusPainted(false);
        loadGameBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 150, 220), 2),
            BorderFactory.createEmptyBorder(12, 40, 12, 40)));
        loadGameBtn.setMaximumSize(new Dimension(300, 50));
        loadGameBtn.setAlignmentX(0.5f);
        loadGameBtn.addActionListener(e -> {
            result[0] = 1;
            introDialog.dispose();
        });

        JButton exitBtn = new JButton("❌ EXIT GAME");
        exitBtn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        exitBtn.setBackground(new Color(150, 50, 50));
        exitBtn.setForeground(Color.WHITE);
        exitBtn.setFocusPainted(false);
        exitBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 100, 100), 2),
            BorderFactory.createEmptyBorder(12, 40, 12, 40)));
        exitBtn.setMaximumSize(new Dimension(300, 50));
        exitBtn.setAlignmentX(0.5f);
        exitBtn.addActionListener(e -> {
            result[0] = 2;
            introDialog.dispose();
        });

        buttonPanel.add(newGameBtn);
        buttonPanel.add(Box.createVerticalStrut(12));
        buttonPanel.add(loadGameBtn);
        buttonPanel.add(Box.createVerticalStrut(12));
        buttonPanel.add(exitBtn);

        mainPanel.add(buttonPanel);
        introDialog.add(mainPanel);
        introDialog.setVisible(true);

        int choice = result[0];

        gameController = new GameController(state, this);
        inventoryService = new InventoryService(state.getInventory());

        switch (choice) {
            case 0 -> {
                java.util.List<Integer> team = showTeamBuilderDialog();
                if (team != null && !team.isEmpty()) {
                    gameController.startNewGame(team);
                    inventoryService = new InventoryService(state.getInventory());
                    startWave();
                } else {
                    dispose();
                }
            }
            case 1 -> {
                if (!gameController.loadSavedGame()) {
                    JOptionPane.showMessageDialog(this, "No saved game found or file is invalid.", "Load Error", JOptionPane.ERROR_MESSAGE);
                    java.util.List<Integer> team = showTeamBuilderDialog();
                    if (team != null && !team.isEmpty()) {
                        gameController.startNewGame(team);
                        inventoryService = new InventoryService(state.getInventory());
                        startWave();
                    } else {
                        dispose();
                    }
                } else {
                    // Loaded a saved game: restore UI without regenerating enemies
                    inventoryService = new InventoryService(state.getInventory());
                    animateBattleScene();
                    battleController = new BattleController(state, inventoryService, this);
                    enableActionButtons(true);
                    updatePanels();
                    onLog("Game loaded successfully.");
                    battleController.nextPlayerTurn();
                }
            }
            default -> dispose();
        }
    }

    private void startWave() {
        gameController.startWave();
        animateBattleScene();

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

            // Build party UI cards from the game state. This is dynamic:
            // every player in the backend list becomes one card in the panel.
            partyPanel.removeAll();
            partyPanel.setLayout(new GridLayout(1, Math.max(1, state.getPlayers().size()), 12, 12));
            partyPanel.setBackground(new Color(18, 18, 28));
            for (Character player : state.getPlayers()) {
                partyPanel.add(cardRenderer.createPlayerCard(player));
            }

            // Build enemy UI cards from the backend enemy list on every refresh.
            enemyPanel.removeAll();
            ArrayList<Enemy> enemies = state.getEnemies();
            enemyPanel.setLayout(new GridLayout(1, Math.max(1, enemies.size()), 12, 12));
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
        shopButton.setEnabled(enabled);
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
        // Show toast notification for every action
        if (!message.trim().isEmpty() && !message.startsWith("---")) {
            onToast(message.trim());
        }
    }

    @Override
    public void onStatusUpdate(String message) {
        statusLabel.setText(message);
    }

    @Override
    public void onToast(String message) {
        toastQueue.add(message);
        if (!toastShowing) {
            showNextToast();
        }
    }

    private void showNextToast() {
        if (toastQueue.isEmpty()) {
            toastShowing = false;
            return;
        }

        toastShowing = true;
        String message = toastQueue.poll();
        toastLabel.setText(message);
        toastPanel.setVisible(true);

        if (toastTimer != null && toastTimer.isRunning()) {
            toastTimer.stop();
        }
        toastTimer = new Timer(1800, e -> {
            toastPanel.setVisible(false);
            showNextToast();
        });
        toastTimer.setRepeats(false);
        toastTimer.start();
    }

    @Override
    public void onPanelsRefresh() {
        updatePanels();
    }

    @Override
    public void onActionButtonsEnabled(boolean enabled) {
        enableActionButtons(enabled);
    }

    @Override
    public void onBattleEnd(boolean victory) {
        enableActionButtons(false);
        if (victory) {
            double prevGold = state.getGold();
            gameController.handleVictory();
            double reward = state.getGold() - prevGold;
            onLog("\nVictory! Wave " + state.getCurrentWave() + " cleared.");
            onStatusUpdate("Wave " + state.getCurrentWave() + " cleared. Visit the shop before the next wave.");

            if (state.getCurrentWave() >= 4) {
                JOptionPane.showMessageDialog(this,
                    "CONGRATULATIONS!\nYou have completed all 4 waves and won the game!\n" +
                    "Enemies defeated: " + state.getEnemiesDefeated() + "\n" +
                    "Turns taken: " + state.getTurnCount() + "\n" +
                    "Final Gold: " + (int)state.getGold(),
                    "Final Victory!", JOptionPane.INFORMATION_MESSAGE);
                hasUnsavedChanges = false;
                showIntroMenu();
                return;
            }

            JOptionPane.showMessageDialog(this,
                "VICTORY!\nEnemies defeated: " + state.getEnemiesDefeated()
                    + "\nTurns taken: " + state.getTurnCount()
                    + "\nGold: " + (int)state.getGold() + " (+" + (int)reward + " this wave)",
                "Victory", JOptionPane.INFORMATION_MESSAGE);
            updatePanels();
            shopButton.setEnabled(true);
            saveButton.setEnabled(true);

            boolean proceedToNext = false;
            while (!proceedToNext) {
                String[] postOptions = {"Open Shop", "Start Next Wave", "Exit Game"};
                int choice = JOptionPane.showOptionDialog(this,
                    "What would you like to do next?\nGold: " + (int)state.getGold(),
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

    @Override
    public boolean onConfirmAction(String message) {
        int result = JOptionPane.showConfirmDialog(this, message, "Confirm Action", JOptionPane.YES_NO_OPTION);
        return result == JOptionPane.YES_OPTION;
    }

    @Override
    public void onShowMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Battle Details", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void onDelay(int milliseconds, Runnable afterDelay) {
        Timer timer = new Timer(milliseconds, e -> afterDelay.run());
        timer.setRepeats(false);
        timer.start();
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

    private java.util.List<Integer> showTeamBuilderDialog() {
        String[] choices = {"Warrior", "Mage", "Archer"};
        String[] imgs = {"players/warrior.png", "players/mage.png", "players/archer.png"};
        
        java.util.List<Integer> selectedClasses = new ArrayList<>();
        
        JDialog dialog = new JDialog(this, "Team Builder", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLayout(new BorderLayout(12, 12));
        dialog.getContentPane().setBackground(new Color(18, 18, 28));

        JLabel prompt = new JLabel("Click to add/remove members (Max 2)", SwingConstants.CENTER);
        prompt.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        prompt.setForeground(Color.WHITE);
        prompt.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        dialog.add(prompt, BorderLayout.NORTH);

        JPanel classPanel = new JPanel(new GridLayout(1, choices.length, 16, 16));
        classPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(80, 130, 220, 100), 2),
            "AVAILABLE CLASSES", javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font(Font.SANS_SERIF, Font.BOLD, 13), new Color(255, 215, 80)));
        classPanel.setBackground(new Color(24, 26, 36));

        JPanel partyPanel = new JPanel(new GridLayout(1, 2, 16, 16));
        partyPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(80, 220, 130, 100), 2),
            "CURRENT PARTY", javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font(Font.SANS_SERIF, Font.BOLD, 13), new Color(255, 215, 80)));
        partyPanel.setBackground(new Color(24, 26, 36));
        partyPanel.setPreferredSize(new Dimension(620, 160));

        JButton startBtn = new JButton("START ADVENTURE");
        startBtn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        startBtn.setBackground(new Color(50, 150, 80));
        startBtn.setForeground(Color.WHITE);
        startBtn.setFocusPainted(false);
        startBtn.setEnabled(false);
        startBtn.addActionListener(e -> dialog.dispose());
        
        final Runnable[] updateUIRef = new Runnable[1];
        updateUIRef[0] = () -> {
            partyPanel.removeAll();
            for (int i = 0; i < selectedClasses.size(); i++) {
                int indexInList = i;
                int classChoice = selectedClasses.get(i);
                ImageIcon icon = assetManager.loadIcon(imgs[classChoice], 100, 100);
                JButton pBtn = new JButton(choices[classChoice], icon);
                pBtn.setVerticalTextPosition(SwingConstants.BOTTOM);
                pBtn.setHorizontalTextPosition(SwingConstants.CENTER);
                pBtn.setBackground(new Color(80, 50, 60));
                pBtn.setForeground(Color.WHITE);
                pBtn.setToolTipText("Click to remove");
                pBtn.addActionListener(e -> {
                    selectedClasses.remove(indexInList);
                    updateUIRef[0].run();
                });
                partyPanel.add(pBtn);
            }
            for (int i = selectedClasses.size(); i < 2; i++) {
                JLabel emptySlot = new JLabel("Empty Slot", SwingConstants.CENTER);
                emptySlot.setForeground(Color.GRAY);
                emptySlot.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2, true));
                partyPanel.add(emptySlot);
            }
            startBtn.setEnabled(selectedClasses.size() > 0);
            partyPanel.revalidate();
            partyPanel.repaint();
        };

        for (int i = 0; i < choices.length; i++) {
            int classChoice = i;
            ImageIcon icon = assetManager.loadIcon(imgs[i], 120, 120);
            JButton btn = new JButton(choices[i], icon);
            btn.setVerticalTextPosition(SwingConstants.BOTTOM);
            btn.setHorizontalTextPosition(SwingConstants.CENTER);
            btn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
            btn.setForeground(Color.WHITE);
            btn.setBackground(new Color(45, 45, 55));
            btn.setToolTipText("Click to add to party");
            btn.addActionListener(e -> {
                if (selectedClasses.size() < 2) {
                    selectedClasses.add(classChoice);
                    updateUIRef[0].run();
                }
            });
            classPanel.add(btn);
        }

        updateUIRef[0].run();

        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        centerPanel.setBackground(new Color(18, 18, 28));
        centerPanel.add(classPanel);
        centerPanel.add(partyPanel);

        dialog.add(centerPanel, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(18, 18, 28));
        bottomPanel.add(startBtn);
        dialog.add(bottomPanel, BorderLayout.SOUTH);

        dialog.setSize(620, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        return selectedClasses;
    }

    private void handleBackToMainMenu() {
        if (hasUnsavedChanges) {
            String[] options = {"Resume", "Save Game", "Quit to Main Menu"};
            int choice = JOptionPane.showOptionDialog(this,
                "You have unsaved changes.\nWhat would you like to do?",
                "Unsaved Changes",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                options,
                options[0]);

            switch (choice) {
                case 0 -> {
                    // Resume - do nothing
                }
                case 1 -> {
                    gameController.saveGame();
                    hasUnsavedChanges = false;
                    JOptionPane.showMessageDialog(this, "Game saved.", "Save", JOptionPane.INFORMATION_MESSAGE);
                    // After saving, return to main menu
                    showIntroMenu();
                }
                case 2 -> {
                    // Return to main menu without saving
                    showIntroMenu();
                }
                default -> {
                    // if dialog closed, resume
                }
            }
        } else {
            showIntroMenu();
        }
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
