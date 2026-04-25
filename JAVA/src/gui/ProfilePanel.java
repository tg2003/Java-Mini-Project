package gui;

import models.Undergraduate;
import service.StudentService;
import util.DataValidator;
import util.DateFormatter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class ProfilePanel extends JPanel {

    private final Undergraduate student;
    private final Runnable onProfileSaved;
    private final StudentService svc = new StudentService();

    private JTextField nameField;
    private JTextField emailField;
    private JTextField nicField;
    private JTextField dobField;
    private JTextField houseNoField;
    private JTextField streetField;
    private JTextField cityField;
    private JTextField phone1Field;
    private JTextField phone2Field;

    public ProfilePanel(Undergraduate student, Runnable onProfileSaved) {
        this.student = student;
        this.onProfileSaved = onProfileSaved;
        setBackground(UITheme.ALMOND);
        setLayout(new BorderLayout(0, 0));
        setBorder(new EmptyBorder(28, 30, 28, 30));
        build();
    }

    private void build() {
        add(UITheme.sectionHeader("My Profile"), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(24, 0));
        body.setOpaque(false);
        body.add(buildAvatarCard(), BorderLayout.EAST);
        body.add(buildEditableCard(), BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);
    }

    private JPanel buildAvatarCard() {
        JPanel card = new JPanel(new BorderLayout(0, 16)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(UITheme.CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(280, 0));
        card.setBorder(new EmptyBorder(24, 20, 24, 20));

        JComponent avatar = new JComponent() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(140, 140);
            }

            @Override
            protected void paintComponent(Graphics g) {
                int size = 140;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.RUSSET);
                g2.fillOval(0, 0, size, size);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Georgia", Font.BOLD, 42));
                String initials = student.getInitials();
                FontMetrics fm = g2.getFontMetrics();
                int x = (size - fm.stringWidth(initials)) / 2;
                int y = ((size - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(initials, x, y);
                g2.dispose();
            }
        };

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.add(avatar);

        JPanel meta = new JPanel();
        meta.setOpaque(false);
        meta.setLayout(new BoxLayout(meta, BoxLayout.Y_AXIS));

        JLabel name = centerLabel(student.getName(), new Font("Georgia", Font.BOLD, 18), UITheme.ESPRESSO);
        JLabel id = centerLabel(student.getUgId(), UITheme.F_BODY_B, UITheme.RUSSET);
        JLabel dept = centerLabel(student.getDptName(), UITheme.F_BODY, UITheme.PUTTY);
        JLabel faculty = centerLabel("Faculty of Technology", UITheme.F_SMALL_B, UITheme.SANDSTONE);

        meta.add(name);
        meta.add(Box.createVerticalStrut(6));
        meta.add(id);
        meta.add(Box.createVerticalStrut(6));
        meta.add(dept);
        meta.add(Box.createVerticalStrut(4));
        meta.add(faculty);

        card.add(top, BorderLayout.NORTH);
        card.add(meta, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildEditableCard() {
        List<String> phones = svc.getPhones(student.getUgId());

        JPanel card = new JPanel(new BorderLayout(0, 18)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(UITheme.CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel form = new JPanel(new GridLayout(0, 2, 16, 14));
        form.setOpaque(false);

        nameField = makeField(student.getName());
        emailField = makeField(student.getEmail());
        nicField = makeField(student.getNic());
        dobField = makeField(DateFormatter.shortFormat(student.getDob()));
        houseNoField = makeField(student.getHouseNo());
        streetField = makeField(student.getStreet());
        cityField = makeField(student.getCity());
        phone1Field = makeField(phones.size() > 0 ? phones.get(0) : "");
        phone2Field = makeField(phones.size() > 1 ? phones.get(1) : "");

        form.add(labeledField("Full Name", nameField));
        form.add(labeledField("Email", emailField));
        form.add(labeledField("NIC", nicField));
        form.add(labeledField("Date of Birth", dobField));
        form.add(labeledField("House No", houseNoField));
        form.add(labeledField("Street", streetField));
        form.add(labeledField("City", cityField));
        form.add(labeledField("Department", readOnlyField(student.getDptName())));
        form.add(labeledField("Phone 1", phone1Field));
        form.add(labeledField("Phone 2", phone2Field));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);
        JButton reset = ghostButton("Reset");
        reset.addActionListener(e -> rebuild());
        JButton save = UITheme.primaryButton("Save Profile");
        save.addActionListener(e -> saveProfile());
        buttons.add(reset);
        buttons.add(save);

        card.add(form, BorderLayout.CENTER);
        card.add(buttons, BorderLayout.SOUTH);
        return card;
    }

    private JPanel labeledField(String label, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);
        JLabel title = new JLabel(label);
        title.setFont(UITheme.F_SMALL_B);
        title.setForeground(UITheme.RUSSET);
        panel.add(title, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JTextField makeField(String value) {
        JTextField field = UITheme.inputField(20);
        field.setText(value == null ? "" : value);
        return field;
    }

    private JTextField readOnlyField(String value) {
        JTextField field = makeField(value);
        field.setEditable(false);
        return field;
    }

    private JButton ghostButton(String text) {
        JButton button = new JButton(text);
        button.setFont(UITheme.F_BODY_B);
        button.setForeground(UITheme.ESPRESSO);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.SANDSTONE, 1),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));
        return button;
    }

    private JLabel centerLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(font);
        label.setForeground(color);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private void saveProfile() {
        String emailError = DataValidator.validateEmail(emailField.getText().trim());
        String nicError = DataValidator.validateNic(nicField.getText().trim());
        String dobError = DataValidator.validateDob(dobField.getText().trim());
        String phone1Error = phone1Field.getText().isBlank() ? null : DataValidator.validatePhone(phone1Field.getText().trim());
        String phone2Error = phone2Field.getText().isBlank() ? null : DataValidator.validatePhone(phone2Field.getText().trim());

        String error = firstError(emailError, nicError, dobError, phone1Error, phone2Error);
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        student.setName(nameField.getText().trim());
        student.setEmail(emailField.getText().trim());
        student.setNic(nicField.getText().trim());
        student.setDob(Date.valueOf(dobField.getText().trim()));
        student.setHouseNo(houseNoField.getText().trim());
        student.setStreet(streetField.getText().trim());
        student.setCity(cityField.getText().trim());

        List<String> phones = new ArrayList<>();
        if (!phone1Field.getText().isBlank()) phones.add(phone1Field.getText().trim());
        if (!phone2Field.getText().isBlank()) phones.add(phone2Field.getText().trim());

        boolean saved = svc.updateProfile(student, phones);
        if (saved) {
            JOptionPane.showMessageDialog(this, "Profile updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            if (onProfileSaved != null) onProfileSaved.run();
            rebuild();
        } else {
            JOptionPane.showMessageDialog(this, "Could not save profile.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String firstError(String... errors) {
        for (String error : errors) {
            if (error != null) return error;
        }
        return null;
    }

    private void rebuild() {
        removeAll();
        build();
        revalidate();
        repaint();
    }
}
