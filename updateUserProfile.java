
// the code is runing fine regardless of the 2 errors


import javax.swing.JOptionPane;

private updateUserProfile (String username, String email, String password) {
    final String DB_URL = "jdbc:mysql://localhost:3306/neighborhood_watch";
    final String USER = "root";
    final String PASS = "";

    
    String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

    String sql = "UPDATE users SET username = ?, email = ?, password = ? WHERE userId = ?";

    try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, username);
        ps.setString(2, email);
        ps.setString(3, hashedPassword);
        ps.setInt(4, user.getUserId());

        int rowsUpdated = ps.executeUpdate();
        if (rowsUpdated > 0) {
            JOptionPane.showMessageDialog(this, "Profile updated successfully!");
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(hashedPassword);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error updating profile.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }

    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error updating profile: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}
