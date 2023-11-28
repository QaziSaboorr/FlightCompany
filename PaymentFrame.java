import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

class PaymentFrame extends JFrame {
    private TicketConfirmationFrame ticketConfirmationFrame;
    private String selectedFlight;
    private String seatNumber;
    private double seatPrice;

    public PaymentFrame(TicketConfirmationFrame ticketConfirmationFrame, String selectedFlight, String seatNumber, double seatPrice) {
        this.ticketConfirmationFrame = ticketConfirmationFrame;
        this.selectedFlight = selectedFlight;
        this.seatNumber = seatNumber;
        this.seatPrice = seatPrice;

        setTitle("Flight Reservation - Payment");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setSize(400, 350); // Adjust the size as needed
        setLocationRelativeTo(null);

        // Implement your payment UI here
        JPanel panel = createPaymentPanel();
        add(panel, BorderLayout.CENTER);
    }

    private JLabel paymentAmountLabel;

    private JPanel createPaymentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 10, 10)); // Example layout, adjust as needed

        JLabel amountLabel = new JLabel("Payment Amount:");
        panel.add(amountLabel);

        // Display the payment amount from seatPrice
        paymentAmountLabel = new JLabel("$" + seatPrice);
        panel.add(paymentAmountLabel);

        // Credit card number label
        JLabel cardNumberLabel = new JLabel("Credit Card Number:");
        panel.add(cardNumberLabel);

        // Text field for credit card input (adjust as needed)
        JTextField cardNumberField = new JTextField();
        panel.add(cardNumberField);

        // Expiry date label
        JLabel expiryDateLabel = new JLabel("Expiry Date:");
        panel.add(expiryDateLabel);

        // Text field for expiry date input (adjust as needed)
        JTextField expiryDateField = new JTextField();
        panel.add(expiryDateField);

        // Confirm Purchase button
        JButton confirmPurchaseButton = new JButton("Confirm Purchase");
        confirmPurchaseButton.addActionListener(e -> confirmPurchase());
        panel.add(confirmPurchaseButton);

        return panel;
    }

    private void confirmPurchase() {
        // Perform payment processing logic if needed

        // Update Payments table
        updatePaymentsTable();

        // Call the confirmTicket method in the TicketConfirmationFrame
        ticketConfirmationFrame.confirmTicket();

        JOptionPane.showMessageDialog(this, "Purchase Confirmed!");
        dispose(); // Close the payment frame
    }



    private void updatePaymentsTable() {
        try (Connection connection = ticketConfirmationFrame.getDatabaseConnector().getConnection()) {
            String query = "INSERT INTO Payments (UserID, FlightID, PaymentAmount) " +
                    "VALUES (?, (SELECT FlightID FROM Flights WHERE FlightNumber = ? LIMIT 1), ?)";
    
            double finalSeatPrice = seatPrice;
    
            // Check if companion ticket is redeemed
            boolean useCompanionTicket = ticketConfirmationFrame.checkCompanionTicketUsage();
    
            // Check if cancellation insurance is selected and adjust the price
            if (ticketConfirmationFrame.isInsuranceSelected()) {
                finalSeatPrice += 20.0; // Assuming cancellation insurance adds $20
            }
    
            // If a companion ticket is redeemed, set PaymentAmount to 0
            if (useCompanionTicket) {
                finalSeatPrice = 0.0;
            }
    
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, ticketConfirmationFrame.getUserType().ordinal() + 1);
                preparedStatement.setString(2, selectedFlight);
                preparedStatement.setDouble(3, finalSeatPrice);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
}