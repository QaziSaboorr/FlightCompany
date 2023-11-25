import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SeatSelectionFrame extends JFrame {
    private String selectedFlight;
    private UserType userType;
    private DatabaseConnector databaseConnector;

    public SeatSelectionFrame(String selectedFlight, UserType userType, DatabaseConnector databaseConnector) {
        this.selectedFlight = selectedFlight;
        this.userType = userType;
        this.databaseConnector = databaseConnector;

        setTitle("Flight Reservation - Seat Selection");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        // Create seat selection panel
        JPanel seatSelectionPanel = createSeatSelectionPanel();
        add(seatSelectionPanel, BorderLayout.CENTER);
    }

    private JPanel createSeatSelectionPanel() {
        System.out.println("Creating seat selection panel...");
        JPanel seatSelectionPanel = new JPanel();
        seatSelectionPanel.setLayout(new GridLayout(4, 4));

        try {
            // Use the shared database connector
            try (Connection connection = databaseConnector.getConnection()) {
                // Query to get the flight ID from the flight number
                String flightIdQuery = "SELECT FlightID FROM Flights WHERE FlightNumber = ?";
                try (PreparedStatement flightIdStatement = connection.prepareStatement(flightIdQuery)) {
                    flightIdStatement.setString(1, selectedFlight);

                    try (ResultSet flightIdResult = flightIdStatement.executeQuery()) {
                        if (flightIdResult.next()) {
                            int flightId = flightIdResult.getInt("FlightID");

                            // Query to get seats based on the flight ID
                            String seatsQuery = "SELECT * FROM Seats WHERE FlightID = ?";
                            try (PreparedStatement seatsStatement = connection.prepareStatement(seatsQuery)) {
                                seatsStatement.setInt(1, flightId);

                                try (ResultSet resultSet = seatsStatement.executeQuery()) {
                                    while (resultSet.next()) {
                                        String seatNumber = resultSet.getString("SeatNumber");
                                        String seatType = resultSet.getString("SeatType");
                                        double seatPrice = calculateTicketPrice(seatType);

                                        // Create seat labels or buttons dynamically based on seat information
                                        JButton seatButton = new JButton("Seat " + seatNumber + " (" + seatType + ") - $" + seatPrice);
                                        seatButton.addActionListener(e -> {
                                            // Handle seat selection (e.g., confirm ticket)
                                            handleSeatSelection(selectedFlight, seatNumber, seatType, seatPrice);
                                        });
                                        seatSelectionPanel.add(seatButton);

                                        System.out.println("Added seat: " + seatNumber + " (" + seatType + ")");
                                    }
                                }
                            }
                        } else {
                            System.err.println("Flight ID not found for the selected flight.");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error retrieving seat information from the database.");
        }

        System.out.println("Seat selection panel created.");
        return seatSelectionPanel;
    }
    // Existing code...

    private void handleSeatSelection(String selectedFlight, String seatNumber, String seatType, double seatPrice) {
        // Prompt user for ticket cancellation insurance
        int option = JOptionPane.showConfirmDialog(this,
                "Do you want to purchase ticket cancellation insurance for an additional $20?",
                "Ticket Insurance",
                JOptionPane.YES_NO_OPTION);
    
        boolean insuranceSelected = (option == JOptionPane.YES_OPTION);
    
        // Open TicketConfirmationFrame with relevant information
        new TicketConfirmationFrame(userType, selectedFlight, seatNumber, seatType, seatPrice, insuranceSelected, databaseConnector).setVisible(true);
    
        // Dispose of the current frame
        this.dispose();
    }
    


    private double calculateTicketPrice(String seatType) {
        // Add your pricing logic based on seat type
        // Adjust this method accordingly
        double basePrice = 100.0; // Default base price
        switch (seatType) {
            case "Regular":
                return basePrice;
            case "Business-Class":
                return basePrice * 2.0; // Double the price of regular
            default:
                return basePrice;
        }
    }
}
