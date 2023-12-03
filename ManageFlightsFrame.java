
// ManageFlightsFrame.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ManageFlightsFrame extends JFrame {
    private JTextField flightNumberField;
    private JTextField originField;
    private JComboBox<String> destinationComboBox;
    private JComboBox<String> aircraftComboBox;
    private JButton addButton;
    private JButton removeButton;

    private ManageController manageController;

    public ManageFlightsFrame() {

        this.manageController = new ManageController();

        setTitle("Flight Reservation - Manage Flights");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(null);

        // Form components
        flightNumberField = new JTextField();
        originField = new JTextField();
        destinationComboBox = new JComboBox<>();
        aircraftComboBox = new JComboBox<>();
        addButton = new JButton("Add Flight");
        removeButton = new JButton("Remove Flight");

        // Load destination names and aircraft numbers into the combo boxes
        manageController.loadDestinationNames(destinationComboBox);
        manageController.loadAircraftNumbers(aircraftComboBox);

        // Create the form layout
        setLayout(new GridLayout(6, 2));

        add(new JLabel("Flight Number:"));
        add(flightNumberField);
        add(new JLabel("Origin:"));
        add(originField);
        add(new JLabel("Destination:"));
        add(destinationComboBox);
        add(new JLabel("Aircraft Number:"));
        add(aircraftComboBox);
        add(new JLabel());
        add(addButton);
        add(new JLabel());
        add(removeButton);

        // Add action listener for the "Add Flight" button
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addFlight();
            }
        });

        // Add action listener for the "Remove Flight" button
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String flightNumberToRemove = JOptionPane.showInputDialog("Enter Flight Number to Remove:");
                if (flightNumberToRemove != null && !flightNumberToRemove.isEmpty()) {
                    manageController.removeFlight(flightNumberToRemove);
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter a valid flight number.");
                }
            }
        });

    }
    private void addFlight() {
        String flightNumber = flightNumberField.getText();
        String origin = originField.getText();
        String destination = (String) destinationComboBox.getSelectedItem();
        String aircraftNumber = (String) aircraftComboBox.getSelectedItem();
    
        if (flightNumber.isEmpty() || origin.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter flight number and origin.");
            return;
        }
    
        try (Connection connection = DatabaseConnector.getInstance().getConnection()) {
            // Get the AircraftID for the selected aircraft number
            int aircraftID = manageController.getAircraftID(connection, aircraftNumber);
            

            // Check if the flight already exists
            if (manageController.flightExists(connection, flightNumber)) {
                JOptionPane.showMessageDialog(this, "Flight with the given flight number already exists.");
                return;
            }
    
            // Insert the flight information into the Flights table with a JOIN to get DestinationName
            String query = "INSERT INTO Flights (FlightNumber, Origin, Destination, AircraftID) " +
                    "SELECT ?, ?, Destinations.DestinationName, ? " +
                    "FROM Destinations " +
                    "WHERE Destinations.DestinationID = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, flightNumber);
                preparedStatement.setString(2, origin);
                preparedStatement.setInt(3, aircraftID);
                preparedStatement.setInt(4, manageController.getDestinationID(connection, destination));
                preparedStatement.executeUpdate();
    
                // Get the FlightID for the newly added flight
                int newFlightID = manageController.getFlightID(connection, flightNumber);
    
                // Add seats for the new flight
                manageController.addSeatsForFlight(connection, newFlightID);
    
                JOptionPane.showMessageDialog(this, "Flight added successfully.");
                flightNumberField.setText(""); // Clear the input fields after adding
                originField.setText("");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding flight.");
        }
    }
}