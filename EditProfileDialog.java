import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class EditProfileDialog extends JDialog {
    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private User user;

    public EditProfileDialog(JFrame parent, User user) {
        super(parent, "Edit Profile", true);
        this.user = user;

        setLayout(new GridLayout(4, 2, 10, 10));
        setSize(300, 200);
        setLocationRelativeTo(parent);

        add(new JLabel("Username:"));
        usernameField = new JTextField(user.getUsername());
        add(usernameField);

        add(new JLabel("Email:"));
        emailField = new JTextField(user.getEmail());
        add(emailField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField(user.getPassword());
        add(passwordField);

        JButton saveButton = new JButton("Save Changes");
        saveButton.addActionListener(this::saveChanges);
        add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        add(cancelButton);
    }

    private void saveChanges(ActionEvent e) {
        String newUsername = usernameField.getText();
        String newEmail = emailField.getText();
        String newPassword = new String(passwordField.getPassword());

        // Perform validation checks before updating
        if (newUsername.isEmpty() || newEmail.isEmpty() || newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled out!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        updateUserProfile(newUsername, newEmail, newPassword);
    }

    private void updateUserProfile(String username, String email, String password) {
        final String DB_URL = "jdbc:mysql://localhost:3306/neighborhood_watch";
        final String USER = "root";
        final String PASS = "";

        String sql = "UPDATE users SET username = ?, email = ?, password = ? WHERE userId = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, password); // Consider hashing the password before storing
            ps.setInt(4, user.getUserId());

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Profile updated successfully!");
                user.setUsername(username);
                user.setEmail(email);
                user.setPassword(password);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Error updating profile.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating profile: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
