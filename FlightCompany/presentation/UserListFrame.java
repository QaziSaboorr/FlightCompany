package presentation;
import datasource.DatabaseConnector;
import domain.UserType;

import javax.swing.*;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserListFrame extends JFrame {
    private JList<String> userList;

    public UserListFrame() {

        setTitle("Flight Reservation - User List");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        // Create a list to store user information
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

    // Function to load registered users from the database
    private void loadUsers() {
        try (Connection connection = DatabaseConnector.getInstance().getConnection()) {
            String query = "SELECT UserName, Email, UserType, Address FROM Users WHERE UserType = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                // Set the UserType parameter to "Registered"
                preparedStatement.setString(1, UserType.Registered.name());

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    DefaultListModel<String> listModel = (DefaultListModel<String>) userList.getModel();

                    while (resultSet.next()) {
                        String userInfo = "Username: " + resultSet.getString("UserName") +
                                ", Email: " + resultSet.getString("Email") +
                                ", UserType: " + resultSet.getString("UserType") +
                                ", Address: " + resultSet.getString("Address");

                        listModel.addElement(userInfo);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading registered users from the database.");
        }
    }

}
