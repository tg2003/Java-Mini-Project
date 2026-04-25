package gui;

import models.*;
import service.*;
import util.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class ProfilePanel extends JPanel {

    private final Undergraduate student;
    private final StudentService svc = new StudentService();

    public ProfilePanel(Undergraduate student) {
        this.student = student;
        setBackground(UITheme.ALMOND);
        setLayout(new BorderLayout(0, 0));
        setBorder(new EmptyBorder(28, 30, 28, 30));
        build();
    }

    private void build() {
        add(UITheme.sectionHeader("My Profile"), BorderLayout.NORTH);

        JPanel body = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 0));
        body.setOpaque(false);
        body.add(buildAvatar());
        body.add(buildInfoCard());
        add(body, BorderLayout.CENTER);
    }

    private JPanel buildAvatar() {
        String initials = student.getInitials();
        JPanel card = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Card background
                g2.setColor(UITheme.CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 18, 18));

                // Top gradient banner
                GradientPaint gp = new GradientPaint(0, 0, UITheme.ESPRESSO, 0, 110, UITheme.RUSSET);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), 110, 18, 18);
                g2.fillRect(0, 90, getWidth(), 20);

                // Avatar circle
                int cx = getWidth() / 2, cy = 110;
                g2.setColor(UITheme.SANDSTONE);
                g2.fillOval(cx - 40, cy - 40, 80, 80);

                // Initials
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Georgia", Font.BOLD, 26));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(initials, cx - fm.stringWidth(initials) / 2, cy + fm.getAscent() / 2 - 2);

                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(200, 300); }
        };
        card.setOpaque(false);

        // Name and role labels below the avatar
        JLabel nameL = new JLabel(student.getName(), SwingConstants.CENTER);
        nameL.setFont(UITheme.F_DISPLAY);
        nameL.setForeground(UITheme.ESPRESSO);
        nameL.setBounds(0, 180, 200, 24);

        JLabel roleL = new JLabel("Undergraduate", SwingConstants.CENTER);
        roleL.setFont(UITheme.F_SMALL);
        roleL.setForeground(UITheme.SANDSTONE);
        roleL.setBounds(0, 205, 200, 20);

        JLabel deptL = new JLabel(student.getDptName() + " Faculty", SwingConstants.CENTER);
        deptL.setFont(UITheme.F_SMALL);
        deptL.setForeground(UITheme.PUTTY);
        deptL.setBounds(0, 225, 200, 20);

        card.add(nameL);
        card.add(roleL);
        card.add(deptL);
        return card;
    }

    private JPanel buildInfoCard() {
        List<String> phones = svc.getPhones(student.getUgId());
        String phoneStr = phones.isEmpty() ? "—" : String.join(", ", phones);

        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 18, 18));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(420, 300));
        card.setBorder(new EmptyBorder(24, 28, 24, 28));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        // Section divider title
        JLabel secTitle = new JLabel("Personal Information");
        secTitle.setFont(UITheme.F_BODY_B);
        secTitle.setForeground(UITheme.RUSSET);
        secTitle.setBorder(new EmptyBorder(0, 0, 12, 0));
        card.add(secTitle);

        String[][] rows = {
            {"Student ID",  student.getUgId()},
            {"Full Name",   student.getName()},
            {"Email",       student.getEmail()},
            {"NIC",         student.getNic()},
            {"Date of Birth", DateFormatter.format(student.getDob())},
            {"Department",  student.getDptName()},
            {"Address",     student.getAddress()},
            {"Phone(s)",    phoneStr}
        };

        for (String[] row : rows) {
            card.add(profileRow(row[0], row[1]));
            card.add(Box.createVerticalStrut(2));
        }
        return card;
    }

    private JPanel profileRow(String key, String val) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(new MatteBorder(0, 0, 1, 0, UITheme.ALMOND));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        JLabel k = new JLabel(key);
        k.setFont(UITheme.F_SMALL);
        k.setForeground(UITheme.PUTTY);
        k.setPreferredSize(new Dimension(120, 28));

        JLabel v = new JLabel(val);
        v.setFont(UITheme.F_BODY);
        v.setForeground(UITheme.DARK_TEXT);

        p.add(k, BorderLayout.WEST);
        p.add(v, BorderLayout.CENTER);
        return p;
    }
}
