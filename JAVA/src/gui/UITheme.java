package gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

public class UITheme {

    // Palette
    public static final Color ESPRESSO   = Color.decode("#291C0E");
    public static final Color RUSSET     = Color.decode("#6E473B");
    public static final Color SANDSTONE  = Color.decode("#A78D78");
    public static final Color PUTTY      = Color.decode("#BEB5A9");
    public static final Color ALMOND     = Color.decode("#E1D4C2");
    public static final Color CARD_BG    = Color.decode("#FAF7F4");
    public static final Color DARK_TEXT  = Color.decode("#1A1008");
    public static final Color ROW_ODD    = CARD_BG;
    public static final Color ROW_EVEN   = Color.decode("#F5F0EA");
    public static final Color SEL_BG     = SANDSTONE;
    public static final Color SEL_FG     = Color.WHITE;
    public static final Color STATUS_GREEN = Color.decode("#2D6A4F");
    public static final Color STATUS_RED   = Color.decode("#9B2335");
    public static final Color STATUS_AMBER = Color.decode("#7D5A1E");
    public static final Color STATUS_GREY  = Color.decode("#5F5E5A");

    // Fonts
    public static final Font F_DISPLAY = new Font("Georgia",   Font.BOLD,  13);
    public static final Font F_HEADING = new Font("Georgia",   Font.BOLD,  22);
    public static final Font F_SUBHEAD = new Font("Georgia",   Font.ITALIC,13);
    public static final Font F_BODY    = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font F_BODY_B  = new Font("SansSerif", Font.BOLD,  13);
    public static final Font F_SMALL   = new Font("SansSerif", Font.PLAIN, 11);
    public static final Font F_SMALL_B = new Font("SansSerif", Font.BOLD,  11);
    public static final Font F_NAV     = new Font("SansSerif", Font.BOLD,  12);
    public static final Font F_HUGE    = new Font("Georgia",   Font.BOLD,  52);
    public static final Font F_LARGE   = new Font("Georgia",   Font.BOLD,  30);

    public static void applyGlobalDefaults() {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
        catch (Exception ignored) {}
        UIManager.put("Table.font",                F_BODY);
        UIManager.put("Table.rowHeight",           34);
        UIManager.put("TableHeader.font",          F_BODY_B);
        UIManager.put("TableHeader.background",    RUSSET);
        UIManager.put("TableHeader.foreground",    Color.WHITE);
        UIManager.put("Table.background",          CARD_BG);
        UIManager.put("Table.foreground",          DARK_TEXT);
        UIManager.put("Table.selectionBackground", SEL_BG);
        UIManager.put("Table.selectionForeground", SEL_FG);
        UIManager.put("Table.gridColor",           ALMOND);
        UIManager.put("ScrollPane.background",     ALMOND);
        UIManager.put("ScrollBar.width",           8);
        UIManager.put("Panel.background",          ALMOND);
    }

    public static JLabel sectionHeader(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(F_HEADING);
        lbl.setForeground(ESPRESSO);
        lbl.setBorder(new EmptyBorder(0, 0, 16, 0));
        return lbl;
    }

    public static JPanel contentPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setBackground(ALMOND);
        p.setBorder(new EmptyBorder(28, 30, 28, 30));
        return p;
    }

    public static JPanel heroPanel() {
        return new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, ESPRESSO, getWidth(), 0, RUSSET);
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 18, 18));
                g2.dispose();
            }
        };
    }

    public static JPanel statCard(Color accentBar) {
        return new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                g2.setColor(accentBar);
                g2.fillRoundRect(0, 0, getWidth(), 5, 5, 5);
                g2.dispose();
            }
        };
    }

    public static JScrollPane scrollPane(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(PUTTY, 1));
        sp.getViewport().setBackground(CARD_BG);
        sp.setBackground(CARD_BG);
        return sp;
    }

    public static JLabel pill(String text, Color bg, Color fg) {
        JLabel lbl = new JLabel(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lbl.setFont(F_SMALL);
        lbl.setForeground(fg);
        lbl.setBorder(new EmptyBorder(5, 14, 5, 14));
        lbl.setOpaque(false);
        return lbl;
    }

    public static JLabel statusBadge(String status) {
        Color bg;
        switch (status) {
            case "Present": case "MedicalApproved": case "Proper":
                bg = STATUS_GREEN; break;
            case "Absent": case "MedicalDeclined":
                bg = STATUS_RED;   break;
            case "Pending": case "Repeat": case "Batchmissed":
                bg = STATUS_AMBER; break;
            default: bg = STATUS_GREY; break;
        }
        final Color finalBg = bg;
        JLabel lbl = new JLabel(status, SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(finalBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lbl.setFont(F_SMALL_B);
        lbl.setForeground(Color.WHITE);
        lbl.setBorder(new EmptyBorder(3, 10, 3, 10));
        lbl.setOpaque(false);
        return lbl;
    }

    public static JButton primaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? ESPRESSO : RUSSET);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(F_BODY_B);
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 22, 10, 22));
        return btn;
    }

    public static JTextField inputField(int cols) {
        JTextField tf = new JTextField(cols);
        tf.setFont(F_BODY);
        tf.setForeground(DARK_TEXT);
        tf.setBackground(CARD_BG);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PUTTY, 1),
            new EmptyBorder(6, 10, 6, 10)));
        return tf;
    }

    public static JPasswordField passwordField(int cols) {
        JPasswordField pf = new JPasswordField(cols);
        pf.setFont(F_BODY);
        pf.setForeground(DARK_TEXT);
        pf.setBackground(CARD_BG);
        pf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PUTTY, 1),
            new EmptyBorder(6, 10, 6, 10)));
        return pf;
    }
}
