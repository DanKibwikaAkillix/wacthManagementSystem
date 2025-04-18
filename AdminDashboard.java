import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.EventObject;

public class AdminDashboard extends JFrame {

    private JTable eventsTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;

    public AdminDashboard(User user) {
        setTitle("Admin Dashboard - " + user.getUsername());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // --- Top Panel ---
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.setBackground(Color.WHITE);

        JLabel welcomeLabel = new JLabel("Welcome back: " + user.getUsername(), JLabel.LEFT);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.BLACK);

        JButton btnManageGroups = new JButton("Manage Watch Groups");
        styleButton(btnManageGroups, new Color(0, 123, 255), Color.WHITE);
        btnManageGroups.addActionListener(e -> new WatchGroupManager().setVisible(true));

        JButton editProfileButton = new JButton("Edit My Profile");
        styleButton(editProfileButton, new Color(0, 123, 255), Color.WHITE);
        editProfileButton.addActionListener(e -> new EditProfileDialog(this, user).setVisible(true));

        JButton logoutButton = new JButton("Logout");
        styleButton(logoutButton, new Color(220, 53, 69), Color.WHITE);
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginForm().setVisible(true);
        });

        topPanel.add(welcomeLabel);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(btnManageGroups);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(editProfileButton);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(logoutButton);

        // --- Filter Panel ---
        JTextField filterField = new JTextField(20);
        filterField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        filterField.setToolTipText("Search by event title...");

        JLabel filterLabel = new JLabel("Filter by Title:");
        filterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        filterPanel.add(filterLabel);
        filterPanel.add(filterField);

        // --- Buttons for Creating Events and Watch Groups ---
        JButton createEventButton = new JButton("Create Event");
        styleButton(createEventButton, new Color(0, 123, 255), Color.WHITE);
        createEventButton.addActionListener(e -> new CreateEventDialog(this).setVisible(true));

        JButton createGroupButton = new JButton("Create Watch Group");
        styleButton(createGroupButton, new Color(108, 117, 125), Color.WHITE);
        createGroupButton.addActionListener(e -> new CreateWatchGroupForm().setVisible(true));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        buttonPanel.add(createEventButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(createGroupButton);

        // --- Table Setup ---
        String[] columns = { "Event ID", "Title", "Description", "Date", "Location", "Actions" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 5;
            }
        };

        eventsTable = new JTable(tableModel);
        eventsTable.setRowHeight(45);
        eventsTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        eventsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        eventsTable.getColumn("Actions").setCellRenderer(new PanelRenderer());
        eventsTable.getColumn("Actions").setCellEditor(new PanelEditor());

        rowSorter = new TableRowSorter<>(tableModel);
        eventsTable.setRowSorter(rowSorter);

        filterField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                filterTable(filterField.getText());
            }

            public void removeUpdate(DocumentEvent e) {
                filterTable(filterField.getText());
            }

            public void changedUpdate(DocumentEvent e) {
                filterTable(filterField.getText());
            }
        });

        JScrollPane scrollPane = new JScrollPane(eventsTable);
        scrollPane.setPreferredSize(new Dimension(900, 400));

        JPanel northWrapper = new JPanel();
        northWrapper.setLayout(new BoxLayout(northWrapper, BoxLayout.Y_AXIS));
        northWrapper.setBackground(Color.WHITE);
        northWrapper.add(topPanel);
        northWrapper.add(filterPanel);

        mainPanel.add(northWrapper, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        loadEvents();
    }

    private void loadEvents() {
        final String DB_URL = "jdbc:mysql://localhost:3306/neighborhood_watch";
        final String USER = "root";
        final String PASS = "";

        String query = "SELECT eventId, title, description, eventDate, location FROM events";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            tableModel.setRowCount(0);
            while (rs.next()) {
                int id = rs.getInt("eventId");
                String title = rs.getString("title");
                String description = rs.getString("description");
                Timestamp date = rs.getTimestamp("eventDate");
                String location = rs.getString("location");
                JPanel actionPanel = createActionButtons(id);

                tableModel.addRow(new Object[]{id, title, description, date, location, actionPanel});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Failed to load events: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterTable(String query) {
        if (query.trim().isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + query, 1));
        }
    }

    private JPanel createActionButtons(int eventId) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        panel.setBackground(Color.WHITE);

        JButton updateBtn = new JButton("Update");
        styleButton(updateBtn, new Color(40, 167, 69), Color.WHITE);
        updateBtn.addActionListener(e -> new CreateEventDialog(this, eventId).setVisible(true));

        JButton deleteBtn = new JButton("Delete");
        styleButton(deleteBtn, new Color(220, 53, 69), Color.WHITE);
        deleteBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this event?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                deleteEvent(eventId);
            }
        });

        panel.add(updateBtn);
        panel.add(deleteBtn);
        return panel;
    }

    private void deleteEvent(int eventId) {
        final String DB_URL = "jdbc:mysql://localhost:3306/neighborhood_watch";
        final String USER = "root";
        final String PASS = "";

        String sql = "DELETE FROM events WHERE eventId = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, eventId);
            ps.executeUpdate();
            loadEvents();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error deleting event: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setForeground(fgColor);
        button.setBackground(bgColor);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private static class PanelRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            return (Component) value;
        }
    }

    private class PanelEditor extends AbstractCellEditor implements TableCellEditor {
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            return (Component) value;
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }

        @Override
        public boolean isCellEditable(EventObject e) {
            return true;
        }
    }
}
