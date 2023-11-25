import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
    
        setSize(400, 200);
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
        panel.add(new JLabel("Price: $" + seatPrice));
        panel.add(new JLabel("Insurance Selected: " + (insuranceSelected ? "Yes" : "No")));

        JButton confirmTicketButton = new JButton("Confirm Ticket");
        confirmTicketButton.addActionListener(e -> confirmTicket());
        panel.add(confirmTicketButton);

        return panel;
    }

    private void confirmTicket() {
        // Logic for confirming the ticket and updating the database
        try {
            // Use the shared database connector
            try (Connection connection = databaseConnector.getConnection()) {
                String query = "INSERT INTO Tickets (UserID, FlightID, SeatID, SeatType, SeatNumber, Destination, PaymentAmount) " +
                "VALUES (?, (SELECT FlightID FROM Flights WHERE FlightNumber = ? LIMIT 1), " +
                "(SELECT SeatID FROM Seats WHERE SeatNumber = ? LIMIT 1), ?, ?, " +
                "(SELECT Destination FROM Flights WHERE FlightNumber = ? LIMIT 1), ?)";

                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setInt(1, userType.ordinal() + 1);
                    preparedStatement.setString(2, selectedFlight);
                    preparedStatement.setString(3, seatNumber);
                    preparedStatement.setString(4, seatType);
                    preparedStatement.setString(5, seatNumber);
                    preparedStatement.setString(6, selectedFlight);
                    preparedStatement.setDouble(7, seatPrice);
                    preparedStatement.setBoolean(8, insuranceSelected);
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
