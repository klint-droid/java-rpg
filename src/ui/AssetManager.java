package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * Handles all asset loading, placeholder generation, and picture importing.
 * Extracted from RpgGameUI (lines 251-369) to follow Single Responsibility.
 */
public class AssetManager {

    private static final String ASSETS_DIR = "assets";

    /**
     * Loads an icon from the assets directory. If the file doesn't exist,
     * generates a colored placeholder with a text label.
     */
    public ImageIcon loadIcon(String filename, double w, double h) {
        try {
            File f = new File(ASSETS_DIR + File.separator + filename);
            Image img;
            if (f.exists()) {
                img = ImageIO.read(f);
            } else {
                int ww = Math.max(1, w > 0 ? (int) w : 32);
                int hh = Math.max(1, h > 0 ? (int) h : 32);
                BufferedImage bi = new BufferedImage(ww, hh, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = bi.createGraphics();
                g.setColor(Color.DARK_GRAY);
                g.fillRect(0, 0, ww, hh);
                g.setColor(Color.WHITE);
                g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, Math.max(10, ww / 2)));
                String t = filename.replaceAll("\\..+$", "");
                if (t.length() > 2) t = t.substring(0, 2).toUpperCase();
                g.drawString(t, Math.max(4, ww / 6), Math.max(12, hh / 2));
                g.dispose();
                img = bi;
            }
            if (w > 0 && h > 0) img = img.getScaledInstance((int) w, (int) h, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Ensures all required asset files exist, creating placeholders for any missing ones.
     * Also imports provided pictures from src/pics if available.
     */
    public void ensureAssetsExist() {
        try {
            importProvidedPics();

            File dir = new File(ASSETS_DIR);
            if (!dir.exists()) dir.mkdirs();

            createPlaceholder("icon.png", 64, 64, Color.DARK_GRAY, "ICON");
            createPlaceholder("attack.png", 24, 24, Color.RED, "A");
            createPlaceholder("defend.png", 24, 24, Color.BLUE, "D");
            createPlaceholder("skill.png", 24, 24, Color.MAGENTA, "S");
            createPlaceholder("item.png", 24, 24, Color.ORANGE, "I");
            createPlaceholder("flee.png", 24, 24, Color.GRAY, "F");
            createPlaceholder("shop.png", 24, 24, Color.GREEN, "$");
            createPlaceholder("save.png", 20, 20, Color.CYAN, "S");
            createPlaceholder("battle.gif", 440, 240, Color.LIGHT_GRAY, "BATTLE");
            createPlaceholder("battle.png", 560, 320, Color.LIGHT_GRAY, "BATTLE");

            createPlaceholder("char_warrior.png", 96, 96, new Color(200, 120, 120), "W");
            createPlaceholder("char_mage.png", 96, 96, new Color(120, 160, 220), "M");
            createPlaceholder("char_archer.png", 96, 96, new Color(160, 200, 140), "A");

            // Enemy portraits
            createPlaceholder("enemy_goblin.png", 96, 96, new Color(100, 160, 80), "GO");
            createPlaceholder("enemy_goblin_archer.png", 96, 96, new Color(120, 140, 70), "GA");
            createPlaceholder("enemy_dark_mage.png", 96, 96, new Color(100, 60, 140), "DM");
            createPlaceholder("enemy_dragon.png", 96, 96, new Color(180, 60, 60), "DR");
            createPlaceholder("enemy_orc.png", 96, 96, new Color(80, 120, 60), "OR");
            createPlaceholder("enemy_shadow_beast.png", 96, 96, new Color(60, 60, 80), "SB");

            createPlaceholder("item_health.png", 32, 32, Color.RED, "H");
            createPlaceholder("item_mana.png", 32, 32, Color.BLUE, "MP");
            createPlaceholder("item_mega.png", 32, 32, Color.MAGENTA, "MG");
            createPlaceholder("item_revive.png", 32, 32, Color.ORANGE, "R");
            createPlaceholder("item_generic.png", 32, 32, Color.GRAY, "I");
        } catch (Exception ex) {
            // Silent fallback — placeholders are best-effort
        }
    }

    private void importProvidedPics() {
        try {
            File srcDir = new File("src" + File.separator + "pics");
            if (!srcDir.exists() || !srcDir.isDirectory()) return;

            File[] files = srcDir.listFiles();
            if (files == null) return;

            for (File f : files) {
                String name = f.getName().toLowerCase();
                String target = null;

                // Character portraits
                if (name.contains("warrior")) target = "char_warrior.png";
                else if (name.contains("archer") && !name.contains("goblin")) target = "char_archer.png";
                else if (name.contains("mage") && !name.contains("dark")) target = "char_mage.png";

                // Enemy portraits — order matters (specific before general)
                else if (name.contains("goblin") && (name.contains("arher") || name.contains("archer")))
                    target = "enemy_goblin_archer.png";
                else if (name.contains("goblin")) target = "enemy_goblin.png";
                else if (name.contains("dark") && name.contains("mage")) target = "enemy_dark_mage.png";
                else if (name.contains("dragon")) target = "enemy_dragon.png";

                // Potion items
                else if (name.contains("health")) target = "item_health.png";
                else if (name.contains("mana")) target = "item_mana.png";
                else if (name.contains("mega")) target = "item_mega.png";
                else if (name.contains("revive")) target = "item_revive.png";

                // Battlefield background
                else if (name.contains("battle") || name.contains("batle") || name.contains("dungeon"))
                    target = "battle.png";

                if (target != null) {
                    File out = new File(ASSETS_DIR + File.separator + target);
                    try {
                        Path srcPath = f.toPath();
                        Path dstPath = out.toPath();
                        File ad = out.getParentFile();
                        if (!ad.exists()) ad.mkdirs();
                        Files.copy(srcPath, dstPath, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException ex) {
                        System.out.println(ex.getLocalizedMessage());
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getLocalizedMessage());
        }
    }

    private void createPlaceholder(String filename, double w, double h, Color bg, String text) {
        File f = new File(ASSETS_DIR + File.separator + filename);
        if (f.exists()) return;
        try {
            int imgW = Math.max(1, (int) w);
            int imgH = Math.max(1, (int) h);
            BufferedImage img = new BufferedImage(imgW, imgH, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            g.setColor(bg);
            g.fillRect(0, 0, imgW, imgH);
            g.setColor(Color.WHITE);
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, Math.max(12, (int) (w / 4))));
            int tx = Math.max(4, (int) (w / 6));
            int ty = Math.max(14, (int) (h / 2 + 6));
            g.drawString(text, tx, ty);
            g.dispose();
            ImageIO.write(img, "png", f);
        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());
        }
    }
}
