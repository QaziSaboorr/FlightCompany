
import javax.swing.*; 
import java.sql.Timestamp;
import java.util.Date;
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
        try (Connection connection = databaseConnector.getConnection();
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
            SystemAdminFrame systemAdminFrame = new SystemAdminFrame(databaseConnector);
            systemAdminFrame.setVisible(true);
        } else {
            // Check if the user has a ticket
            boolean hasTicket = checkUserHasTicket(username);
    
            if (hasTicket) {
                // Display the user's ticket
                displayUserTicket(username);
    
                // Add a button to cancel the ticket
                JButton cancelTicketButton = new JButton("Cancel Ticket");
                cancelTicketButton.addActionListener(e -> cancelTicket(username));
    
                // Use getContentPane() to get the content pane of the frame
                getContentPane().add(cancelTicketButton);
    
                // Revalidate and repaint the frame to update the UI
                revalidate();
                repaint();
            } else {
                // If the user does not have a ticket, proceed to flight selection
                FlightSelectionFrame flightSelectionFrame = new FlightSelectionFrame(userType, databaseConnector);
                flightSelectionFrame.setVisible(true);
            }
        }
        // Do not dispose of the frame here
    }
    
    
    
    
    private boolean checkUserHasTicket(String username) {
        String query = "SELECT * FROM Tickets WHERE UserName = ?";
    
        try (Connection connection = databaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
    
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next(); // Returns true if the user has a ticket
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return false;
    }
    
    private void displayUserTicket(String username) {
        // Query to retrieve ticket information based on the user's username
        String query = "SELECT * FROM Tickets WHERE UserName = ?";
    
        try (Connection connection = databaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
    
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // Extract ticket information from the ResultSet
                    int ticketID = resultSet.getInt("TicketID");
                    int userID = resultSet.getInt("UserID");
                    String email = resultSet.getString("Email");
                    String flightID = resultSet.getString("FlightID");
                    int seatID = resultSet.getInt("SeatID");
                    String seatType = resultSet.getString("SeatType");
                    String seatNumber = resultSet.getString("SeatNumber");
                    String destination = resultSet.getString("Destination");
                    boolean isCancelled = resultSet.getBoolean("IsCancelled");
                    boolean insuranceSelected = resultSet.getBoolean("InsuranceSelected");
                    double paymentAmount = resultSet.getDouble("PaymentAmount");
                    boolean emailSent = resultSet.getBoolean("EmailSent");
                    boolean receiptSent = resultSet.getBoolean("ReceiptSent");
    
                    // Display the ticket information
                    StringBuilder ticketInfo = new StringBuilder();
                    ticketInfo.append("Ticket ID: ").append(ticketID).append("\n");
                    ticketInfo.append("User ID: ").append(userID).append("\n");
                    ticketInfo.append("Email: ").append(email).append("\n");
                    ticketInfo.append("Flight ID: ").append(flightID).append("\n");
                    ticketInfo.append("Seat ID: ").append(seatID).append("\n");
                    ticketInfo.append("Seat Type: ").append(seatType).append("\n");
                    ticketInfo.append("Seat Number: ").append(seatNumber).append("\n");
                    ticketInfo.append("Destination: ").append(destination).append("\n");
                    ticketInfo.append("Is Cancelled: ").append(isCancelled).append("\n");
                    ticketInfo.append("Insurance Selected: ").append(insuranceSelected).append("\n");
                    ticketInfo.append("Payment Amount: $").append(paymentAmount).append("\n");
                    ticketInfo.append("Email Sent: ").append(emailSent).append("\n");
                    ticketInfo.append("Receipt Sent: ").append(receiptSent).append("\n");
    
                    // Display the ticket information using JOptionPane
                    JOptionPane.showMessageDialog(this, ticketInfo.toString(), "Ticket Details", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "No ticket found for the user.", "Ticket Details", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    
    private void cancelTicket(String username) {
        // Update the logic to cancel the user's ticket and set the cancellation date
        String updateTicketQuery = "UPDATE Tickets SET IsCancelled = true, CancellationDate = ?, SeatID = null WHERE UserName = ?";
        String updatePassengerQuery = "UPDATE Passengers SET FlightID = 0, PassengerName = null WHERE PassengerID = ?";
    
        try (Connection connection = databaseConnector.getConnection();
             PreparedStatement updateTicketStatement = connection.prepareStatement(updateTicketQuery);
             PreparedStatement updatePassengerStatement = connection.prepareStatement(updatePassengerQuery)) {
            // Set the cancellation date parameter to the current date and time
            Timestamp cancellationDate = new Timestamp(new Date().getTime());
            updateTicketStatement.setTimestamp(1, cancellationDate);
    
            // Set the username parameter
            updateTicketStatement.setString(2, username);
    
            int rowsAffected = updateTicketStatement.executeUpdate();
    
            if (rowsAffected > 0) {
                // Get the PassengerID associated with the canceled ticket
                int passengerID = getPassengerID(username, connection);
    
                if (passengerID > 0) {
                    // Update the Passengers table with FlightID set to 0 and PassengerName set to null
                    updatePassengerStatement.setInt(1, passengerID);
                    updatePassengerStatement.executeUpdate();
                }
    
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
    
    private int getPassengerID(String username, Connection connection) throws SQLException {
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
                        "Would you like to become a member of Vortex Airlines Rewards Program?", "Membership",
                        JOptionPane.YES_NO_OPTION);

                if (option == JOptionPane.YES_OPTION) {
                    updateMembershipStatus(username, true);
                }
            }
        }
    }

    private void checkCreditCard(UserType userType, String username) {
        // Exclude the prompt for company credit card for specific user types
        if (userType == UserType.SystemAdmin || userType == UserType.FlightAttendant || userType == UserType.AirlineAgent) {
            return;
        }

        // Check credit card status and prompt if needed
        boolean hasCompanyCreditCard = getCompanyCreditCardStatus(username);
        if (!hasCompanyCreditCard) {
            int option = JOptionPane.showConfirmDialog(this,
                    "Would you like to apply for a Vortex Airlines credit card?", "Credit Card",
                    JOptionPane.YES_NO_OPTION);

            if (option == JOptionPane.YES_OPTION) {
                updateCreditCardStatus(username, true);
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


    public void setUserType(UserType userType) {
        userTypeComboBox.setSelectedItem(userType);
    }
    
}
