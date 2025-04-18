import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class Dashboard extends JFrame {

    public Dashboard(User user) {
        setTitle("Dashboard - " + user.getUsername() + " (" + user.getRole() + ")");
        setExtendedState(JFrame.MAXIMIZED_BOTH); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Top panel with logout button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(245, 245, 245));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Neighborhood Watch Dashboard", JLabel.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        topPanel.add(title, BorderLayout.WEST);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(Color.RED);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        logoutButton.setPreferredSize(new Dimension(120, 40));
        topPanel.add(logoutButton, BorderLayout.EAST);

        // Center welcome message
        JLabel welcomeLabel = new JLabel("Welcome, " + user.getUsername() + "! Role: " + user.getRole(), JLabel.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 30));
        welcomeLabel.setForeground(new Color(33, 37, 41)); 

        add(topPanel, BorderLayout.NORTH);
        add(welcomeLabel, BorderLayout.CENTER);

        // Logout Action
        logoutButton.addActionListener((ActionEvent e) -> {
            dispose(); 
            new LoginForm().setVisible(true); 
        });
    }
}
