import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserListFrame extends JFrame {
    private JList<String> userList;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/airlinedb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Password25";

    public UserListFrame() {
        setTitle("Flight Reservation - User List");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        // Create a list model to store user information
        DefaultListModel<String> listModel = new DefaultListModel<>();
        userList = new JList<>(listModel);

        // Scroll pane for the user list
        JScrollPane scrollPane = new JScrollPane(userList);

        // Load users from the database
        loadUsers();

        // Layout setup
        setLayout(new BorderLayout());
        add(new JLabel("List of Registered Users"), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    // Function to load users from the database
    private void loadUsers() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT UserName, Email, UserType, Address FROM Users";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                DefaultListModel<String> listModel = (DefaultListModel<String>) userList.getModel();

                while (resultSet.next()) {
                    String userInfo = "Username: " + resultSet.getString("UserName") +
                                      ", Email: " + resultSet.getString("Email") +
                                      ", UserType: " + resultSet.getString("UserType") +
                                      ", Address: " + resultSet.getString("Address");

                    listModel.addElement(userInfo);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UserListFrame().setVisible(true));
    }
}
