
import javax.swing.*;
import java.awt.*;


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
        dispose(); // Close the payment frame
    }
}