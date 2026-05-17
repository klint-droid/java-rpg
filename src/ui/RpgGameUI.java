package ui;

import actions.AttackAction;
import actions.BattleAction;
import actions.DefendAction;
import actions.SkillAction;
import characters.Archer;
import characters.Character;
import characters.Mage;
import characters.Warrior;
import constants.GameConstants;
import enemies.Enemy;
import game.SaveData;
import game.SaveManager;
import inventory.EmptyInventoryException;
import inventory.HealthPotion;
import inventory.Inventory;
import inventory.Item;
import inventory.ManaPotion;
import inventory.MegaPotion;
import inventory.RevivePotion;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import results.BattleResult;

public class RpgGameUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private ArrayList<Character> players;
    private ArrayList<Enemy> enemies;
    private Inventory inventory;
    private double currentWave;
    private double enemiesDefeated;
    private double gold;
    private double turnCount;
    private int currentPlayerIndex;

    private JLabel waveLabel;
    private JLabel goldLabel;
    private JLabel turnLabel;
    private JLabel statusLabel;
    private JLabel scoreLabel;
    private JPanel partyPanel;
    private JPanel enemyPanel;
    private JPanel inventoryPanel;
    private JButton viewInventoryButton;
    private JButton viewCharButton;
    private JTextArea logArea;
    private JTextArea artArea;
    private JLabel imageLabel;
    private JPanel scenePanel;
    private JButton attackButton;
    private JButton defendButton;
    private JButton skillButton;
    private JButton itemButton;
    private JButton fleeButton;
    private JButton shopButton;
    private JButton saveButton;

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

        initializeComponents();
        ensureAssetsExist();
        showIntroMenu();
    }

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

        attackButton = createActionButton("Basic Attack", e -> handleAction(1));
        defendButton = createActionButton("Defend", e -> handleAction(2));
        skillButton = createActionButton("Use Skill", e -> handleAction(3));
        itemButton = createActionButton("Use Item", e -> openInventoryFrame());
        fleeButton = createActionButton("Flee", e -> handleAction(5));
        shopButton = createActionButton("Open Shop", e -> openShopFrame());
        saveButton = createActionButton("Save", e -> saveGame());

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
        footerPanel.add(createSmallUtilityButton("Save", e -> saveGame()));
        add(footerPanel, BorderLayout.SOUTH);

        battleFrames = new String[] {
            "  /\\        /\\  \n ( o >  < o ) \n  \\\\  ^  // \n   \\\\___// \n    /   ",
            "   /\\      /\\   \n  ( ^ >  < ^ )  \n   \\\\  o  //  \n    \\\\___//   \n     /   ",
            "    /\\    /\\    \n   ( * >  < * )   \n    \\\\  x  //    \n     \\\\___//     \n      /   ",
            "     /\\  /\\     \n    ( ! >  < ! )    \n     \\\\  *  //     \n      \\\\___//      \n       /   "
        };

        // ensure placeholder assets exist, then try to load window icon and button icons from ./assets
        ensureAssetsExist();
        ImageIcon ic = loadIcon("icon.png", 64, 64);
        if (ic != null) {
            setIconImage(ic.getImage());
        }

        ImageIcon aic = loadIcon("attack.png", 24, 24);
        if (aic != null) attackButton.setIcon(aic);
        ImageIcon dic = loadIcon("defend.png", 24, 24);
        if (dic != null) defendButton.setIcon(dic);
        ImageIcon sik = loadIcon("skill.png", 24, 24);
        if (sik != null) skillButton.setIcon(sik);
        ImageIcon iic = loadIcon("item.png", 24, 24);
        if (iic != null) itemButton.setIcon(iic);
        ImageIcon fic = loadIcon("flee.png", 24, 24);
        if (fic != null) fleeButton.setIcon(fic);
        ImageIcon shc = loadIcon("shop.png", 24, 24);
        if (shc != null) shopButton.setIcon(shc);
        ImageIcon svc = loadIcon("save.png", 20, 20);
        if (svc != null) saveButton.setIcon(svc);
    }

    public ImageIcon loadIcon(String filename, double w, double h) {
        try {
            File f = new File("assets" + File.separator + filename);
            Image img;
            if (f.exists()) {
                img = ImageIO.read(f);
            } else {
                // create an in-memory placeholder icon when file is missing
                int ww = Math.max(1, w > 0 ? (int) w : 32);
                int hh = Math.max(1, h > 0 ? (int) h : 32);
                BufferedImage bi = new BufferedImage(ww, hh, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = bi.createGraphics();
                g.setColor(Color.DARK_GRAY);
                g.fillRect(0,0,ww,hh);
                g.setColor(Color.WHITE);
                g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, Math.max(10, ww/2)));
                String t = filename.replaceAll("\\..+$", "");
                if (t.length() > 2) t = t.substring(0,2).toUpperCase();
                g.drawString(t, Math.max(4, ww/6), Math.max(12, hh/2));
                g.dispose();
                img = bi;
            }
            if (w > 0 && h > 0) img = img.getScaledInstance((int) w, (int) h, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (IOException e) {
            return null;
        }
    }

    // Generate simple placeholder PNG assets if the assets folder is missing images.
    private void ensureAssetsExist() {
        try {
            File dir = new File("assets");
            if (!dir.exists()) dir.mkdirs();

            createPlaceholder("icon.png", 64, 64, Color.DARK_GRAY, "ICON");
            createPlaceholder("attack.png", 24, 24, Color.RED, "A");
            createPlaceholder("defend.png", 24, 24, Color.BLUE, "D");
            createPlaceholder("skill.png", 24, 24, Color.MAGENTA, "S");
            createPlaceholder("item.png", 24, 24, Color.ORANGE, "I");
            createPlaceholder("flee.png", 24, 24, Color.GRAY, "F");
            createPlaceholder("shop.png", 24, 24, Color.GREEN, "$" );
            createPlaceholder("save.png", 20, 20, Color.CYAN, "S");
            createPlaceholder("battle.gif", 440, 240, Color.LIGHT_GRAY, "BATTLE");
            createPlaceholder("battle.png", 560, 320, Color.LIGHT_GRAY, "BATTLE");
        } catch (Exception ex) {
            // ignore errors creating placeholders
        }
    }

    private void createPlaceholder(String filename, double w, double h, Color bg, String text) {
        File f = new File("assets" + File.separator + filename);
        if (f.exists()) return;
        try {
            int imgW = Math.max(1, (int) w);
            int imgH = Math.max(1, (int) h);
            BufferedImage img = new BufferedImage(imgW, imgH, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            g.setColor(bg);
            g.fillRect(0,0,imgW,imgH);
            g.setColor(Color.WHITE);
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, Math.max(12, (int) (w/4))));
            int tx = Math.max(4, (int) (w/6));
            int ty = Math.max(14, (int) (h/2 + 6));
            g.drawString(text, tx, ty);
            g.dispose();
            ImageIO.write(img, filename.endsWith(".png") ? "png" : "png", f);
        } catch (IOException ex) {
            // ignore
        }
    }

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

    private void showIntroMenu() {
        String[] options = {"New Game", "Load Game", "Exit"};
        int choice = JOptionPane.showOptionDialog(this,
            "Welcome to the RPG Battle GUI!",
            "RPG Game",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            null,
            options,
            options[0]);

        switch (choice) {
            case 0 -> startNewGame();
            case 1 -> loadSavedGame();
            default -> dispose();
        }
    }

    private void startNewGame() {
        players = new ArrayList<>();
        inventory = new Inventory();
        inventory.addStarterItems();
        currentWave = 1;
        enemiesDefeated = 0;
        gold = GameConstants.STARTING_GOLD;
        turnCount = 1;
        currentPlayerIndex = 0;
        createParty();
        startWave();
    }

    private void loadSavedGame() {
        SaveManager saveManager = new SaveManager();
        SaveData saveData = saveManager.loadGame();

        if (saveData == null) {
            JOptionPane.showMessageDialog(this, "No saved game found or file is invalid.", "Load Error", JOptionPane.ERROR_MESSAGE);
            startNewGame();
            return;
        }

        players = saveData.getPlayers();
        inventory = saveData.getInventory();
        currentWave = saveData.getCurrentWave();
        gold = saveData.getGold();
        enemiesDefeated = (currentWave - 1) * 1; // approximate
        turnCount = 1;
        currentPlayerIndex = 0;
        startWave();
    }

    private void createParty() {
        while (players.size() < 2) {
            String[] choices = {"Warrior", "Mage", "Archer"};
            int classChoice = JOptionPane.showOptionDialog(this,
                "Choose your character " + (players.size() + 1) + ":",
                "Character Selection",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                choices,
                choices[0]);

            if (classChoice < 0) {
                dispose();
                return;
            }

            String name = JOptionPane.showInputDialog(this, "Enter a name for your " + choices[classChoice] + ":");

            if (name == null || name.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "A name is required.", "Invalid Name", JOptionPane.WARNING_MESSAGE);
                continue;
            }

            Character newPlayer = switch (classChoice) {
                case 0 -> new Warrior(name.trim());
                case 1 -> new Mage(name.trim());
                default -> new Archer(name.trim());
            };

            players.add(newPlayer);
            appendLog(newPlayer.getName() + " joined the party as a " + choices[classChoice] + "!");
            updateStatus("Created " + newPlayer.getName() + ".");
            updatePanels();
        }
    }

    private void startWave() {
        enemies = createWave((int) currentWave);
        turnCount = 1;
        currentPlayerIndex = 0;
        appendLog("\n--- Wave " + currentWave + " begins! ---");
        updatePanels();
        updateStatus("Wave " + currentWave + " started.");
        animateBattleScene();
        // disable shop while the wave is active
        shopButton.setEnabled(false);
        enableActionButtons(true);
        nextPlayerTurn();
    }

    private ArrayList<Enemy> createWave(int wave) {
        ArrayList<Enemy> waveEnemies = new ArrayList<>();
        switch (wave) {
            case 1 -> waveEnemies.add(new Enemy("Goblin", "Goblin", 120, 20, 5));
            case 2 -> {
                waveEnemies.add(new Enemy("Orc", "Orc", 150, 30, 10));
                waveEnemies.add(new Enemy("Goblin Archer", "Goblin", 200, 25, 5));
            }
            case 3 -> waveEnemies.add(new Enemy("Dark Mage", "Dark Mage", 300, 40, 8));
            case 4 -> waveEnemies.add(new Enemy("Dragon", "Boss", 350, 100, 20));
            default -> waveEnemies.add(new Enemy("Shadow Beast", "Elite", 280, 50, 12));
        }

        return waveEnemies;
    }

    private void nextPlayerTurn() {
        if (!hasLivingPlayers()) {
            endBattle(false);
            return;
        }
        if (!hasLivingEnemies()) {
            endBattle(true);
            return;
        }

        currentPlayerIndex = findNextAvailablePlayer(currentPlayerIndex);

        if (currentPlayerIndex < 0) {
            enemyTurn();
            return;
        }

        Character active = players.get(currentPlayerIndex);
        if (!active.isAlive()) {
            currentPlayerIndex++;
            nextPlayerTurn();
            return;
        }

        updateStatus(active.getName() + " is ready. Choose an action.");
        enableActionButtons(true);
    }

    private int findNextAvailablePlayer(int index) {
        for (int i = index; i < players.size(); i++) {
            if (players.get(i).isAlive()) {
                return i;
            }
        }
        return -1;
    }

    private void handleAction(int actionType) {
        if (!hasLivingPlayers() || !hasLivingEnemies()) {
            return;
        }

        Character active = players.get(currentPlayerIndex);
        if (active == null || !active.isAlive()) {
            return;
        }

        switch (actionType) {
            case 1 -> performAttack(active);
            case 2 -> performDefend(active);
            case 3 -> performSkill(active);
            case 4 -> performItem();
            case 5 -> performFlee(active);
            default -> {
            }
        }
    }

    private void performAttack(Character active) {
        Enemy target = chooseEnemyTarget();
        if (target == null) {
            return;
        }

        BattleAction action = new AttackAction(active, target);
        BattleResult result = action.execute();
        appendLog(result.getMessage());
        afterPlayerAction(result);
    }

    private void performDefend(Character active) {
        BattleAction action = new DefendAction(active);
        BattleResult result = action.execute();
        appendLog(result.getMessage());
        afterPlayerAction(result);
    }

    private void performSkill(Character active) {
        Enemy target = chooseEnemyTarget();
        if (target == null) {
            return;
        }

        BattleAction action = new SkillAction(active, target);
        BattleResult result = action.execute();
        appendLog(result.getMessage());

        if (result.getMessage().contains("does not have enough mana")) {
            updateStatus(active.getName() + " needs more mana to use that skill.");
            updatePanels();
            return;
        }

        afterPlayerAction(result);
    }

    private void performItem() {
        if (inventory.isEmpty()) {
            updateStatus("Inventory is empty.");
            JOptionPane.showMessageDialog(this, "There are no items in the inventory.", "Inventory", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        List<Item> items = inventory.getItems();

        // group items by name for stacking
        java.util.Map<String, Integer> counts = new java.util.LinkedHashMap<>();
        java.util.Map<String, Item> example = new java.util.HashMap<>();
        for (Item it : items) {
            String key = it.getName();
            counts.put(key, counts.getOrDefault(key, 0) + 1);
            example.putIfAbsent(key, it);
        }

        String[] choices = new String[counts.size()];
        String[] keys = new String[counts.size()];
        int idx = 0;
        for (java.util.Map.Entry<String, Integer> e : counts.entrySet()) {
            Item sample = example.get(e.getKey());
            choices[idx] = e.getKey() + " x" + e.getValue() + " - " + sample.getDescription();
            keys[idx] = e.getKey();
            idx++;
        }

        int choice = JOptionPane.showOptionDialog(this,
            "Choose an item to use:",
            "Inventory",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            null,
            choices,
            choices[0]);

        if (choice < 0 || choice >= keys.length) {
            return;
        }

        Character target = choosePlayerTarget();
        if (target == null) {
            return;
        }

        String selectedName = keys[choice];

        try {
            // find first index of an item with this name
            int removeIndex = -1;
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).getName().equals(selectedName)) {
                    removeIndex = i;
                    break;
                }
            }

            if (removeIndex == -1) {
                throw new IndexOutOfBoundsException();
            }

            Item item = items.get(removeIndex);
            inventory.useItem(removeIndex, target);
            appendLog("Used " + item.getName() + " on " + target.getName() + ".");
        } catch (EmptyInventoryException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Inventory", JOptionPane.ERROR_MESSAGE);
        } catch (IndexOutOfBoundsException e) {
            JOptionPane.showMessageDialog(this, "Invalid item selected.", "Inventory", JOptionPane.ERROR_MESSAGE);
        }

        afterPlayerAction(new BattleResult("Item used.", 0, false, false));
    }

    private void performFlee(Character active) {
        double fleeChance = (double) (Math.random() * 100);
        if (fleeChance < GameConstants.FLEE_SUCCESS_CHANCE) {
            appendLog(active.getName() + " successfully fled the battle!");
            updateStatus("Fled the battle.");
            enableActionButtons(false);
            endBattle(false);
            return;
        }

        appendLog(active.getName() + " failed to flee.");
        appendLog("All living party members take " + GameConstants.FLEE_DAMAGE + " damage.");
        for (Character player : players) {
            if (player.isAlive()) {
                player.takeDamage(GameConstants.FLEE_DAMAGE);
                appendLog(player.getName() + " drops to " + player.getHp() + " HP.");
            }
        }
        updatePanels();
        enemyTurn();
    }

    private Enemy chooseEnemyTarget() {
        ArrayList<Enemy> aliveEnemies = new ArrayList<>();
        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) {
                aliveEnemies.add(enemy);
            }
        }

        if (aliveEnemies.isEmpty()) {
            return null;
        }

        String[] options = new String[aliveEnemies.size()];
        for (int i = 0; i < aliveEnemies.size(); i++) {
            Enemy enemy = aliveEnemies.get(i);
            options[i] = enemy.getName() + " (HP: " + (double) enemy.getHp() + ")";
        }

        int choice = JOptionPane.showOptionDialog(this,
            "Choose an enemy target:",
            "Target Selection",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);

        if (choice < 0 || choice >= aliveEnemies.size()) {
            return null;
        }

        return aliveEnemies.get(choice);
    }

    private Character choosePlayerTarget() {
        if (players.isEmpty()) {
            return null;
        }

        String[] options = new String[players.size()];
        for (int i = 0; i < players.size(); i++) {
            Character player = players.get(i);
            String status = player.isAlive() ? "HP: " + (double) player.getHp() : "DEAD";
            options[i] = player.getName() + " (" + status + ")";
        }

        int choice = JOptionPane.showOptionDialog(this,
            "Choose a party member:",
            "Target Selection",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);

        if (choice < 0 || choice >= players.size()) {
            return null;
        }

        return players.get(choice);
    }

    private void afterPlayerAction(BattleResult result) {
        updatePanels();

        if (!hasLivingEnemies()) {
            endBattle(true);
            return;
        }

        currentPlayerIndex++;
        updateStatus("Action complete: " + result.getMessage());
        nextPlayerTurn();
    }

    private void enemyTurn() {
        enableActionButtons(false);
        if (!hasLivingEnemies()) {
            endBattle(true);
            return;
        }

        appendLog("\n--- Enemy Turn ---");
        for (Enemy enemy : enemies) {
            if (!enemy.isAlive()) {
                continue;
            }
            BattleResult result = enemy.getAi().decideAction(enemy, players);
            appendLog(result.getMessage());
            updatePanels();
            if (!hasLivingPlayers()) {
                break;
            }
        }

        if (!hasLivingPlayers()) {
            endBattle(false);
            return;
        }

        turnCount++;
        for (Character player : players) {
            if (player.isAlive()) {
                player.regenerateMana(10);
            }
        }

        currentPlayerIndex = 0;
        updatePanels();
        animateBattleScene();
        nextPlayerTurn();
    }

    private boolean hasLivingPlayers() {
        for (Character player : players) {
            if (player.isAlive()) {
                return true;
            }
        }
        return false;
    }

    private boolean hasLivingEnemies() {
        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) {
                return true;
            }
        }
        return false;
    }

    private void endBattle(boolean victory) {
        enableActionButtons(false);
        if (victory) {
            enemiesDefeated += enemies.size();
            gold += GameConstants.WAVE_REWARD;
            appendLog("\nVictory! Wave " + currentWave + " cleared.");
            updateStatus("Wave " + currentWave + " cleared. Visit the shop before the next wave.");
            JOptionPane.showMessageDialog(this,
                "VICTORY!\nEnemies defeated: " + enemiesDefeated + "\nTurns taken: " + turnCount,
                "Victory",
                JOptionPane.INFORMATION_MESSAGE);
            updatePanels();
            shopButton.setEnabled(true);
            saveButton.setEnabled(true);

            // Allow the player to open the shop as many times as desired
            // before choosing to proceed to the next wave.
            boolean proceedToNext = false;
            while (!proceedToNext) {
                String[] postOptions = {"Open Shop", "Start Next Wave", "Exit Game"};
                int choice = JOptionPane.showOptionDialog(this,
                    "What would you like to do next?\nGold: " + gold,
                    "Post-Wave Options",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    postOptions,
                    postOptions[0]);

                switch (choice) {
                    case 0 -> {
                        openShop();
                        // refresh panels after shopping
                        updatePanels();
                    }
                    case 1 -> proceedToNext = true;
                    default -> {
                        // Exit the game window
                        dispose();
                        return;
                    }
                }
            }

            currentWave++;
            startWave();
        } else {
            appendLog("\nDefeat... The party has fallen.");
            updateStatus("Game Over. Refresh to play again.");
            JOptionPane.showMessageDialog(this,
                "DEFEAT...\nEnemies defeated: " + enemiesDefeated + "\nTurns taken: " + turnCount,
                "Defeat",
                JOptionPane.ERROR_MESSAGE);
            enableActionButtons(false);
            shopButton.setEnabled(false);
            saveButton.setEnabled(false);
        }
    }

    private void openShop() {
        // maintain backward compatibility with modal shop
        String[] options = {
            "Health Potion (20)",
            "Mega Potion (50)",
            "Mana Potion (30)",
            "Revive Potion (100)",
            "Exit Shop"
        };

        while (true) {
            int choice = JOptionPane.showOptionDialog(this,
                "Gold: " + gold + "\nChoose a purchase:",
                "Shop",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]);

            if (choice < 0 || choice == 4) {
                break;
            }

            switch (choice) {
                case 0 -> buyItem(new HealthPotion(), GameConstants.HEALTH_POTION_PRICE);
                case 1 -> buyItem(new MegaPotion(), GameConstants.MEGA_POTION_PRICE);
                case 2 -> buyItem(new ManaPotion(), GameConstants.MANA_POTION_PRICE);
                case 3 -> buyItem(new RevivePotion(), GameConstants.REVIVE_POTION_PRICE);
                default -> {
                }
            }
        }

        updatePanels();
    }

    // New: open ShopFrame as separate window
    private void openShopFrame() {
        ShopFrame sf = new ShopFrame(inventory, gold, this);
        sf.setVisible(true);
    }

    private void openInventoryFrame() {
        InventoryFrame inf = new InventoryFrame(inventory, this);
        inf.setVisible(true);
    }

    private void openCharacterSheetFrame() {
        PartySheetFrame psf = new PartySheetFrame(players);
        psf.setVisible(true);
    }

    // expose helper for InventoryFrame
    public Character choosePlayerTargetDialog() {
        return choosePlayerTarget();
    }

    // allow ShopFrame to update gold
    public void setGold(double g) {
        this.gold = g;
    }

    private void buyItem(Item item, double price) {
        if (gold >= price) {
            inventory.addItem(item);
            gold -= price;
            appendLog("Bought " + item.getName() + " for " + price + " gold.");
            updateStatus(item.getName() + " added to inventory.");
        } else {
            JOptionPane.showMessageDialog(this, "Not enough gold.", "Shop", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void saveGame() {
        SaveManager saveManager = new SaveManager();
        saveManager.saveGame(currentWave, gold, players, inventory);
        JOptionPane.showMessageDialog(this, "Game saved.", "Save", JOptionPane.INFORMATION_MESSAGE);
    }

    private void animateBattleScene() {
        // if there's a battle image in assets, use it; otherwise fall back to ASCII animation
        CardLayout cl = (CardLayout) scenePanel.getLayout();
        ImageIcon battleIcon = loadIcon("battle.png", 560, 320);
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

    private void updatePanels() {
        SwingUtilities.invokeLater(() -> {
            waveLabel.setText("Wave: " + currentWave);
            goldLabel.setText("Gold: " + gold);
            turnLabel.setText("Turn: " + turnCount);
            scoreLabel.setText("Enemies Defeated: " + enemiesDefeated);

            partyPanel.removeAll();
            if (players != null) {
                for (Character player : players) {
                    JPanel line = new JPanel(new BorderLayout(8, 4));
                    line.setBackground(new Color(250, 250, 250));
                    JLabel label = new JLabel(player.getName() + " (" + player.getClass().getSimpleName() + ")");
                    label.setForeground(Color.DARK_GRAY);
                    JProgressBar hpBar = new JProgressBar(0, Math.max(1, (int) player.getMaxHp()));
                    hpBar.setValue(Math.max(0, (int) player.getHp()));
                    hpBar.setStringPainted(true);
                    hpBar.setForeground(new Color(150, 220, 150));
                    hpBar.setString("HP " + (int) player.getHp() + "/" + (int) player.getMaxHp());

                    JProgressBar manaBar = new JProgressBar(0, Math.max(1, (int) player.getMaxMana()));
                    manaBar.setValue(Math.max(0, (int) player.getMana()));
                    manaBar.setStringPainted(true);
                    manaBar.setForeground(new Color(150, 180, 240));
                    manaBar.setString("MP " + (int) player.getMana() + "/" + (int) player.getMaxMana());

                    JPanel bars = new JPanel(new GridLayout(2, 1));
                    bars.setBackground(new Color(250, 250, 250));
                    bars.add(hpBar);
                    bars.add(manaBar);

                    line.add(label, BorderLayout.NORTH);
                    line.add(bars, BorderLayout.CENTER);
                    partyPanel.add(line);
                }
            }

            enemyPanel.removeAll();
            if (enemies != null) {
                for (Enemy enemy : enemies) {
                    JPanel line = new JPanel(new BorderLayout(8, 4));
                    JLabel label = new JLabel(enemy.getName() + " (" + enemy.getEnemyType() + ")");
                    JProgressBar bar = new JProgressBar(0, (int) enemy.getMaxHp());
                    bar.setValue((int) enemy.getHp());
                    bar.setStringPainted(true);
                    bar.setString("HP " + (int) enemy.getHp() + "/" + (int) enemy.getMaxHp());
                    line.add(label, BorderLayout.NORTH);
                    line.add(bar, BorderLayout.CENTER);
                    enemyPanel.add(line);
                }
            } else {
                enemyPanel.add(new JLabel("No enemies yet."));
            }

            inventoryPanel.removeAll();
            if (inventory == null || inventory.getItems().isEmpty()) {
                inventoryPanel.add(new JLabel("No items in inventory."));
            } else {
                // group items by class name and show counts (stacking)
                java.util.Map<String, Integer> counts = new java.util.LinkedHashMap<>();
                java.util.Map<String, Item> example = new java.util.HashMap<>();
                for (Item item : inventory.getItems()) {
                    String key = item.getName();
                    counts.put(key, counts.getOrDefault(key, 0) + 1);
                    example.putIfAbsent(key, item);
                }

                for (java.util.Map.Entry<String, Integer> e : counts.entrySet()) {
                    Item sample = example.get(e.getKey());
                    inventoryPanel.add(new JLabel(e.getKey() + " x" + e.getValue() + " - " + sample.getDescription()));
                }
            }

            partyPanel.revalidate();
            partyPanel.repaint();
            enemyPanel.revalidate();
            enemyPanel.repaint();
            inventoryPanel.revalidate();
            inventoryPanel.repaint();
        });
    }

    private void updateStatus(String text) {
        statusLabel.setText(text);
    }

    private void appendLog(String text) {
        logArea.append(text + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    // Public wrappers for other frames
    public void refreshPanels() {
        updatePanels();
    }

    public void appendLogPublic(String text) {
        appendLog(text);
    }

    private void enableActionButtons(boolean enabled) {
        attackButton.setEnabled(enabled);
        defendButton.setEnabled(enabled);
        skillButton.setEnabled(enabled);
        itemButton.setEnabled(enabled);
        fleeButton.setEnabled(enabled);
    }
}

