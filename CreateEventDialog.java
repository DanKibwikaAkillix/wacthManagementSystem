import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Date;

public class CreateEventDialog extends JDialog {
    private JTextField organizerField, titleField, locationField;
    private JTextArea descriptionArea;
    private JSpinner dateSpinner;
    private JButton saveButton, cancelButton;
    private int eventId = 0;

    public CreateEventDialog(JFrame parent) {
        super(parent, "Create Event", true);
        setupUI();
    }

    public CreateEventDialog(JFrame parent, int eventId) {
        super(parent, "Update Event", true);
        this.eventId = eventId;
        setupUI();
        loadEventData();
    }

    private void setupUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        // Organizer Name
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("Organizer Name:"), gbc);
        organizerField = new JTextField(20);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        add(organizerField, gbc);

        // Title
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("Title:"), gbc);
        titleField = new JTextField(20);
        gbc.gridx = 1;
        add(titleField, gbc);

        // Description
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.NORTHEAST;
        add(new JLabel("Description:"), gbc);
        descriptionArea = new JTextArea(4, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        gbc.gridx = 1;
        add(new JScrollPane(descriptionArea), gbc);

        // Date
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("Date:"), gbc);
        dateSpinner = new JSpinner(new SpinnerDateModel());
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd HH:mm:ss"));
        gbc.gridx = 1;
        add(dateSpinner, gbc);

        // Location
        gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("Location:"), gbc);
        locationField = new JTextField(20);
        gbc.gridx = 1;
        add(locationField, gbc);

        // Buttons
        saveButton = new JButton(eventId == 0 ? "Create" : "Update");
        cancelButton = new JButton("Cancel");
        styleButton(saveButton, new Color(0, 123, 255), Color.WHITE);
        styleButton(cancelButton, new Color(108, 117, 125), Color.WHITE);
        JPanel btnPanel = new JPanel();
        btnPanel.add(saveButton);
        btnPanel.add(cancelButton);
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        add(btnPanel, gbc);

        saveButton.addActionListener(e -> saveEvent());
        cancelButton.addActionListener(e -> dispose());

        pack();
        setLocationRelativeTo(getParent());
    }

    private void styleButton(JButton button, Color bg, Color fg) {
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void loadEventData() {
        final String URL = "jdbc:mysql://localhost:3306/neighborhood_watch";
        final String USER = "root";
        final String PASSWORD = "";
        String sql = "SELECT organizerName, title, description, eventDate, location FROM events WHERE eventId = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    organizerField.setText(rs.getString("organizerName"));
                    titleField.setText(rs.getString("title"));
                    descriptionArea.setText(rs.getString("description"));
                    dateSpinner.setValue(rs.getTimestamp("eventDate"));
                    locationField.setText(rs.getString("location"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading event: " + e.getMessage(),
                                          "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveEvent() {
        final String URL = "jdbc:mysql://localhost:3306/neighborhood_watch";
        final String USER = "root";
        final String PASSWORD = "";

        String organizer = organizerField.getText().trim();
        String title = titleField.getText().trim();
        String desc = descriptionArea.getText().trim();
        Timestamp ts = new Timestamp(((Date) dateSpinner.getValue()).getTime());
        String loc = locationField.getText().trim();

        String sql;
        if (eventId == 0) {
            sql = "INSERT INTO events (organizerName, title, description, eventDate, location) VALUES (?, ?, ?, ?, ?)";
        } else {
            sql = "UPDATE events SET organizerName = ?, title = ?, description = ?, eventDate = ?, location = ? WHERE eventId = ?";
        }

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, organizer);
            ps.setString(2, title);
            ps.setString(3, desc);
            ps.setTimestamp(4, ts);
            ps.setString(5, loc);
            if (eventId != 0) ps.setInt(6, eventId);

            ps.executeUpdate();
            dispose();
            if (getParent() instanceof AdminDashboard) {
                ((AdminDashboard) getParent()).loadEvents();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving event: " + e.getMessage(),
                                          "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
