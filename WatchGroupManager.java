import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class WatchGroupManager extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JButton btnEdit, btnDelete;

    private final String DB_URL = "jdbc:mysql://localhost:3306/neighborhood_watch";
    private final String USER = "root";
    private final String PASS = "";

    public WatchGroupManager() {
        setTitle("Manage Watch Groups");
        setSize(800, 400);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        model = new DefaultTableModel(
            new String[]{"ID", "Event ID", "Organizer ID", "Title", "Description", "Event Date", "Location"}, 0
        );
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel btnPanel = new JPanel();
        btnEdit = new JButton("Edit Group");
        btnDelete = new JButton("Delete Group");

        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);

        add(scrollPane, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        loadGroups();

        btnEdit.addActionListener(e -> editGroup());
        btnDelete.addActionListener(e -> deleteGroup());
    }

    private void loadGroups() {
        model.setRowCount(0);
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM watchgroups")) {

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("watchGroupId"));
                row.add(rs.getInt("eventId"));
                row.add(rs.getInt("organizerId"));
                row.add(rs.getString("title"));
                row.add(rs.getString("description"));
                row.add(rs.getTimestamp("eventDate"));
                row.add(rs.getString("location"));
                model.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading groups: " + e.getMessage());
        }
    }

    private void editGroup() {
        int selected = table.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "Select a group to edit.");
            return;
        }

        int id = (int) model.getValueAt(selected, 0);
        int eventId = (int) model.getValueAt(selected, 1);
        int organizerId = (int) model.getValueAt(selected, 2);
        String title = (String) model.getValueAt(selected, 3);
        String description = (String) model.getValueAt(selected, 4);
        Timestamp eventDate = (Timestamp) model.getValueAt(selected, 5);
        String location = (String) model.getValueAt(selected, 6);

        GroupForm form = new GroupForm(id, eventId, organizerId, title, description, eventDate, location);
        form.setVisible(true);

        // Refresh the table after editing
        loadGroups();
    }

    private void deleteGroup() {
        int selected = table.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "Select a group to delete.");
            return;
        }

        int id = (int) model.getValueAt(selected, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this group?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                 PreparedStatement ps = conn.prepareStatement("DELETE FROM watchgroups WHERE watchGroupId = ?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
                loadGroups();
                JOptionPane.showMessageDialog(this, "Group deleted.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting group: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WatchGroupManager().setVisible(true));
    }
}
