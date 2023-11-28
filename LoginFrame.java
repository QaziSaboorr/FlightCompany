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

    private DatabaseController databaseController;

    public LoginFrame(DatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;

        databaseController = new DatabaseController(databaseConnector);

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
                // If not registering, ask for email and add the user to the Users table
                String email = JOptionPane.showInputDialog(this, "Please enter your email:");
                if (email != null && !email.isEmpty()) {
                    databaseController.addUserToDatabase(username, email);
                    openFlightSelectionFrame(selectedUserType, username, passwordString);
                } else {
                    JOptionPane.showMessageDialog(this, "Email is required.");
                }
            }
        } else {
            if (databaseController.authenticateUser(selectedUserType, username, passwordString)) {
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


    private void openFlightSelectionFrame(UserType userType, String username, String password) {
        if (userType == UserType.FlightAttendant) {
            FlightAttendantFrame flightAttendantFrame = new FlightAttendantFrame(databaseConnector);
            flightAttendantFrame.setVisible(true);
        } else if (userType == UserType.SystemAdmin) {
            SystemAdminFrame systemAdminFrame = new SystemAdminFrame(databaseConnector);
            systemAdminFrame.setVisible(true);
        } 
        else if(userType == UserType.Unregistered) {
            UserHomeFrame userHomeFrame = new UserHomeFrame(databaseConnector);
            userHomeFrame.setVisible(true);

        }
        else if(userType == UserType.Registered) {
            RegisteredUserHomeFrame registeredUserHomeFrame = new RegisteredUserHomeFrame(databaseConnector, username);
            registeredUserHomeFrame.setVisible(true);

        }
        
        else {
            // Check if the user has a ticket
            boolean hasTicket = databaseController.checkUserHasTicket(username);
    
            if (hasTicket) {
                // Display the user's ticket
                displayUserTicket(username);
    
                // Add a button to cancel the ticket
                JButton cancelTicketButton = new JButton("Cancel Ticket");
                cancelTicketButton.addActionListener(e -> databaseController.cancelTicket(username));
                add(cancelTicketButton);
            } else {
                // If the user does not have a ticket, proceed to flight selection
                FlightSelectionFrame flightSelectionFrame = new FlightSelectionFrame(userType, databaseConnector);
                flightSelectionFrame.setVisible(true);
            }
        }
        this.dispose();
    }
    

    private void displayUserTicket(String username) {
        // Implement the logic to display the user's ticket based on their UserType
        // For example, fetch and display relevant information from the Tickets table
        // You can use a JOptionPane or create a new JFrame for displaying the ticket details
        // This could include information such as flight details, seat, price, etc.
        // You can reuse some of the logic from the TicketConfirmationFrame class for displaying ticket details.
        // Note: This is a simplified example, and you might need to adjust it based on your database schema and requirements.
    }
    
    private void openUserRegistrationFrame() {
        UserRegistrationFrame registrationFrame = new UserRegistrationFrame(databaseConnector, this);
        registrationFrame.setVisible(true);
        this.dispose();
    }

    private void checkMemberAttributes(UserType userType, String username) {
        if (userType == UserType.Registered) {
            // Check membership attributes and prompt if needed
            boolean isMember = databaseController.getMembershipStatus(username);
            if (!isMember) {
                int option = JOptionPane.showConfirmDialog(this,
                        "Would you like to become a member of Vortex Airlines Rewards Program?", "Membership",
                        JOptionPane.YES_NO_OPTION);

                if (option == JOptionPane.YES_OPTION) {
                    databaseController.updateMembershipStatus(username, true);
                }
            }
        }
    }

    private void checkCreditCard(UserType userType, String username) {
        // Check credit card status and prompt if needed
        boolean hasCompanyCreditCard = databaseController.getCompanyCreditCardStatus(username);
        if (!hasCompanyCreditCard) {
            int option = JOptionPane.showConfirmDialog(this,
                    "Would you like to apply for a Vortex Airlines credit card?", "Credit Card",
                    JOptionPane.YES_NO_OPTION);

            if (option == JOptionPane.YES_OPTION) {
                UserUtils.updateCreditCardStatus(databaseConnector, username, true);
            }
        }
    }

    private void checkRedeemedCompanionTicket(UserType userType, String username) {
        if (userType == UserType.Registered) {
            // Check companion ticket status and prompt if needed
            boolean hasRedeemedCompanionTicket = databaseController.getCompanionTicketRedemptionStatus(username);
            if (!hasRedeemedCompanionTicket) {
                int option = JOptionPane.showConfirmDialog(this,
                        "Would you like to redeem your 1 free companion ticket?", "Companion Ticket",
                        JOptionPane.YES_NO_OPTION);

                if (option == JOptionPane.YES_OPTION) {
                    databaseController.updateCompanionTicketRedemptionStatus(username, true);
                }
            }
        }
    }

    public void setUserType(UserType userType) {
        userTypeComboBox.setSelectedItem(userType);
    }
    
}
