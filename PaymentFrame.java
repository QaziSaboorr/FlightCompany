import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

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
        
        double paySeatPrice = seatPrice;
        double insSeatPrice = paySeatPrice + 20.0;
        // Display the payment amount from seatPrice
        if (ticketConfirmationFrame.isInsuranceSelected()) {
            paymentAmountLabel = new JLabel("$" + insSeatPrice );
            panel.add(paymentAmountLabel);

        }else{
            paymentAmountLabel = new JLabel("$" + paySeatPrice);
            panel.add(paymentAmountLabel);
        }

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

        // CVV label
        JLabel cvvLabel = new JLabel("CVV:");
        panel.add(cvvLabel);

        // Text field for expiry date input (adjust as needed)
        JTextField cvvField = new JTextField();
        panel.add(cvvField);

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
        // Send confirmation email
        // sendConfirmationEmail();
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

