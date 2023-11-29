import javax.swing.*;


import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TicketConfirmationFrame extends JFrame {
    private UserType userType;
    private String selectedFlight;
    private String seatNumber;
    private String seatType;
    private double seatPrice;
    private boolean insuranceSelected;
    private DatabaseConnector databaseConnector;
    

    public TicketConfirmationFrame(UserType userType, String selectedFlight, String seatNumber, String seatType, double seatPrice, boolean insuranceSelected, DatabaseConnector databaseConnector) {
        this.userType = userType;
        this.selectedFlight = selectedFlight;
        this.seatNumber = seatNumber;
        this.seatType = seatType;
        this.seatPrice = seatPrice;
        this.insuranceSelected = insuranceSelected;
        this.databaseConnector = databaseConnector; // Add this line

        setTitle("Flight Reservation - Ticket Confirmation");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(400, 250); // Increased height to accommodate user name and email
        setLocationRelativeTo(null);

        // Implement your ticket confirmation UI here
        JPanel panel = createConfirmationPanel();
        add(panel, BorderLayout.CENTER);

        // Create cancel ticket button
        JButton cancelTicketButton = new JButton("Cancel Ticket");
        cancelTicketButton.addActionListener(e -> cancelTicket());
        add(cancelTicketButton, BorderLayout.SOUTH);
    }

    private JPanel createConfirmationPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    
        // Display relevant information on the confirmation panel
        panel.add(new JLabel("Flight: " + selectedFlight));
        panel.add(new JLabel("Seat: " + seatNumber + " (" + seatType + ")"));
    
        // Check if companion ticket was used
        boolean useCompanionTicket = checkCompanionTicketUsage();
    
        // Update the price and insurance display based on the use of a companion ticket
        if (useCompanionTicket) {
            seatPrice = 0; // Update price to 0 when using a companion ticket
            insuranceSelected = false; // Companion ticket comes with free insurance
            panel.add(new JLabel("Price: Free (Companion Ticket)"));
            panel.add(new JLabel("Insurance: Free (Companion Ticket)"));
        } else {
            // Use the updated seatPrice for display
            double displayPrice = insuranceSelected ? seatPrice + 20.0 : seatPrice;
            panel.add(new JLabel("Price: $" + displayPrice));
            panel.add(new JLabel("Insurance Selected: " + (insuranceSelected ? "Yes" : "No")));
        }
    
        // Get user name and email
        String userName = getUserName();
        String userEmail = getUserEmail();
    
        panel.add(new JLabel("User Name: " + userName));
        panel.add(new JLabel("Email: " + userEmail));
    
        // Add extras text for registered users
        if (getUserType() == UserType.Registered) {
            panel.add(Box.createVerticalStrut(10)); // Add some vertical spacing
            panel.add(new JLabel("To thank you for being a Registered User of Vortex Airlines, you get the following:"));
            panel.add(new JLabel("- Discounted access to Airport Lounges"));
            panel.add(new JLabel("- Monthly Promos"));
        }
    
        // Remove Confirm Ticket button
        JButton proceedToPaymentButton = new JButton("Proceed to Payment");
        proceedToPaymentButton.addActionListener(e -> proceedToPayment());
        panel.add(proceedToPaymentButton);
    
        return panel;
    }
    

    public boolean checkCompanionTicketUsage() {
        try (Connection connection = databaseConnector.getConnection()) {
            String query;
            if (getUserType() == UserType.Registered) {
                // Check for companion ticket redemption for registered users
                query = "SELECT HasRedeemedCompanionTicket FROM Users WHERE UserID = ?";
            } else {
                // Unregistered users shouldn't have a companion ticket
                return false;
            }
    
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, userType.ordinal() + 1);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return resultSet.next() && resultSet.getBoolean("HasRedeemedCompanionTicket");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private String getUserName() {
        try (Connection connection = databaseConnector.getConnection()) {
            String query = "SELECT UserName FROM Users ORDER BY UserID DESC LIMIT 1";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("UserName");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "N/A";
    }
    
    public String getUserEmail() {
        try (Connection connection = databaseConnector.getConnection()) {
            String query = "SELECT Email FROM Users ORDER BY UserID DESC LIMIT 1";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("Email");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "N/A";
    }
    // Getter method for userType
    public UserType getUserType() {
        return userType;
    }

    // Getter method for databaseConnector
    public DatabaseConnector getDatabaseConnector() {
        return databaseConnector;
    }

    // Getter method for seatType
    public String getSeatType() {
        return seatType;
    }
    


    public void confirmTicket() {
        // Logic for confirming the ticket and updating the database
        try {
            // Use the shared database connector
            try (Connection connection = databaseConnector.getConnection()) {
                // Check if a companion ticket is redeemed
                boolean useCompanionTicket = checkCompanionTicketUsage();
    
                if (!useCompanionTicket) {
                    redeemCompanionTicket(); // Prompt for companion ticket redemption
                }
    
                // Adjust seat price based on the companion ticket redemption
                if (useCompanionTicket) {
                    seatPrice = 0.0; // The original ticket is free if a companion ticket is redeemed
                    insuranceSelected = false; // Companion ticket comes with free insurance
                } else if (insuranceSelected) {
                    seatPrice += 20.0; // Increment seat price if insurance is selected
                }
    
                // Insert data into the Tickets table for the original ticket
                String ticketInsertQuery = "INSERT INTO Tickets (UserID, Email, UserName, FlightID, SeatID, SeatType, SeatNumber, Destination, InsuranceSelected, PaymentAmount) " +
                        "VALUES (?, ?, ?, (SELECT FlightID FROM Flights WHERE FlightNumber = ? LIMIT 1), " +
                        "(SELECT SeatID FROM Seats WHERE SeatNumber = ? LIMIT 1), ?, ?, " +
                        "(SELECT Destination FROM Flights WHERE FlightNumber = ? LIMIT 1), ?, ?)";
    
                try (PreparedStatement ticketPreparedStatement = connection.prepareStatement(ticketInsertQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    ticketPreparedStatement.setInt(1, userType.ordinal() + 1);
                    ticketPreparedStatement.setString(2, getUserEmail());
                    ticketPreparedStatement.setString(3, getUserName());
                    ticketPreparedStatement.setString(4, selectedFlight);
                    ticketPreparedStatement.setString(5, seatNumber);
                    ticketPreparedStatement.setString(6, seatType);
                    ticketPreparedStatement.setString(7, seatNumber);
                    ticketPreparedStatement.setString(8, selectedFlight);
                    ticketPreparedStatement.setBoolean(9, insuranceSelected);
                    ticketPreparedStatement.setDouble(10, seatPrice);
                    ticketPreparedStatement.executeUpdate();
    
                    // Retrieve the auto-generated ticket ID for the original ticket
                    try (ResultSet generatedKeys = ticketPreparedStatement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int ticketID = generatedKeys.getInt(1);
    
                            // Insert data into the Passengers table for the original ticket
                            String passengerInsertQuery = "INSERT INTO Passengers (TicketID, FlightID, PassengerName) VALUES (?, (SELECT FlightID FROM Flights WHERE FlightNumber = ? LIMIT 1), ?)";
                            try (PreparedStatement passengerPreparedStatement = connection.prepareStatement(passengerInsertQuery)) {
                                passengerPreparedStatement.setInt(1, ticketID);
                                passengerPreparedStatement.setString(2, selectedFlight);
                                passengerPreparedStatement.setString(3, getUserName());
                                passengerPreparedStatement.executeUpdate();
                            }
    
                            // Insert data into the Payments table for the original ticket
                            String paymentInsertQuery = "INSERT INTO Payments (TicketID, PaymentAmount) VALUES (?, ?)";
                            try (PreparedStatement paymentPreparedStatement = connection.prepareStatement(paymentInsertQuery)) {
                                paymentPreparedStatement.setInt(1, ticketID);
                                paymentPreparedStatement.setDouble(2, seatPrice);
                                paymentPreparedStatement.executeUpdate();
                            }
                        } else {
                            throw new SQLException("Failed to retrieve the generated ticket ID for the original ticket.");
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    
        // Additional logic for ticket confirmation (e.g., sending email, updating UI)
        JOptionPane.showMessageDialog(this, "Ticket Confirmed!");
        dispose();
    }
    
    
    
    private void cancelTicket() {
        try {
            // Use the shared database connector
            try (Connection connection = databaseConnector.getConnection()) {
                String query = "UPDATE Tickets SET IsCancelled = TRUE, CancellationDate = NOW() WHERE UserID = ? AND FlightID = (SELECT FlightID FROM Flights WHERE FlightNumber = ? LIMIT 1)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setInt(1, userType.ordinal() + 1);
                    preparedStatement.setString(2, selectedFlight);
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // Additional logic for ticket cancellation (e.g., updating UI)
        JOptionPane.showMessageDialog(this, "Ticket Cancelled!");
        dispose();
    }


    private void proceedToPayment() {
        // Open the payment frame
        PaymentFrame paymentFrame = new PaymentFrame(this, selectedFlight, seatNumber, seatPrice);
        paymentFrame.setVisible(true);
        dispose(); // Close the current frame
    }

    public boolean isInsuranceSelected() {
        return insuranceSelected;
    }
    
    private void redeemCompanionTicket() {
        int option = JOptionPane.showConfirmDialog(this,
                "You have a free companion ticket available. Do you want to redeem it?", "Companion Ticket Redemption",
                JOptionPane.YES_NO_OPTION);
    
        if (option == JOptionPane.YES_OPTION) {
            // Allow the user to choose a seat for the companion ticket
            String newSeatNumber = JOptionPane.showInputDialog(this, "Choose a seat for the companion ticket:");
    
            // Confirm the redemption and create the new ticket
            if (newSeatNumber != null && !newSeatNumber.isEmpty()) {
                createCompanionTicket(newSeatNumber);
            } else {
                JOptionPane.showMessageDialog(this, "Seat selection is required.");
            }
        }
    }
    
    private void createCompanionTicket(String newSeatNumber) {
        // Logic to create a new ticket for the same flight with the chosen seat
        // Set seatPrice to 0 for the companion ticket
        double companionTicketPrice = 0.0;
    
        // Insert data into the Tickets table for the companion ticket
        String companionTicketInsertQuery = "INSERT INTO Tickets (UserID, Email, UserName, FlightID, SeatID, SeatType, SeatNumber, Destination, InsuranceSelected, PaymentAmount) " +
                "VALUES (?, ?, ?, (SELECT FlightID FROM Flights WHERE FlightNumber = ? LIMIT 1), " +
                "(SELECT SeatID FROM Seats WHERE SeatNumber = ? LIMIT 1), ?, ?, " +
                "(SELECT Destination FROM Flights WHERE FlightNumber = ? LIMIT 1), ?, ?)";
    
        try (Connection connection = databaseConnector.getConnection();
             PreparedStatement companionTicketPreparedStatement = connection.prepareStatement(companionTicketInsertQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
    
            companionTicketPreparedStatement.setInt(1, userType.ordinal() + 1);
            companionTicketPreparedStatement.setString(2, getUserEmail());
            companionTicketPreparedStatement.setString(3, getUserName());
            companionTicketPreparedStatement.setString(4, selectedFlight);
            companionTicketPreparedStatement.setString(5, newSeatNumber);
            companionTicketPreparedStatement.setString(6, seatType);
            companionTicketPreparedStatement.setString(7, newSeatNumber);
            companionTicketPreparedStatement.setString(8, selectedFlight);
            companionTicketPreparedStatement.setBoolean(9, false); // Companion ticket does not have insurance
            companionTicketPreparedStatement.setDouble(10, companionTicketPrice);
            companionTicketPreparedStatement.executeUpdate();
    
            // Additional logic for companion ticket confirmation (e.g., sending email)
            JOptionPane.showMessageDialog(this, "Companion Ticket Redeemed!");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
}