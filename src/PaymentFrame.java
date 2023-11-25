package src;
import javax.swing.*;
import java.awt.*;

public class PaymentFrame extends JFrame {
    public PaymentFrame() {
        setTitle("Flight Reservation - Payment");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);

        // Implement your payment UI here
        // For simplicity, let's just add a label
        JLabel label = new JLabel("Credit Card Payment Form Here");
        add(label, BorderLayout.CENTER);
    }
}
