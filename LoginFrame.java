
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
        // Display a welcome message for Unregistered users
        JOptionPane.showMessageDialog(this, "Welcome to Vortex Airlines!");
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

        // Add password components only if the selected UserType is not Unregistered
        userTypeComboBox.addActionListener(e -> {
            UserType selectedUserType = (UserType) userTypeComboBox.getSelectedItem();
            boolean isUnregistered = selectedUserType == UserType.Unregistered;
            passwordLabel.setVisible(!isUnregistered);
            passwordField.setVisible(!isUnregistered);
        });

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
            int option = JOptionPane.showConfirmDialog(this, "Would you like to register for Vortex Airlines?",
                    "Register", JOptionPane.YES_NO_OPTION);

            if (option == JOptionPane.YES_OPTION) {
                openUserRegistrationFrame();
            } else {
                openFlightSelectionFrame(selectedUserType, username, null);
            }
        } else {
            if (authenticateUser(selectedUserType, username, passwordString)) {
                // Check and prompt for membership, credit card, and companion ticket
                checkMemberAttributes(selectedUserType, username);
                checkCreditCard(selectedUserType, username);
                checkRedeemedCompanionTicket(selectedUserType, username);

                // After checks, open the appropriate frame
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
        } else if (userType == UserType.SystemAdmin) {
            SystemAdminFrame SystemAdminFrame = new SystemAdminFrame(databaseConnector);
            SystemAdminFrame.setVisible(true);
        } else {
            FlightSelectionFrame flightSelectionFrame = new FlightSelectionFrame(userType, databaseConnector);
            flightSelectionFrame.setVisible(true);
        }
        this.dispose();
    }

    private void openUserRegistrationFrame() {
        UserRegistrationFrame registrationFrame = new UserRegistrationFrame(databaseConnector, this);
        registrationFrame.setVisible(true);
        this.dispose();
    }

    private void checkMemberAttributes(UserType userType, String username) {
        if (userType == UserType.Registered) {
            // Check membership attributes and prompt if needed
            boolean isMember = getMembershipStatus(username);
            if (!isMember) {
                int option = JOptionPane.showConfirmDialog(this,
                        "Would you like to become a member of Vortex Airlines?", "Membership",
                        JOptionPane.YES_NO_OPTION);

                if (option == JOptionPane.YES_OPTION) {
                    updateMembershipStatus(username, true);
                }
            }
        }
    }

    private void checkCreditCard(UserType userType, String username) {
        // Check credit card status and prompt if needed
        boolean hasCompanyCreditCard = getCompanyCreditCardStatus(username);
        if (!hasCompanyCreditCard) {
            int option = JOptionPane.showConfirmDialog(this,
                    "Would you like to apply for a company credit card?", "Credit Card",
                    JOptionPane.YES_NO_OPTION);

            if (option == JOptionPane.YES_OPTION) {
                updateCreditCardStatus(username, true);
            }
        }
    }

    private void checkRedeemedCompanionTicket(UserType userType, String username) {
        if (userType == UserType.Registered) {
            // Check companion ticket status and prompt if needed
            boolean hasRedeemedCompanionTicket = getCompanionTicketRedemptionStatus(username);
            if (!hasRedeemedCompanionTicket) {
                int option = JOptionPane.showConfirmDialog(this,
                        "Would you like to redeem your free companion ticket?", "Companion Ticket",
                        JOptionPane.YES_NO_OPTION);

                if (option == JOptionPane.YES_OPTION) {
                    updateCompanionTicketRedemptionStatus(username, true);
                }
            }
        }
    }
    private boolean getMembershipStatus(String username) {
        String query = "SELECT IsMember FROM Users WHERE UserName = ?";

        try (Connection connection = databaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBoolean("IsMember");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; // Default value
    }

    private void updateMembershipStatus(String username, boolean isMember) {
        String query = "UPDATE Users SET IsMember = ? WHERE UserName = ?";

        try (Connection connection = databaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setBoolean(1, isMember);
            preparedStatement.setString(2, username);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean getCompanyCreditCardStatus(String username) {
        String query = "SELECT HasCompanyCreditCard FROM Users WHERE UserName = ?";

        try (Connection connection = databaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBoolean("HasCompanyCreditCard");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; // Default value
    }

    private void updateCreditCardStatus(String username, boolean hasCompanyCreditCard) {
        String query = "UPDATE Users SET HasCompanyCreditCard = ? WHERE UserName = ?";

        try (Connection connection = databaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setBoolean(1, hasCompanyCreditCard);
            preparedStatement.setString(2, username);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean getCompanionTicketRedemptionStatus(String username) {
        String query = "SELECT HasRedeemedCompanionTicket FROM Users WHERE UserName = ?";

        try (Connection connection = databaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBoolean("HasRedeemedCompanionTicket");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; // Default value
    }

    private void updateCompanionTicketRedemptionStatus(String username, boolean hasRedeemedCompanionTicket) {
        String query = "UPDATE Users SET HasRedeemedCompanionTicket = ? WHERE UserName = ?";

        try (Connection connection = databaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setBoolean(1, hasRedeemedCompanionTicket);
            preparedStatement.setString(2, username);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void setUserType(UserType userType) {
        userTypeComboBox.setSelectedItem(userType);
    }
    
}
