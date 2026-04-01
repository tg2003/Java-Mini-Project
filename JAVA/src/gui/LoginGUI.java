package gui;
import javax.swing.*;
import models.Login;

public class LoginGUI extends JFrame{
    private JLabel loginLabel;
    private JLabel passwordLabel;
    private JLabel usernameLabel;
    private JTextField usernameTextField;
    private JButton loginButton;
    private JPanel loginPanel;
    private JPasswordField passwordField;

    public LoginGUI() {
        setContentPane(loginPanel);
        setTitle("LMS Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400,300);
        setLocationRelativeTo(null);


        //login button e
        loginButton.addActionListener(e -> {
            String id = usernameTextField.getText();
            String pwd = passwordField.getText();
            String role = Login.login(id,pwd); //send user enterd - id,pwd to db & check

            if (role==null){
                    JOptionPane.showMessageDialog(this,"Invalid Credintials !","Error",JOptionPane.ERROR_MESSAGE);
            }
            else{
                JOptionPane.showMessageDialog(this,"Welcome "+role);
                new AdminGUI().setVisible(true);
            }
        });
    }


}
