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
    
    // Add the confirmationPanel here
    private JPanel confirmationPanel;

    public TicketConfirmationFrame(UserType userType, String selectedFlight, String seatNumber, String seatType, double seatPrice, boolean insuranceSelected, DatabaseConnector databaseConnector) {
        this.userType = userType;
        this.selectedFlight = selectedFlight;
        this.seatNumber = seatNumber;
        this.seatType = seatType;
        this.seatPrice = seatPrice;
        this.insuranceSelected = insuranceSelected;
        this.databaseConnector = databaseConnector;
        
        // Initialize confirmationPanel
        this.confirmationPanel = new JPanel(); // You might need to adjust this based on your actual UI design
        
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
            panel.add(new JLabel("Price: $" + seatPrice));
            panel.add(new JLabel("Insurance Selected: " + (insuranceSelected ? "Yes" : "No")));
        }

        // Get user name and email
        String userName = getUserName();
        String userEmail = getUserEmail();

        panel.add(new JLabel("User Name: " + userName));
        panel.add(new JLabel("Email: " + userEmail));

        // Remove Confirm Ticket button
        JButton proceedToPaymentButton = new JButton("Proceed to Payment");
        proceedToPaymentButton.addActionListener(e -> proceedToPayment());
        panel.add(proceedToPaymentButton);

        return panel;
    }

    private boolean checkCompanionTicketUsage() {
        try (Connection connection = databaseConnector.getConnection()) {
            String query = "SELECT HasRedeemedCompanionTicket FROM Users WHERE UserID = ?";
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

    private String getUserEmail() {
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

    public void confirmTicket() {
        // Logic for confirming the ticket and updating the database
        try {
            // Use the shared database connector
            try (Connection connection = databaseConnector.getConnection()) {
                String query = "INSERT INTO Tickets (UserID, Email, UserName, FlightID, SeatID, SeatType, SeatNumber, Destination, InsuranceSelected, PaymentAmount) " +
                        "VALUES (?, ?, ?, (SELECT FlightID FROM Flights WHERE FlightNumber = ? LIMIT 1), " +
                        "(SELECT SeatID FROM Seats WHERE SeatNumber = ? LIMIT 1), ?, ?, " +
                        "(SELECT Destination FROM Flights WHERE FlightNumber = ? LIMIT 1), ?, ?)";

                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setInt(1, userType.ordinal() + 1);
                    preparedStatement.setString(2, getUserEmail());
                    preparedStatement.setString(3, getUserName());
                    preparedStatement.setString(4, selectedFlight);
                    preparedStatement.setString(5, seatNumber);
                    preparedStatement.setString(6, seatType);
                    preparedStatement.setString(7, seatNumber);
                    preparedStatement.setString(8, selectedFlight);
                    preparedStatement.setBoolean(9, insuranceSelected);
                    preparedStatement.setDouble(10, seatPrice);
                    preparedStatement.executeUpdate();
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
        boolean useCompanionTicket = checkCompanionTicketUsage();
    
        if (useCompanionTicket) {
            // If a companion ticket is redeemed, allow the user to select a second seat
            String selectedSecondSeat = selectSecondSeat();
            if (selectedSecondSeat != null) {
                // Update the confirmation panel with the second seat
                updateConfirmationPanel(selectedSecondSeat);
    
                // Ask the user if they want ticket cancellation insurance for the second seat
                boolean insuranceSelectedSecondSeat = askForInsurance();
    
                // Calculate the total price (base price for two seats + insurance if selected)
                double totalPrice = seatPrice * 2.0;
                if (insuranceSelectedSecondSeat) {
                    totalPrice += 20.0;
                }
    
                // Open the payment frame for the second seat with the updated total price
                PaymentFrame paymentFrame = new PaymentFrame(this, selectedFlight, selectedSecondSeat, totalPrice, getUserEmail());
                paymentFrame.setVisible(true);
                dispose(); // Close the current frame
            } else {
                // User canceled the seat selection, do not proceed to payment
                return;
            }
        }
    
        // Ask the user if they want ticket cancellation insurance for the first seat
        boolean insuranceSelectedFirstSeat = askForInsurance();
    
        // Calculate the total price (base price for one seat + insurance if selected)
        double totalPrice = seatPrice;
        if (insuranceSelectedFirstSeat) {
            totalPrice += 20.0;
        }
    
        // Open the payment frame for the first seat with the updated total price
        PaymentFrame paymentFrame = new PaymentFrame(this, selectedFlight, seatNumber, totalPrice, getUserEmail());
        paymentFrame.setVisible(true);
        dispose(); // Close the current frame
    }
    
    // Method to ask the user if they want ticket cancellation insurance
    private boolean askForInsurance() {
        int option = JOptionPane.showConfirmDialog(this,
                "Do you want to purchase ticket cancellation insurance for an additional $20?",
                "Ticket Insurance",
                JOptionPane.YES_NO_OPTION);
    
        return option == JOptionPane.YES_OPTION;
    }
    

    private String selectSecondSeat() {
        SeatSelectionFrame seatSelectionFrame = new SeatSelectionFrame(selectedFlight, userType, databaseConnector);
        seatSelectionFrame.setVisible(true);
        // Wait for the seat selection frame to be closed
        return seatSelectionFrame.getSelectedSeat();
    }

    private void updateConfirmationPanel(String selectedSecondSeat) {
        // Update the confirmation panel to display the second seat
        // You might need to add additional labels or modify existing ones
        // based on your UI design.
        // For simplicity, I'm assuming you have a JLabel for the second seat.

        JLabel secondSeatLabel = new JLabel("Second Seat: " + selectedSecondSeat);

        confirmationPanel.add(secondSeatLabel);
        confirmationPanel.revalidate();
        confirmationPanel.repaint();
    }

    // Getter method for userType
    public UserType getUserType() {
        return userType;
    }

    // Getter method for databaseConnector
    public DatabaseConnector getDatabaseConnector() {
        return databaseConnector;
    }
    
}