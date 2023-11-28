import javax.swing.*;

import com.mysql.cj.protocol.Message;

import java.awt.*;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;


class PaymentFrame extends JFrame {
    private TicketConfirmationFrame ticketConfirmationFrame;
    private String selectedFlight;
    private String seatNumber;
    private double seatPrice;
    private String userEmail; // Add this variable

    public PaymentFrame(TicketConfirmationFrame ticketConfirmationFrame, String selectedFlight, String seatNumber, double seatPrice, String userEmail) {
        this.ticketConfirmationFrame = ticketConfirmationFrame;
        this.selectedFlight = selectedFlight;
        this.seatNumber = seatNumber;
        this.seatPrice = seatPrice;
        this.userEmail = userEmail; // Set the user email

        setTitle("Flight Reservation - Payment");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setSize(400, 250); // Adjust the size as needed
        setLocationRelativeTo(null);

        // Implement your payment UI here
        JPanel panel = createPaymentPanel();
        add(panel, BorderLayout.CENTER);
    }

    private JPanel createPaymentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 10, 10)); // Example layout, adjust as needed
    
        // Payment amount label
        JLabel amountLabel = new JLabel("Payment Amount:");
        panel.add(amountLabel);
    
        // Display the payment amount (adjust as needed)
        JLabel paymentAmountLabel = new JLabel("$" + seatPrice);
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

        // Call the confirmTicket method in the TicketConfirmationFrame
        ticketConfirmationFrame.confirmTicket();

        // Additional logic for confirming purchase
        JOptionPane.showMessageDialog(this, "Purchase Confirmed!");
        updatePaymentsTable();
        // Send email to the user
        // sendEmailToUser();

        dispose(); // Close the payment frame
    }

 

        private void updatePaymentsTable() {
        try (Connection connection = ticketConfirmationFrame.getDatabaseConnector().getConnection()) {
            String query = "INSERT INTO Payments (UserID, FlightID, PaymentAmount) " +
                    "VALUES (?, (SELECT FlightID FROM Flights WHERE FlightNumber = ? LIMIT 1), ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, ticketConfirmationFrame.getUserType().ordinal() + 1);
                preparedStatement.setString(2, selectedFlight);
                preparedStatement.setDouble(3, seatPrice);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}