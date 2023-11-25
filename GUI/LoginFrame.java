import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginFrame extends JFrame {
    private JComboBox<UserType> userTypeComboBox;
    private JLabel userLabel;
    private JTextField userField;
    private JLabel passwordLabel;
    private JPasswordField passwordField;
    private JButton loginButton;
    private DatabaseConnector databaseConnector;

    public LoginFrame(DatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;

        setTitle("Flight Reservation - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setLocationRelativeTo(null);

        userTypeComboBox = new JComboBox<>(UserType.values());
        userLabel = new JLabel("Username:");
        userField = new JTextField();
        passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();
        loginButton = new JButton("Login");

        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        add(new JLabel("Select User Type:"));
        add(userTypeComboBox);
        add(userLabel);
        add(userField);
        add(passwordLabel);
        add(passwordField);
        add(loginButton);

        // Action Listener for login button
        loginButton.addActionListener(e -> {
            try {
                openNextFrame();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    private void openNextFrame() {
        UserType selectedUserType = (UserType) userTypeComboBox.getSelectedItem();
        String username = userField.getText();
        char[] password = passwordField.getPassword();
        String passwordString = new String(password);

        if (selectedUserType == UserType.Unregistered) {
            openFlightSelectionFrame(selectedUserType, username, null);
        } else {
            if (authenticateUser(selectedUserType, username, passwordString)) {
                openFlightSelectionFrame(selectedUserType, username, passwordString);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.");
            }
        }
    }

    

    private boolean authenticateUser(UserType userType, String username, String password) {
        // Implement user authentication logic here (query the database, etc.)
        // For simplicity, compare with the fake data
        switch (userType) {
            case Registered:
            case AirlineAgent:
            case SystemAdmin:
            case FlightAttendant:
                return checkCredentials(username, password);
            default:
                return false;
        }
    }

    private boolean checkCredentials(String username, String password) {
        String query = "SELECT * FROM Users WHERE UserName = ? AND Password = ?";
        
        try (Connection connection = databaseConnector.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next(); // Returns true if a matching user is found
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }


    private void openFlightSelectionFrame(UserType userType, String username, String password) {
        if (userType == UserType.FlightAttendant) {
            FlightAttendantFrame flightAttendantFrame = new FlightAttendantFrame(databaseConnector);
            flightAttendantFrame.setVisible(true);
        }if(userType == UserType.SystemAdmin){
            SystemAdminFrame SystemAdminFrame = new SystemAdminFrame(databaseConnector);
            SystemAdminFrame.setVisible(true);
        }else {
            FlightSelectionFrame flightSelectionFrame = new FlightSelectionFrame(userType, databaseConnector);
            flightSelectionFrame.setVisible(true);
        }
        this.dispose();
    }
}