import javax.swing.*;

public class
Lecturer_dashboard {
    private JButton enterMarksButton;
    private JButton logOutButton;
    private JButton gradesGPAButton;
    private JButton CAEligibilityButton1;

    public Lecturer_dashboard() {
        JFrame frame = new JFrame("Lecturer Dashboard");
        frame.setSize(400, 300);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        enterMarksButton = new JButton("Marks");
        gradesGPAButton = new JButton("GPA");
        CAEligibilityButton1 = new JButton("CA");
        logOutButton = new JButton("Logout");

        enterMarksButton.setBounds(50, 50, 120, 30);
        gradesGPAButton.setBounds(50, 90, 120, 30);
        CAEligibilityButton1.setBounds(50, 130, 120, 30);
        logOutButton.setBounds(50, 170, 120, 30);

        frame.add(enterMarksButton);
        frame.add(gradesGPAButton);
        frame.add(CAEligibilityButton1);
        frame.add(logOutButton);

        enterMarksButton.addActionListener(e -> new Mark_entry_form());

        gradesGPAButton.addActionListener(e -> new Gpacalulation());

        CAEligibilityButton1.addActionListener(e -> new caeligible());

        logOutButton.addActionListener(e ->
                JOptionPane.showMessageDialog(null, "Logout!")
        );

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new Lecturer_dashboard();
    }


}
