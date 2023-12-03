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

    private LoginController loginController;


    public LoginFrame() {

        this.loginController = new LoginController();

        setTitle("Flight Reservation - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setLocationRelativeTo(null);

        userTypeComboBox = new JComboBox<>(UserType.values());
        // Display a welcome message
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
                // If not registering, ask for email and add the user to the Users table
                String email = JOptionPane.showInputDialog(this, "Please enter your email:");
                if (email != null && !email.isEmpty()) {
                    addUserToDatabase(username, email);
                    openFlightSelectionFrame(selectedUserType, username, passwordString);
                } else {
                    JOptionPane.showMessageDialog(this, "Email is required.");
                }
            }
        } else {
            if (authenticateUser(selectedUserType, username, passwordString)) {
                // Check and prompt for membership, credit card, and companion ticket
                checkMemberAttributes(selectedUserType, username);
                checkCreditCard(selectedUserType, username);
                // After checks, open the appropriate frame
                openFlightSelectionFrame(selectedUserType, username, passwordString);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.");
            }
        }
    }

    private void addUserToDatabase(String username, String email) {
        String query = "INSERT INTO Users (UserName, Email, UserType) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, UserType.Unregistered.name()); // Set UserType to Unregistered

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private boolean authenticateUser(UserType userType, String username, String password) {
        switch (userType) {
            case Registered:
            case AirlineAgent:
            case SystemAdmin:
            case FlightAttendant:
                return loginController.checkCredentials(username, password);
            default:
                return false;
        }
    }


    private void openFlightSelectionFrame(UserType userType, String username, String password) {
        if (userType == UserType.FlightAttendant) {
            FlightAttendantFrame flightAttendantFrame = new FlightAttendantFrame();
            flightAttendantFrame.setVisible(true);
        } else if (userType == UserType.SystemAdmin) {
            SystemAdminFrame systemAdminFrame = new SystemAdminFrame();
            systemAdminFrame.setVisible(true);
        } else {
            // Check if the user has a ticket
            boolean hasTicket = loginController.checkUserHasTicket(username);
    
            if (hasTicket) {
                // Display the user's ticket
                loginController.displayUserTicket(username);
    
                // Add a button to cancel the ticket
                JButton cancelTicketButton = new JButton("Cancel Ticket");
                cancelTicketButton.addActionListener(e -> cancelTicket(username));
    
                // Get the content pane of the frame
                getContentPane().add(cancelTicketButton);
    
                // Revalidate and repaint the frame to update the UI
                revalidate();
                repaint();
            } else {
                // If the user does not have a ticket, proceed to flight selection
                FlightSelectionFrame flightSelectionFrame = new FlightSelectionFrame(userType);
                flightSelectionFrame.setVisible(true);
            }
        }
    }
    
    private void cancelTicket(String username) {
        // Update the logic to cancel the user's ticket and set the cancellation date
        String updateTicketQuery = "UPDATE Tickets SET IsCancelled = true, CancellationDate = NOW(), SeatID = null WHERE UserName = ?";
        String updatePassengerQuery = "UPDATE Passengers SET FlightID = NULL, PassengerName = null WHERE TicketID = (SELECT TicketID FROM Tickets WHERE UserName = ? AND IsCancelled = true LIMIT 1)";
    
        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             PreparedStatement updateTicketStatement = connection.prepareStatement(updateTicketQuery);
             PreparedStatement updatePassengerStatement = connection.prepareStatement(updatePassengerQuery)) {
            // Set the username parameter
            updateTicketStatement.setString(1, username);
    
            int rowsAffected = updateTicketStatement.executeUpdate();
    
            if (rowsAffected > 0) {
                // Update the Passengers table with FlightID set to NULL and PassengerName set to null
                updatePassengerStatement.setString(1, username);
                updatePassengerStatement.executeUpdate();
    
                JOptionPane.showMessageDialog(this, "Ticket Canceled! Passenger information updated.");
                revalidate();
                repaint();
            } else {
                JOptionPane.showMessageDialog(this, "Ticket not found for cancellation.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public int getPassengerID(String username, Connection connection) throws SQLException {
        // Retrieve the PassengerID associated with the given username
        String passengerIDQuery = "SELECT PassengerID FROM Passengers WHERE UserName = ?";
        try (PreparedStatement passengerIDStatement = connection.prepareStatement(passengerIDQuery)) {
            passengerIDStatement.setString(1, username);
            try (ResultSet resultSet = passengerIDStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("PassengerID");
                }
            }
        }
    
        // Return -1 if no PassengerID is found
        return -1;
    }
    
    private void openUserRegistrationFrame() {
        UserRegistrationFrame registrationFrame = new UserRegistrationFrame(this);
        registrationFrame.setVisible(true);
        this.dispose();
    }

    private void checkMemberAttributes(UserType userType, String username) {
        if (userType == UserType.Registered) {
            // Check membership attributes and prompt if needed
            boolean isMember = loginController.getMembershipStatus(username);
            if (!isMember) {
                int option = JOptionPane.showConfirmDialog(this,
                        "Would you like to become a member of Vortex Airlines Rewards Program?", "Membership",
                        JOptionPane.YES_NO_OPTION);

                if (option == JOptionPane.YES_OPTION) {
                    loginController.updateMembershipStatus(username, true);
                }
            }
        }
    }

    private void checkCreditCard(UserType userType, String username) {
        // Exclude the prompt for company credit card for specific user types
        if (userType == UserType.SystemAdmin || userType == UserType.FlightAttendant || userType == UserType.AirlineAgent) {
            return;
        }

        // Check credit card status 
        boolean hasCompanyCreditCard = loginController.getCompanyCreditCardStatus(username);
        if (!hasCompanyCreditCard) {
            int option = JOptionPane.showConfirmDialog(this,
                    "Would you like to apply for a Vortex Airlines credit card?", "Credit Card",
                    JOptionPane.YES_NO_OPTION);

            if (option == JOptionPane.YES_OPTION) {
                loginController.updateCreditCardStatus(username, true);
            }
        }
    }

    public void setUserType(UserType userType) {
        userTypeComboBox.setSelectedItem(userType);
    }

}
