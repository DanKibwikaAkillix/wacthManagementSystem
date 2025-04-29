import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Date;

public class CreateEventDialog extends JDialog {
    private JTextField organizerField, titleField, locationField;
    private JTextArea descriptionArea;
    private JSpinner dateSpinner;
    private JButton saveButton, cancelButton;
    private int eventId = 0;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/neighborhood_watch";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    public CreateEventDialog(JFrame parent) {
        this(parent, 0); // Create new event
    }

    public CreateEventDialog(JFrame parent, int eventId) {
        super(parent, eventId == 0 ? "Create Event" : "Update Event", true);
        this.eventId = eventId;
        initializeUI();
        if (eventId != 0) {
            loadEventData();
        }
    }

    private void initializeUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        // Organizer
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

        saveButton.addActionListener(e -> saveEvent());
        cancelButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);

        pack();
        setLocationRelativeTo(getParent());
    }

    private void loadEventData() {
        String sql = "SELECT organizerName, title, description, eventDate, location FROM events WHERE eventId = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                organizerField.setText(rs.getString("organizerName"));
                titleField.setText(rs.getString("title"));
                descriptionArea.setText(rs.getString("description"));
                dateSpinner.setValue(rs.getTimestamp("eventDate"));
                locationField.setText(rs.getString("location"));
            }
        } catch (SQLException e) {
            showError("Error loading event: " + e.getMessage());
        }
    }

    private void saveEvent() {
        String organizer = organizerField.getText().trim();
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        String location = locationField.getText().trim();
        Timestamp eventDate = new Timestamp(((Date) dateSpinner.getValue()).getTime());

        if (organizer.isEmpty() || title.isEmpty() || description.isEmpty() || location.isEmpty()) {
            showError("All fields must be filled!");
            return;
        }

        String sql;
        boolean isUpdate = (eventId != 0);

        if (isUpdate) {
            sql = "UPDATE events SET organizerName=?, title=?, description=?, eventDate=?, location=? WHERE eventId=?";
        } else {
            sql = "INSERT INTO events (organizerName, title, description, eventDate, location) VALUES (?, ?, ?, ?, ?)";
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, organizer);
            ps.setString(2, title);
            ps.setString(3, description);
            ps.setTimestamp(4, eventDate);
            ps.setString(5, location);

            if (isUpdate) {
                ps.setInt(6, eventId);
            }

            int affected = ps.executeUpdate();
            if (affected > 0) {
                JOptionPane.showMessageDialog(this, isUpdate ? "Event updated!" : "Event created!");
                dispose();

                if (getParent() instanceof AdminDashboard) {
                    ((AdminDashboard) getParent()).loadEvents();
                }
            } else {
                showError("No rows affected. Please check your data.");
            }

        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
