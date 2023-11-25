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
    private DatabaseConnector databaseConnector;

    public ManageFlightsFrame(DatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;

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

        // Add action listener for the "Add Flight" button
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addFlight();
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

    // Function to add a new flight to the database
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
            // Get the DestinationID for the selected destination
            int destinationID = getDestinationID(connection, destination);

            // Get the AircraftID for the selected aircraft number
            int aircraftID = getAircraftID(connection, aircraftNumber);

            // Insert the flight information into the Flights table
            String query = "INSERT INTO Flights (FlightNumber, Origin, Destination, AircraftID) VALUES (?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, flightNumber);
                preparedStatement.setString(2, origin);
                preparedStatement.setInt(3, destinationID);
                preparedStatement.setInt(4, aircraftID);
                preparedStatement.executeUpdate();
                JOptionPane.showMessageDialog(this, "Flight added successfully.");
                flightNumberField.setText(""); // Clear the input fields after adding
                originField.setText("");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding flight.");
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
}