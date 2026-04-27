package gui;

import models.Attendance;
import models.Medical;
import models.Undergraduate;
import service.AttendanceService;
import service.MedicalService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class MedicalSubmissionDialog extends JDialog {

    private final Undergraduate   student;
    private final MedicalService  medicalService  = new MedicalService();
    private final AttendanceService attendanceService = new AttendanceService();
    private final Runnable        onSaved;
    private final Medical         editingMedical;

    // Step 1 — date range
    private JTextField fromDateField;
    private JTextField toDateField;

    // Step 2 — absent session table
    private DefaultTableModel sessionTableModel;
    private JTable            sessionTable;
    private List<Attendance>  loadedSessions = new ArrayList<>();

    // Step 3 — certificate
    private JLabel uploadLabel;
    private File   selectedCertificate;

    // Card navigation
    private CardLayout cardLayout;
    private JPanel     cardPanel;

    public MedicalSubmissionDialog(Window owner, Undergraduate student,
                                   Medical editingMedical, Runnable onSaved) {
        super(owner,
                editingMedical == null ? "Submit Medical" : "Edit Pending Medical",
                ModalityType.APPLICATION_MODAL);
        this.student        = student;
        this.editingMedical = editingMedical;
        this.onSaved        = onSaved;

        setSize(720, 560);
        setMinimumSize(new Dimension(640, 480));
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        if (editingMedical != null) {
            // Edit mode — keep old simple form
            setContentPane(buildEditContent());
        } else {
            // New submission — 3-step wizard
            setContentPane(buildWizardContent());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // WIZARD (new submission): Step 1 → Step 2 → Step 3 → Submit
    // ─────────────────────────────────────────────────────────────────────────
    private JPanel buildWizardContent() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(UITheme.ALMOND);

        // Header
        JLabel header = new JLabel("Submit Medical  ·  Select Date Range");
        header.setFont(UITheme.F_HEADING);
        header.setForeground(UITheme.ESPRESSO);
        header.setBorder(new EmptyBorder(20, 24, 8, 24));
        root.add(header, BorderLayout.NORTH);

        // Cards
        cardLayout = new CardLayout();
        cardPanel  = new JPanel(cardLayout);
        cardPanel.setOpaque(false);
        cardPanel.setBorder(new EmptyBorder(0, 24, 0, 24));
        cardPanel.add(buildStep1Panel(), "Step1");
        cardPanel.add(buildStep2Panel(), "Step2");
        cardPanel.add(buildStep3Panel(), "Step3");
        root.add(cardPanel, BorderLayout.CENTER);

        cardLayout.show(cardPanel, "Step1");
        return root;
    }

    // ── Step 1: Date range picker ─────────────────────────────────────────────
    private JPanel buildStep1Panel() {
        JPanel card = card();

        JLabel info = new JLabel("<html><b>Step 1 of 3</b> — Choose your date range (maximum 14 days)</html>");
        info.setFont(UITheme.F_BODY);
        info.setForeground(UITheme.RUSSET);

        JPanel fields = new JPanel(new GridLayout(2, 2, 16, 12));
        fields.setOpaque(false);
        fields.setBorder(new EmptyBorder(16, 0, 16, 0));

        fromDateField = UITheme.inputField(12);
        fromDateField.setText(LocalDate.now().minusDays(13).toString());
        toDateField   = UITheme.inputField(12);
        toDateField.setText(LocalDate.now().toString());

        fields.add(label("From Date (yyyy-mm-dd)"));
        fields.add(label("To Date (yyyy-mm-dd)"));
        fields.add(fromDateField);
        fields.add(toDateField);

        JLabel hint = new JLabel("Only Absent sessions in this range will be shown.");
        hint.setFont(UITheme.F_SMALL);
        hint.setForeground(UITheme.PUTTY);

        JPanel actions = actions();
        JButton next = UITheme.primaryButton("Search Absent Sessions →");
        next.addActionListener(e -> goToStep2());
        JButton cancel = plainButton("Cancel");
        cancel.addActionListener(e -> dispose());
        actions.add(cancel);
        actions.add(next);

        card.add(info,    BorderLayout.NORTH);
        card.add(fields,  BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(hint,    BorderLayout.NORTH);
        bottom.add(actions, BorderLayout.SOUTH);
        card.add(bottom, BorderLayout.SOUTH);
        return card;
    }

    private void goToStep2() {
        LocalDate from, to;
        try {
            from = LocalDate.parse(fromDateField.getText().trim());
            to   = LocalDate.parse(toDateField.getText().trim());
        } catch (DateTimeParseException ex) {
            error("Date format must be yyyy-mm-dd  (e.g. 2025-03-15)");
            return;
        }
        if (to.isBefore(from)) {
            error("'To Date' must be on or after 'From Date'.");
            return;
        }
        long span = ChronoUnit.DAYS.between(from, to);
        if (span > 13) {
            error("Date range cannot exceed 14 days.\nYour range spans " + (span + 1) + " days.");
            return;
        }

        // Query absent sessions
        loadedSessions = attendanceService.getAbsentSessionsInRange(
                student.getUgId(), from, to);

        // Populate table
        sessionTableModel.setRowCount(0);
        for (Attendance a : loadedSessions) {
            sessionTableModel.addRow(new Object[]{
                    false,
                    a.getDate().toString(),
                    a.getCCode(),
                    a.getCName(),
                    a.getSessionType(),
                    a.getTimeSlot()
            });
        }

        if (loadedSessions.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No absent sessions found in this date range,\n" +
                            "or all absences already have a medical submitted.",
                    "No Sessions Found", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        cardLayout.show(cardPanel, "Step2");
    }

    // ── Step 2: Checkbox table of absent sessions ─────────────────────────────
    private JPanel buildStep2Panel() {
        JPanel card = card();

        JLabel info = new JLabel("<html><b>Step 2 of 3</b> — Select the sessions you want to submit a medical for</html>");
        info.setFont(UITheme.F_BODY);
        info.setForeground(UITheme.RUSSET);
        info.setBorder(new EmptyBorder(0, 0, 12, 0));

        // Table with checkbox column
        String[] cols = {"✓", "Date", "Course Code", "Course Name", "Type", "Time"};
        sessionTableModel = new DefaultTableModel(cols, 0) {
            @Override public Class<?> getColumnClass(int col) {
                return col == 0 ? Boolean.class : String.class;
            }
            @Override public boolean isCellEditable(int row, int col) {
                return col == 0;
            }
        };
        sessionTable = new JTable(sessionTableModel);
        sessionTable.setFont(UITheme.F_BODY);
        sessionTable.setRowHeight(28);
        sessionTable.getTableHeader().setFont(UITheme.F_SMALL_B);
        sessionTable.getColumnModel().getColumn(0).setMaxWidth(36);
        sessionTable.setBackground(UITheme.CARD_BG);
        sessionTable.setGridColor(UITheme.ALMOND);

        JScrollPane scroll = new JScrollPane(sessionTable);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.ALMOND));

        JPanel selectRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        selectRow.setOpaque(false);
        JButton selectAll = plainButton("Select All");
        selectAll.addActionListener(e -> setAllChecked(true));
        JButton clearAll = plainButton("Clear All");
        clearAll.addActionListener(e -> setAllChecked(false));
        selectRow.add(selectAll);
        selectRow.add(clearAll);

        JPanel actions = actions();
        JButton back = plainButton("← Back");
        back.addActionListener(e -> cardLayout.show(cardPanel, "Step1"));
        JButton next = UITheme.primaryButton("Upload Certificate →");
        next.addActionListener(e -> goToStep3());
        JButton cancel = plainButton("Cancel");
        cancel.addActionListener(e -> dispose());
        actions.add(cancel);
        actions.add(back);
        actions.add(next);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(info,      BorderLayout.NORTH);
        top.add(selectRow, BorderLayout.SOUTH);

        card.add(top,    BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        card.add(actions, BorderLayout.SOUTH);
        return card;
    }

    private void setAllChecked(boolean checked) {
        for (int i = 0; i < sessionTableModel.getRowCount(); i++)
            sessionTableModel.setValueAt(checked, i, 0);
    }

    private void goToStep3() {
        if (getCheckedSessions().isEmpty()) {
            error("Please select at least one session.");
            return;
        }
        cardLayout.show(cardPanel, "Step3");
    }

    private List<Attendance> getCheckedSessions() {
        List<Attendance> checked = new ArrayList<>();
        for (int i = 0; i < sessionTableModel.getRowCount(); i++) {
            if (Boolean.TRUE.equals(sessionTableModel.getValueAt(i, 0)))
                checked.add(loadedSessions.get(i));
        }
        return checked;
    }

    // ── Step 3: Certificate upload + submit ───────────────────────────────────
    private JPanel buildStep3Panel() {
        JPanel card = card();

        JLabel info = new JLabel("<html><b>Step 3 of 3</b> — Upload your medical certificate and submit</html>");
        info.setFont(UITheme.F_BODY);
        info.setForeground(UITheme.RUSSET);
        info.setBorder(new EmptyBorder(0, 0, 20, 0));

        uploadLabel = new JLabel("No certificate selected");
        uploadLabel.setFont(UITheme.F_SMALL);
        uploadLabel.setForeground(UITheme.PUTTY);

        JButton uploadBtn = UITheme.primaryButton("Browse Certificate (PDF / Image)");
        uploadBtn.addActionListener(e -> chooseFile());

        JTextArea note = new JTextArea(
                "After submitting:\n" +
                        "• A REF No will be generated for each selected session.\n" +
                        "• Submit the original certificate to the Dean's Office with your Student ID and the REF No(s).\n" +
                        "• Submitted medicals will appear as Pending in View Medicals."
        );
        note.setEditable(false);
        note.setLineWrap(true);
        note.setWrapStyleWord(true);
        note.setBackground(UITheme.CARD_BG);
        note.setFont(UITheme.F_SMALL);
        note.setForeground(UITheme.RUSSET);
        note.setBorder(new EmptyBorder(14, 0, 0, 0));

        JPanel uploadPanel = new JPanel(new BorderLayout(0, 8));
        uploadPanel.setOpaque(false);
        uploadPanel.add(uploadBtn,   BorderLayout.NORTH);
        uploadPanel.add(uploadLabel, BorderLayout.CENTER);
        uploadPanel.add(note,        BorderLayout.SOUTH);

        JPanel actions = actions();
        JButton back = plainButton("← Back");
        back.addActionListener(e -> cardLayout.show(cardPanel, "Step2"));
        JButton submit = UITheme.primaryButton("Submit Medical");
        submit.addActionListener(e -> doSubmit());
        JButton cancel = plainButton("Cancel");
        cancel.addActionListener(e -> dispose());
        actions.add(cancel);
        actions.add(back);
        actions.add(submit);

        card.add(info,        BorderLayout.NORTH);
        card.add(uploadPanel, BorderLayout.CENTER);
        card.add(actions,     BorderLayout.SOUTH);
        return card;
    }

    private void doSubmit() {
        if (selectedCertificate == null) {
            error("Please upload a medical certificate before submitting.");
            return;
        }

        List<Attendance> checked = getCheckedSessions();
        if (checked.isEmpty()) {
            error("No sessions selected. Go back and select at least one.");
            return;
        }

        // Insert one MEDICAL row per selected session
        List<Integer> refs = medicalService.submitMedicalForSessions(student.getUgId(), checked);

        if (refs.isEmpty()) {
            error("Medical submission failed. Please try again.");
            return;
        }

        // Copy certificate once, named after first REF
        copyCertificate(refs.get(0));

        // Build summary message
        StringBuilder sb = new StringBuilder();
        sb.append("Medical submitted successfully!\n\n");
        sb.append("Sessions submitted: ").append(refs.size()).append("\n");
        sb.append("REF Numbers:\n");
        for (int ref : refs) sb.append("  REF-").append(String.format("%05d", ref)).append("\n");
        sb.append("\nSubmit your certificate to the Dean's Office\nwith the above REF number(s) and your Student ID.");

        JOptionPane.showMessageDialog(this, sb.toString(), "Success", JOptionPane.INFORMATION_MESSAGE);
        if (onSaved != null) onSaved.run();
        dispose();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // EDIT MODE (existing pending medical)
    // ─────────────────────────────────────────────────────────────────────────
    private JPanel buildEditContent() {
        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(UITheme.ALMOND);
        root.setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel header = new JLabel("Edit Pending Medical");
        header.setFont(UITheme.F_HEADING);
        header.setForeground(UITheme.ESPRESSO);
        root.add(header, BorderLayout.NORTH);

        JPanel formCard = card();

        JPanel form = new JPanel(new GridLayout(0, 2, 16, 14));
        form.setOpaque(false);

        fromDateField = UITheme.inputField(18);
        toDateField   = UITheme.inputField(18);
        JComboBox<String> sessionTypeCombo = new JComboBox<>(new String[]{"Lecture", "Practical", "Exam"});
        sessionTypeCombo.setFont(UITheme.F_BODY);

        uploadLabel = new JLabel("No new certificate selected");
        uploadLabel.setFont(UITheme.F_SMALL);
        uploadLabel.setForeground(UITheme.PUTTY);

        // Pre-fill
        fromDateField.setText(editingMedical.getFromDate().toString());
        toDateField.setText(editingMedical.getToDate().toString());
        sessionTypeCombo.setSelectedItem(editingMedical.getSessionType());

        form.add(labeled("From Date (yyyy-mm-dd)", fromDateField));
        form.add(labeled("To Date (yyyy-mm-dd)",   toDateField));
        form.add(labeled("Session Type",           sessionTypeCombo));
        form.add(new JPanel()); // spacer

        JPanel uploadPanel = new JPanel(new BorderLayout(0, 8));
        uploadPanel.setOpaque(false);
        JButton uploadBtn = UITheme.primaryButton("Replace Certificate (optional)");
        uploadBtn.addActionListener(e -> chooseFile());
        uploadPanel.add(uploadBtn,   BorderLayout.NORTH);
        uploadPanel.add(uploadLabel, BorderLayout.CENTER);

        formCard.add(form,        BorderLayout.NORTH);
        formCard.add(uploadPanel, BorderLayout.CENTER);

        JPanel actions = actions();
        JButton cancel = plainButton("Cancel");
        cancel.addActionListener(e -> dispose());
        JButton save = UITheme.primaryButton("Save Changes");
        save.addActionListener(e -> {
            Date fromDate, toDate;
            try {
                fromDate = Date.valueOf(fromDateField.getText().trim());
                toDate   = Date.valueOf(toDateField.getText().trim());
            } catch (IllegalArgumentException ex) {
                error("Date format must be yyyy-mm-dd.");
                return;
            }
            boolean ok = medicalService.updatePendingMedical(
                    editingMedical.getMedicalId(), fromDate, toDate,
                    sessionTypeCombo.getSelectedItem().toString(), editingMedical.getCCode());
            if (!ok) { error("Update failed — only Pending medicals can be edited."); return; }
            if (selectedCertificate != null) copyCertificate(editingMedical.getMedicalId());
            JOptionPane.showMessageDialog(this, "Medical updated.\nREF: " +
                    editingMedical.getReferenceNo(), "Updated", JOptionPane.INFORMATION_MESSAGE);
            if (onSaved != null) onSaved.run();
            dispose();
        });
        actions.add(cancel);
        actions.add(save);

        root.add(formCard, BorderLayout.CENTER);
        root.add(actions,  BorderLayout.SOUTH);
        return root;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Shared helpers
    // ─────────────────────────────────────────────────────────────────────────
    private void chooseFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Medical Certificate");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "PDF / Images", "pdf", "jpg", "jpeg", "png"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedCertificate = chooser.getSelectedFile();
            uploadLabel.setText("✓  " + selectedCertificate.getName());
            uploadLabel.setForeground(new Color(0x2E7D32));
        }
    }

    private void copyCertificate(int refId) {
        try {
            Path targetDir = Path.of("src", "resources", "medicalSubmissions");
            Files.createDirectories(targetDir);
            String safeName = selectedCertificate.getName().replaceAll("[^A-Za-z0-9._-]", "_");
            String fileName = String.format("REF-%05d_%s_%s",
                    refId, LocalDate.now(), safeName);
            Files.copy(selectedCertificate.toPath(),
                    targetDir.resolve(fileName),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Medical saved, but the certificate file could not be copied.",
                    "Upload Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Validation Error", JOptionPane.WARNING_MESSAGE);
    }

    // ── Layout helpers ────────────────────────────────────────────────────────
    private JPanel card() {
        JPanel card = new JPanel(new BorderLayout(0, 12)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(UITheme.CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        return card;
    }

    private JPanel actions() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        p.setOpaque(false);
        return p;
    }

    private JButton plainButton(String text) {
        JButton b = new JButton(text);
        b.setFont(UITheme.F_BODY);
        b.setFocusPainted(false);
        return b;
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.F_SMALL_B);
        l.setForeground(UITheme.RUSSET);
        return l;
    }

    private JPanel labeled(String title, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setOpaque(false);
        p.add(label(title), BorderLayout.NORTH);
        p.add(field,        BorderLayout.CENTER);
        return p;
    }
}