package gui;

import models.Undergraduate;
import service.StudentService;
import util.DateFormatter;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class ProfilePanel extends JPanel {

    private final Undergraduate student;
    private final StudentService svc = new StudentService();

    private JPanel avatarCard;
    private BufferedImage profileImage = null;

    // Editable fields
    private JTextField emailField;
    private JTextField houseNoField;
    private JTextField streetField;
    private JTextField cityField;

    // Phone management
    private List<String> phoneList = new ArrayList<>();
    private JPanel phoneListPanel;

    public ProfilePanel(Undergraduate student) {
        this.student = student;
        setBackground(UITheme.ALMOND);
        setLayout(new BorderLayout(0, 0));
        setBorder(new EmptyBorder(28, 30, 28, 30));
        loadProfileImage();
        build();
    }

    private Runnable onPhotoChanged;

    public ProfilePanel(Undergraduate student, Runnable onPhotoChanged) {
        this.student = student;
        this.onPhotoChanged = onPhotoChanged;
        setBackground(UITheme.ALMOND);
        setLayout(new BorderLayout(0, 0));
        setBorder(new EmptyBorder(28, 30, 28, 30));
        loadProfileImage();
        build();
    }

    private void build() {
        add(UITheme.sectionHeader("My Profile"), BorderLayout.NORTH);

        JPanel body = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 0));
        body.setOpaque(false);

        avatarCard = buildAvatarCard();
        body.add(avatarCard);
        body.add(buildInfoCard());
        add(body, BorderLayout.CENTER);
    }

    // ── Avatar Card (unchanged) ───────────────────────────────────────────
    private JPanel buildAvatarCard() {
        String initials = student.getInitials();

        JPanel card = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(UITheme.CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 18, 18));

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
                    g2.drawString(initials,
                            cx - fm.stringWidth(initials) / 2,
                            cy + fm.getAscent() / 2 - 2);
                }

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
                changeBtn.setBackground(UITheme.RUSSET);
                changeBtn.setOpaque(true);
                changeBtn.setForeground(Color.WHITE);
            }
            @Override public void mouseExited(MouseEvent e) {
                changeBtn.setOpaque(false);
                changeBtn.setForeground(UITheme.RUSSET);
            }
            @Override public void mouseClicked(MouseEvent e) { handleChangePhoto(); }
        });

        card.add(nameL);
        card.add(roleL);
        card.add(deptL);
        card.add(changeBtn);
        return card;
    }

    private void handleChangePhoto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Profile Photo");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Image files (JPG, PNG, JPEG)", "jpg", "jpeg", "png"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File chosen = chooser.getSelectedFile();
        boolean success = svc.changeProfilePic(student, chosen);

        if (success) {
            loadProfileImage();
            avatarCard.repaint();
            if (onPhotoChanged != null) onPhotoChanged.run();
            JOptionPane.showMessageDialog(this,
                    "Profile photo updated successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to update profile photo.\nPlease try again.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

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

    // ── Info Card ─────────────────────────────────────────────────────────
    private JPanel buildInfoCard() {
        phoneList = new ArrayList<>(svc.getPhones(student.getUgId()));

        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 18, 18));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(480, 520));
        card.setBorder(new EmptyBorder(24, 28, 24, 28));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        // Section title
        JLabel secTitle = new JLabel("Personal Information");
        secTitle.setFont(UITheme.F_BODY_B);
        secTitle.setForeground(UITheme.RUSSET);
        secTitle.setBorder(new EmptyBorder(0, 0, 12, 0));
        card.add(secTitle);

        // Read-only fields
        card.add(readOnlyRow("Student ID",    student.getUgId()));
        card.add(Box.createVerticalStrut(2));
        card.add(readOnlyRow("Full Name",     student.getName()));
        card.add(Box.createVerticalStrut(2));
        card.add(readOnlyRow("NIC",           student.getNic()));
        card.add(Box.createVerticalStrut(2));
        card.add(readOnlyRow("Date of Birth", DateFormatter.format(student.getDob())));
        card.add(Box.createVerticalStrut(2));
        card.add(readOnlyRow("Department",    student.getDptName()));
        card.add(Box.createVerticalStrut(8));

        // Editable section title
        JLabel editTitle = new JLabel("Contact Details  (editable)");
        editTitle.setFont(UITheme.F_BODY_B);
        editTitle.setForeground(UITheme.RUSSET);
        editTitle.setBorder(new EmptyBorder(8, 0, 8, 0));
        card.add(editTitle);

        // Editable fields
        emailField   = editField(student.getEmail());
        houseNoField = editField(student.getHouseNo());
        streetField  = editField(student.getStreet());
        cityField    = editField(student.getCity());

        card.add(editableRow("Email",    emailField));
        card.add(Box.createVerticalStrut(4));
        card.add(editableRow("House No", houseNoField));
        card.add(Box.createVerticalStrut(4));
        card.add(editableRow("Street",   streetField));
        card.add(Box.createVerticalStrut(4));
        card.add(editableRow("City",     cityField));
        card.add(Box.createVerticalStrut(8));

        // Phone section
        JLabel phoneTitle = new JLabel("Phone Numbers");
        phoneTitle.setFont(UITheme.F_BODY_B);
        phoneTitle.setForeground(UITheme.RUSSET);
        phoneTitle.setBorder(new EmptyBorder(4, 0, 6, 0));
        card.add(phoneTitle);

        phoneListPanel = new JPanel();
        phoneListPanel.setOpaque(false);
        phoneListPanel.setLayout(new BoxLayout(phoneListPanel, BoxLayout.Y_AXIS));
        refreshPhoneList();
        card.add(phoneListPanel);

        // Add phone row
        JPanel addPhoneRow = new JPanel(new BorderLayout(8, 0));
        addPhoneRow.setOpaque(false);
        addPhoneRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        JTextField newPhoneField = UITheme.inputField(12);
        newPhoneField.setToolTipText("Enter 10-digit phone number");
        JButton addBtn = UITheme.primaryButton("+ Add");
        addBtn.addActionListener(e -> {
            String phone = newPhoneField.getText().trim();
            if (!phone.matches("\\d{10}")) {
                JOptionPane.showMessageDialog(this,
                        "Phone number must be exactly 10 digits.",
                        "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (phoneList.contains(phone)) {
                JOptionPane.showMessageDialog(this,
                        "This number is already added.",
                        "Duplicate", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (svc.addPhone(student.getUgId(), phone)) {
                phoneList.add(phone);
                newPhoneField.setText("");
                refreshPhoneList();
                JOptionPane.showMessageDialog(this,
                        "Phone number added.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to add phone number.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        addPhoneRow.add(newPhoneField, BorderLayout.CENTER);
        addPhoneRow.add(addBtn, BorderLayout.EAST);
        card.add(addPhoneRow);
        card.add(Box.createVerticalStrut(12));

        // Save button
        JButton saveBtn = UITheme.primaryButton("Save Changes");
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.addActionListener(e -> saveContactDetails());
        card.add(saveBtn);

        return card;
    }

    // ── Save contact details ──────────────────────────────────────────────
    private void saveContactDetails() {
        String email   = emailField.getText().trim();
        String houseNo = houseNoField.getText().trim();
        String street  = streetField.getText().trim();
        String city    = cityField.getText().trim();

        // Basic validation
        if (email.isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "Email cannot be empty.", "Validation",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!email.matches("^[\\w.+\\-]+@[\\w\\-]+\\.[a-zA-Z]{2,}$")) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid email address.", "Validation",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean saved = svc.updateContactDetails(
                student.getUgId(), email, houseNo, street, city);

        if (saved) {
            JOptionPane.showMessageDialog(this,
                    "Contact details updated successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to save changes. Please try again.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Phone list refresh ────────────────────────────────────────────────
    private void refreshPhoneList() {
        phoneListPanel.removeAll();
        for (String phone : phoneList) {
            JPanel row = new JPanel(new BorderLayout(8, 0));
            row.setOpaque(false);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

            JLabel lbl = new JLabel(phone);
            lbl.setFont(UITheme.F_BODY);
            lbl.setForeground(UITheme.DARK_TEXT);

            JButton removeBtn = new JButton("✕");
            removeBtn.setFont(UITheme.F_SMALL);
            removeBtn.setForeground(UITheme.STATUS_RED);
            removeBtn.setContentAreaFilled(false);
            removeBtn.setBorderPainted(false);
            removeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            removeBtn.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Remove phone number " + phone + "?",
                        "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (svc.deletePhone(student.getUgId(), phone)) {
                        phoneList.remove(phone);
                        refreshPhoneList();
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Failed to remove phone number.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            row.add(lbl, BorderLayout.CENTER);
            row.add(removeBtn, BorderLayout.EAST);
            phoneListPanel.add(row);
            phoneListPanel.add(Box.createVerticalStrut(4));
        }
        phoneListPanel.revalidate();
        phoneListPanel.repaint();
    }

    // ── Row helpers ───────────────────────────────────────────────────────
    private JPanel readOnlyRow(String key, String val) {
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

    private JPanel editableRow(String key, JTextField field) {
        JPanel p = new JPanel(new BorderLayout(8, 0));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        JLabel k = new JLabel(key);
        k.setFont(UITheme.F_SMALL);
        k.setForeground(UITheme.PUTTY);
        k.setPreferredSize(new Dimension(120, 28));

        p.add(k, BorderLayout.WEST);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private JTextField editField(String value) {
        JTextField tf = UITheme.inputField(18);
        tf.setText(value != null ? value : "");
        return tf;
    }
}