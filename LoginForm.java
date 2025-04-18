import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginForm extends JFrame {
    public LoginForm() {
        setTitle("User Login");
        setSize(350, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField(20);

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(20);

        JButton loginButton = new JButton("Login");
        JButton goToRegister = new JButton("Go to Register");

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(loginButton);
        panel.add(goToRegister);

        add(panel);

        loginButton.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            User user = UserDAO.loginUser(email, password);
            if (user != null) {
                JOptionPane.showMessageDialog(this, "Login successful!");
                dispose();
                
                switch (user.getRole().toLowerCase()) {
                    case "admin":
                        new AdminDashboard(user).setVisible(true);
                        break;
                    case "organizer":
                        new OrganizerDashboard(user).setVisible(true);
                        break;
                    case "user":
                        new UserDashboard(user).setVisible(true);
                        break;
                    default:
                        JOptionPane.showMessageDialog(this, "Unknown user role: " + user.getRole());
                }
                
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials.");
            }
        });

        goToRegister.addActionListener(e -> {
            dispose();
            new RegisterForm().setVisible(true);
        });
    }
}
