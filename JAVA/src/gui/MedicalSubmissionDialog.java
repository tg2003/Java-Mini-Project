package gui;

import models.Course;
import models.Medical;
import models.Undergraduate;
import service.CourseService;
import service.MedicalService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class MedicalSubmissionDialog extends JDialog {

    private final Undergraduate student;
    private final MedicalService medicalService = new MedicalService();
    private final CourseService courseService = new CourseService();
    private final Runnable onSaved;
    private final Medical editingMedical;

    private JTextField fromDateField;
    private JTextField toDateField;
    private JComboBox<String> sessionTypeCombo;
    private JComboBox<String> courseCombo;
    private JLabel uploadLabel;
    private File selectedCertificate;

    public MedicalSubmissionDialog(Window owner, Undergraduate student, Medical editingMedical, Runnable onSaved) {
        super(owner, editingMedical == null ? "Submit Medical" : "Edit Pending Medical", ModalityType.APPLICATION_MODAL);
        this.student = student;
        this.editingMedical = editingMedical;
        this.onSaved = onSaved;

        setSize(620, 520);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setContentPane(buildContent());
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(UITheme.ALMOND);
        root.setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel header = new JLabel(editingMedical == null ? "Submit Medicals" : "Edit Pending Medical");
        header.setFont(UITheme.F_HEADING);
        header.setForeground(UITheme.ESPRESSO);
        root.add(header, BorderLayout.NORTH);

        JPanel formCard = new JPanel(new BorderLayout(0, 18)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(UITheme.CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.dispose();
            }
        };
        formCard.setOpaque(false);
        formCard.setBorder(new EmptyBorder(22, 22, 22, 22));

        JPanel form = new JPanel(new GridLayout(0, 2, 16, 14));
        form.setOpaque(false);

        fromDateField = UITheme.inputField(18);
        toDateField = UITheme.inputField(18);
        sessionTypeCombo = new JComboBox<>(new String[]{"Lecture", "Exam"});
        sessionTypeCombo.setFont(UITheme.F_BODY);
        courseCombo = new JComboBox<>();
        courseCombo.setFont(UITheme.F_BODY);
        courseCombo.addItem("Select Course");
        List<Course> courses = courseService.getCourses(student.getUgId());
        for (Course course : courses) {
            courseCombo.addItem(course.getCode() + " - " + course.getName());
        }

        uploadLabel = new JLabel("No certificate selected");
        uploadLabel.setFont(UITheme.F_SMALL);
        uploadLabel.setForeground(UITheme.PUTTY);

        form.add(labeled("From Date (yyyy-mm-dd)", fromDateField));
        form.add(labeled("To Date (yyyy-mm-dd)", toDateField));
        form.add(labeled("Session Type", sessionTypeCombo));
        form.add(labeled("Course", courseCombo));

        JPanel uploadPanel = new JPanel(new BorderLayout(0, 8));
        uploadPanel.setOpaque(false);
        JButton uploadButton = UITheme.primaryButton("Upload Medical Certificate");
        uploadButton.addActionListener(e -> chooseFile());
        uploadPanel.add(uploadButton, BorderLayout.NORTH);
        uploadPanel.add(uploadLabel, BorderLayout.CENTER);

        formCard.add(form, BorderLayout.NORTH);
        formCard.add(uploadPanel, BorderLayout.CENTER);

        JTextArea note = new JTextArea(
                "The medical certificate will be copied to the local medical submissions folder.\n" +
                        "Submit the original medical certificate to the Dean's Office with the REF No and Student ID."
        );
        note.setEditable(false);
        note.setLineWrap(true);
        note.setWrapStyleWord(true);
        note.setBackground(UITheme.CARD_BG);
        note.setFont(UITheme.F_SMALL);
        note.setForeground(UITheme.RUSSET);
        formCard.add(note, BorderLayout.SOUTH);

        if (editingMedical != null) {
            fromDateField.setText(editingMedical.getFromDate().toString());
            toDateField.setText(editingMedical.getToDate().toString());
            sessionTypeCombo.setSelectedItem(editingMedical.getSessionType());
            if (!"-".equals(editingMedical.getCCode())) {
                for (int i = 1; i < courseCombo.getItemCount(); i++) {
                    if (courseCombo.getItemAt(i).startsWith(editingMedical.getCCode() + " ")) {
                        courseCombo.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> dispose());
        JButton save = UITheme.primaryButton(editingMedical == null ? "Submit Medical" : "Save Changes");
        save.addActionListener(e -> saveMedical());
        actions.add(cancel);
        actions.add(save);

        root.add(formCard, BorderLayout.CENTER);
        root.add(actions, BorderLayout.SOUTH);
        return root;
    }

    private JPanel labeled(String title, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);
        JLabel label = new JLabel(title);
        label.setFont(UITheme.F_SMALL_B);
        label.setForeground(UITheme.RUSSET);
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private void chooseFile() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedCertificate = chooser.getSelectedFile();
            uploadLabel.setText(selectedCertificate.getName());
        }
    }

    private void saveMedical() {
        Date fromDate;
        Date toDate;
        try {
            fromDate = Date.valueOf(fromDateField.getText().trim());
            toDate = Date.valueOf(toDateField.getText().trim());
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Date format must be yyyy-mm-dd.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (toDate.before(fromDate)) {
            JOptionPane.showMessageDialog(this, "To Date must be on or after From Date.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedCertificate == null && editingMedical == null) {
            JOptionPane.showMessageDialog(this, "Please upload a medical certificate.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String selectedCourse = courseCombo.getSelectedIndex() <= 0 ? null : courseCombo.getSelectedItem().toString().split(" - ")[0];
        String sessionType = sessionTypeCombo.getSelectedItem().toString();

        int refId;
        if (editingMedical == null) {
            refId = medicalService.submitMedical(student.getUgId(), fromDate, toDate, sessionType, selectedCourse);
            if (refId <= 0) {
                JOptionPane.showMessageDialog(this, "Medical submission failed.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            boolean updated = medicalService.updatePendingMedical(editingMedical.getMedicalId(), fromDate, toDate, sessionType, selectedCourse);
            if (!updated) {
                JOptionPane.showMessageDialog(this, "Only pending medicals can be edited.", "Update Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
            refId = editingMedical.getMedicalId();
        }

        if (selectedCertificate != null) {
            copyCertificate(refId);
        }

        JOptionPane.showMessageDialog(this,
                (editingMedical == null ? "Medical submitted successfully.\n" : "Medical updated successfully.\n") +
                        "REF No: " + String.format("REF-%05d", refId),
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        if (onSaved != null) onSaved.run();
        dispose();
    }

    private void copyCertificate(int refId) {
        try {
            Path targetDir = Path.of("src", "resources", "medicalSubmissions");
            Files.createDirectories(targetDir);
            String safeName = selectedCertificate.getName().replaceAll("[^A-Za-z0-9._-]", "_");
            String fileName = String.format("REF-%05d_%s_%s", refId, LocalDate.now(), safeName);
            Files.copy(selectedCertificate.toPath(), targetDir.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Medical saved, but the certificate file could not be copied locally.",
                    "Upload Warning",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
}
