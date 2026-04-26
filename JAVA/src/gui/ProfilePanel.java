package gui;

import models.Undergraduate;
import service.StudentService;
import util.DateFormatter;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

public class ProfilePanel extends JPanel {

    private final Undergraduate  student;
    private final Runnable       onPhotoChanged;   // notifies dashboard to refresh top avatar
    private final StudentService svc = new StudentService();

    private JPanel        avatarCard;
    private BufferedImage profileImage = null;

    public ProfilePanel(Undergraduate student, Runnable onPhotoChanged) {
        this.student        = student;
        this.onPhotoChanged = onPhotoChanged;

        setBackground(UITheme.ALMOND);
        setLayout(new BorderLayout(0, 0));
        setBorder(new EmptyBorder(28, 30, 28, 30));

        loadProfileImage();
        build();
    }

    // ── Build UI ──────────────────────────────────────────────────────────────
    private void build() {
        add(UITheme.sectionHeader("My Profile"), BorderLayout.NORTH);

        JPanel body = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 0));
        body.setOpaque(false);

        avatarCard = buildAvatarCard();
        body.add(avatarCard);
        body.add(buildInfoCard());
        add(body, BorderLayout.CENTER);
    }

    // ── Avatar Card ───────────────────────────────────────────────────────────
    private JPanel buildAvatarCard() {
        JPanel card = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Card background
                g2.setColor(UITheme.CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 18, 18));

                // Top banner
                GradientPaint gp = new GradientPaint(0, 0, UITheme.ESPRESSO, 0, 110, UITheme.RUSSET);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), 110, 18, 18);
                g2.fillRect(0, 90, getWidth(), 20);

                int cx = getWidth() / 2;
                int cy = 110;
                int r  = 40;

                if (profileImage != null) {
                    Shape clip = new Ellipse2D.Float(cx - r, cy - r, r * 2, r * 2);
                    g2.setClip(clip);
                    g2.drawImage(profileImage, cx - r, cy - r, r * 2, r * 2, null);
                    g2.setClip(null);
                    g2.setColor(UITheme.SANDSTONE);
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawOval(cx - r, cy - r, r * 2, r * 2);
                } else {
                    g2.setColor(UITheme.SANDSTONE);
                    g2.fillOval(cx - r, cy - r, r * 2, r * 2);
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Georgia", Font.BOLD, 26));
                    FontMetrics fm = g2.getFontMetrics();
                    String initials = student.getInitials();
                    g2.drawString(initials,
                            cx - fm.stringWidth(initials) / 2,
                            cy + fm.getAscent() / 2 - 2);
                }

                // Camera icon hint
                g2.setColor(new Color(0, 0, 0, 120));
                g2.fillOval(cx + r - 16, cy + r - 16, 22, 22);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Dialog", Font.PLAIN, 12));
                g2.drawString("\uD83D\uDCF7", cx + r - 12, cy + r - 1);

                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() { return new Dimension(200, 300); }
        };
        card.setOpaque(false);

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

        JLabel changeBtn = new JLabel("Change Photo", SwingConstants.CENTER);
        changeBtn.setFont(UITheme.F_SMALL);
        changeBtn.setForeground(UITheme.RUSSET);
        changeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        changeBtn.setBounds(25, 255, 150, 22);
        changeBtn.setBorder(BorderFactory.createLineBorder(UITheme.RUSSET, 1, true));

        changeBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                changeBtn.setOpaque(true);
                changeBtn.setBackground(UITheme.RUSSET);
                changeBtn.setForeground(Color.WHITE);
            }
            @Override public void mouseExited(MouseEvent e) {
                changeBtn.setOpaque(false);
                changeBtn.setForeground(UITheme.RUSSET);
            }
            @Override public void mouseClicked(MouseEvent e) {
                handleChangePhoto();
            }
        });

        card.add(nameL);
        card.add(roleL);
        card.add(deptL);
        card.add(changeBtn);
        return card;
    }

    // ── Change Photo ──────────────────────────────────────────────────────────
    private void handleChangePhoto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Profile Photo");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Image files (JPG, PNG, JPEG)", "jpg", "jpeg", "png"));

        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File chosen = chooser.getSelectedFile();
        boolean success = svc.changeProfilePic(student, chosen);

        if (success) {
            loadProfileImage();          // reload image in ProfilePanel
            avatarCard.repaint();        // redraw avatar here
            if (onPhotoChanged != null)
                onPhotoChanged.run();    // tell dashboard to refresh top-bar avatar
            JOptionPane.showMessageDialog(this,
                    "Profile photo updated successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to update profile photo.\nPlease try again.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Load Image from student.getProfilePic() path ──────────────────────────
    private void loadProfileImage() {
        profileImage = null;
        String path = student.getProfilePic();
        if (path == null || path.isBlank()) return;
        try {
            File f = new File(path);
            if (f.exists()) profileImage = ImageIO.read(f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── Info Card ─────────────────────────────────────────────────────────────
    private JPanel buildInfoCard() {
        List<String> phones = svc.getPhones(student.getUgId());
        String phoneStr = phones.isEmpty() ? "—" : String.join(", ", phones);

        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
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

        JLabel secTitle = new JLabel("Personal Information");
        secTitle.setFont(UITheme.F_BODY_B);
        secTitle.setForeground(UITheme.RUSSET);
        secTitle.setBorder(new EmptyBorder(0, 0, 12, 0));
        card.add(secTitle);

        String[][] rows = {
                {"Student ID",    student.getUgId()},
                {"Full Name",     student.getName()},
                {"Email",         student.getEmail()},
                {"NIC",           student.getNic()},
                {"Date of Birth", DateFormatter.format(student.getDob())},
                {"Department",    student.getDptName()},
                {"Address",       student.getAddress()},
                {"Phone(s)",      phoneStr}
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