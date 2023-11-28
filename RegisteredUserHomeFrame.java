

import javax.swing.*;
import java.util.List;

public class RegisteredUserHomeFrame extends JFrame {
    private DatabaseConnector databaseConnector;
    private String username;

    private UserController userController;

    public RegisteredUserHomeFrame(DatabaseConnector databaseConnector, String username) {
        this.databaseConnector = databaseConnector;
        this.username = username;

        userController = new UserController(databaseConnector);


        setTitle("Flight Reservation - Registered User");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));


        JButton flightSelectionButton = new JButton("Select Flights");
        JButton cancelFlightButton = new JButton("Cancel Flight");
        JButton applyForCreditCard = new JButton("Apply for Credit Card");
        JButton checkPromotions = new JButton("Check Promotions");

        add(flightSelectionButton);
        add(cancelFlightButton);
        add(applyForCreditCard);
        add(checkPromotions);

        flightSelectionButton.addActionListener(e -> userController.selectFlight());
        cancelFlightButton.addActionListener(e -> cancelFlight());
        applyForCreditCard.addActionListener(e -> applyForCreditCard());
        checkPromotions.addActionListener(e -> checkPromotions());

        setVisible(true);
    }


    private void applyForCreditCard() {
        userController.updateCreditCardStatus(databaseConnector, username, true);   
    }

    private void checkPromotions() {
        
    }
    
    private void cancelFlight() {
        // Retrieve the list of flights for the user
        List<String> userFlights = userController.getUserFlights(databaseConnector, username);

        // Show a dialog with the dropdown for the user to choose a flight
        int result = JOptionPane.showConfirmDialog(this, createFlightDropdown(userFlights), "Select Flight to Cancel", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            // Get the selected flight
            JComboBox<String> flightDropdown = (JComboBox<String>) ((JOptionPane) getRootPane().getParent()).getMessage();
            String selectedFlight = (String) flightDropdown.getSelectedItem();

            // Perform the cancellation (you need to implement this)
            userController.cancelSelectedFlight(selectedFlight, username);

            // Optionally, update the UI or show a confirmation message
            JOptionPane.showMessageDialog(this, "Flight canceled: " + selectedFlight, "Cancellation Confirmation", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private JPanel createFlightDropdown(List<String> userFlights) {
        JPanel panel = new JPanel();
        JComboBox<String> flightDropdown = new JComboBox<>();
        
        // Populate the dropdown with user's flights
        for (String flight : userFlights) {
            flightDropdown.addItem(flight);
        }

        panel.add(flightDropdown);
        return panel;
    }
}




