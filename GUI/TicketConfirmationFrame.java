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

        JButton confirmTicketButton = new JButton("Confirm Ticket");
        confirmTicketButton.addActionListener(e -> confirmTicket());
        panel.add(confirmTicketButton);

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
        // Implement logic to retrieve user name from the database based on user type
        try (Connection connection = databaseConnector.getConnection()) {
            String query = "SELECT UserName FROM Users WHERE UserID = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, userType.ordinal() + 1);
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
        // Implement logic to retrieve user email from the database based on user type
        try (Connection connection = databaseConnector.getConnection()) {
            String query = "SELECT Email FROM Users WHERE UserID = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, userType.ordinal() + 1);
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

    private void confirmTicket() {
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
}
