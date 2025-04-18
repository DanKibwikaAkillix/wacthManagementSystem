import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RegisterForm extends JFrame {
    public RegisterForm() {
        setTitle("User Registration");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(20);

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField(20);

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(20);

        JLabel roleLabel = new JLabel("Role:");
        String[] roles = {"user", "admin"};
        JComboBox<String> roleBox = new JComboBox<>(roles);

        JButton registerButton = new JButton("Register");
        JButton goToLogin = new JButton("Go to Login");

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(roleLabel);
        panel.add(roleBox);
        panel.add(registerButton);
        panel.add(goToLogin);

        add(panel);

        registerButton.addActionListener(e -> {
            String username = usernameField.getText();
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            String role = roleBox.getSelectedItem().toString();

            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(password);
            user.setRole(role);

            if (UserDAO.registerUser(user)) {
                JOptionPane.showMessageDialog(this, "Registration successful!");
                dispose();
                new LoginForm().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed!");
            }
        });

        goToLogin.addActionListener(e -> {
            dispose();
            new LoginForm().setVisible(true);
        });
    }
}
