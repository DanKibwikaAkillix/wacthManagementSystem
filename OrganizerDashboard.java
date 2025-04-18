import javax.swing.*;
import java.awt.*;

public class OrganizerDashboard extends JFrame {
    public OrganizerDashboard(User user) {
        setTitle("Organizer Dashboard - " + user.getUsername());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        JLabel label = new JLabel("Welcome Organizer: " + user.getUsername(), JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 28));
        label.setForeground(Color.BLACK);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setForeground(Color.WHITE);       
        logoutButton.setBackground(Color.RED);         
        logoutButton.setOpaque(true);
        logoutButton.setBorderPainted(false);
        logoutButton.setFocusPainted(false);
        logoutButton.setFont(new Font("Arial", Font.BOLD, 14));
        
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginForm().setVisible(true);
        });

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setBackground(Color.WHITE);
        topPanel.add(logoutButton);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(label, BorderLayout.CENTER);

        add(mainPanel);
    }
}
