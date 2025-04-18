import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class CreateWatchGroupForm extends JFrame {
    private JTextField eventIdField;
    private JTextField organizerIdField;
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JTextField eventDateField; 
    private JTextField locationField;

    public CreateWatchGroupForm() {
        setTitle("Create New Watch Group");
        setSize(400, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));

        panel.add(new JLabel("Event ID:"));
        eventIdField = new JTextField();
        panel.add(eventIdField);

        panel.add(new JLabel("Organizer ID:"));
        organizerIdField = new JTextField();
        panel.add(organizerIdField);

        panel.add(new JLabel("Title:"));
        titleField = new JTextField();
        panel.add(titleField);

        panel.add(new JLabel("Description:"));
        descriptionArea = new JTextArea(3, 20);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        panel.add(scrollPane);

        panel.add(new JLabel("Event Date (YYYY-MM-DD HH:MM:SS):"));
        eventDateField = new JTextField();
        panel.add(eventDateField);

        panel.add(new JLabel("Location:"));
        locationField = new JTextField();
        panel.add(locationField);
        

        JButton createButton = new JButton("Create Watch Group");
        createButton.addActionListener(e -> createGroup());

        JButton createGroupButton = new JButton("Create Watch Group");
createGroupButton.addActionListener(e -> {
    new CreateWatchGroupForm().setVisible(true);
});

        add(panel, BorderLayout.CENTER);
        add(createButton, BorderLayout.SOUTH);
    }

    private void createGroup() {
        String eventId = eventIdField.getText();
        String organizerId = organizerIdField.getText();
        String title = titleField.getText();
        String description = descriptionArea.getText();
        String eventDate = eventDateField.getText();
        String location = locationField.getText();

        String dbURL = "jdbc:mysql://localhost:3306/neighborhood_watch";
        String dbUser = "root";
        String dbPass = "";

        String sql = "INSERT INTO watchgroups (eventId, organizerId, title, description, eventDate, location) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPass);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, Integer.parseInt(eventId));
            stmt.setInt(2, Integer.parseInt(organizerId));
            stmt.setString(3, title);
            stmt.setString(4, description);
            stmt.setTimestamp(5, Timestamp.valueOf(eventDate));
            stmt.setString(6, location);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Watch Group created successfully!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create Watch Group.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD HH:MM:SS", "Input Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CreateWatchGroupForm().setVisible(true));
    }
}
