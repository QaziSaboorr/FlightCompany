
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
    private DatabaseConnector databaseConnector;

    private ManageController manageController;

    public ManageFlightsFrame(DatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;

        this.manageController = new ManageController(databaseConnector);

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
        loadDestinationNames();
        loadAircraftNumbers();

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
                    removeFlight(flightNumberToRemove);
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter a valid flight number.");
                }
            }
        });

    }

    // Function to load destination names into the combo box
    private void loadDestinationNames() {
        try (Connection connection = databaseConnector.getConnection()) {
            String query = "SELECT DestinationName FROM Destinations";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String destinationName = resultSet.getString("DestinationName");
                    destinationComboBox.addItem(destinationName);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading destination names.");
        }
    }

    // Function to load aircraft numbers into the combo box
    private void loadAircraftNumbers() {
        try (Connection connection = databaseConnector.getConnection()) {
            String query = "SELECT AircraftNumber FROM Aircrafts";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String aircraftNumber = resultSet.getString("AircraftNumber");
                    aircraftComboBox.addItem(aircraftNumber);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading aircraft numbers.");
        }
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
    
        try (Connection connection = databaseConnector.getConnection()) {
            // Get the AircraftID for the selected aircraft number
            int aircraftID = getAircraftID(connection, aircraftNumber);
            

            // Check if the flight already exists
            if (flightExists(connection, flightNumber)) {
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
                preparedStatement.setInt(4, getDestinationID(connection, destination));
                preparedStatement.executeUpdate();
    
                // Get the FlightID for the newly added flight
                int newFlightID = manageController.getFlightID(connection, flightNumber);
    
                // Add seats for the new flight
                addSeatsForFlight(connection, newFlightID);
    
                JOptionPane.showMessageDialog(this, "Flight added successfully.");
                flightNumberField.setText(""); // Clear the input fields after adding
                originField.setText("");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding flight.");
        }
    }
    
    // Function to check if a flight with the given flight number already exists
    private boolean flightExists(Connection connection, String flightNumber) throws SQLException {
        String query = "SELECT FlightID FROM Flights WHERE FlightNumber = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, flightNumber);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next(); // Returns true if a matching flight is found
            }
        }
    }
    

    // Function to get the DestinationID for a given destination name
    private int getDestinationID(Connection connection, String destinationName) throws SQLException {
        String query = "SELECT DestinationID FROM Destinations WHERE DestinationName = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, destinationName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("DestinationID");
                }
            }
        }
        return -1; // Return -1 if DestinationID is not found (should not happen in a well-formed database)
    }

    // Function to get the AircraftID for a given aircraft number
    private int getAircraftID(Connection connection, String aircraftNumber) throws SQLException {
        String query = "SELECT AircraftID FROM Aircrafts WHERE AircraftNumber = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, aircraftNumber);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("AircraftID");
                }
            }
        }
        return -1; // Return -1 if AircraftID is not found (should not happen in a well-formed database)
    }


    private void removeFlight(String flightNumber) {
        try (Connection connection = databaseConnector.getConnection()) {
            // Update the flight information in the Flights table and set the entire row to null
            String query = "UPDATE Flights SET FlightNumber = null, Origin = null, Destination = null, AircraftID = 0 WHERE FlightNumber = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, flightNumber);
                int rowsUpdated = preparedStatement.executeUpdate();
    
                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(this, "Flight information removed successfully.");
                } else {
                    JOptionPane.showMessageDialog(this, "Flight not found with the given flight number.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error removing flight information.");
        }
    }
    
    private void addSeatsForFlight(Connection connection, int flightID) throws SQLException {
        // Define the seat data
        String[] seatNumbers = {"A1", "A2", "A3", "A4", "B1", "B2", "B3", "B4", "C1", "C2", "C3", "C4", "D1", "D2", "D3", "D4"};
        String[] seatTypes = {"Business-Class", "Business-Class", "Business-Class", "Business-Class", "Business-Class", "Business-Class", "Business-Class", "Business-Class", "Regular", "Regular", "Regular", "Regular", "Regular", "Regular", "Regular", "Regular"};
        double[] seatPrices = {200.00, 200.00, 200.00, 200.00, 200.00, 200.00, 200.00, 200.00, 100.00, 100.00, 100.00, 100.00, 100.00, 100.00, 100.00, 100.00};

        // Insert seat data into the Seats table for the given flightID
        String query = "INSERT INTO Seats (FlightID, SeatNumber, SeatType, SeatPrice) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            for (int i = 0; i < seatNumbers.length; i++) {
                preparedStatement.setInt(1, flightID);
                preparedStatement.setString(2, seatNumbers[i]);
                preparedStatement.setString(3, seatTypes[i]);
                preparedStatement.setDouble(4, seatPrices[i]);
                preparedStatement.executeUpdate();
            }
        }
    }
}