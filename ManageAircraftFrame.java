import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ManageAircraftFrame extends JFrame {
    private JTextField aircraftNumberField;
    private JComboBox<String> aircraftDropdown;
    private JButton addButton;
    private JButton removeButton;
    private DatabaseConnector databaseConnector;

    public ManageAircraftFrame(DatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;

        setTitle("Flight Reservation - Manage Aircraft");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);

        aircraftNumberField = new JTextField();
        aircraftDropdown = new JComboBox<>();
        addButton = new JButton("Add Aircraft");
        removeButton = new JButton("Remove Aircraft");

        setLayout(new GridLayout(4, 2));

        add(new JLabel("Aircraft Number:"));
        add(aircraftNumberField);
        add(new JLabel("Select Aircraft:"));
        add(aircraftDropdown);
        add(addButton);
        add(removeButton);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addAircraft();
            }
        });

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeAircraft();
            }
        });

        loadAircraftAndFlights();
    }

private void addAircraft() {
    String aircraftNumber = aircraftNumberField.getText().trim();

    if (aircraftNumber.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please enter Aircraft Number.");
        return;
    }

    int confirmation = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to add this aircraft: " + aircraftNumber + "?",
            "Confirm Addition",
            JOptionPane.YES_NO_OPTION
    );

    if (confirmation == JOptionPane.YES_OPTION) {
        try (Connection connection = databaseConnector.getConnection()) {
            String query = "INSERT INTO Aircrafts (AircraftNumber) VALUES (?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, aircraftNumber);
                preparedStatement.executeUpdate();

                JOptionPane.showMessageDialog(this, "Aircraft added successfully.");
                aircraftNumberField.setText("");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error adding aircraft: " + ex.getMessage());
                ex.printStackTrace();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database connection error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
private void removeAircraft() {
        String selectedAircraftModel = (String) aircraftDropdown.getSelectedItem();

        if (selectedAircraftModel == null) {
            JOptionPane.showMessageDialog(this, "Please select an aircraft model to remove.");
            return;
        }

        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to remove all aircrafts of model '" + selectedAircraftModel + "' and their associated flights?",
                "Confirm Removal",
                JOptionPane.YES_NO_OPTION
        );

        Connection connection = null;
        try {
            connection = databaseConnector.getConnection();
            // Start a transaction
            connection.setAutoCommit(false);

            // Retrieve all AircraftIDs for the selected aircraft model
            String aircraftIdQuery = "SELECT AircraftID FROM Aircrafts WHERE AircraftNumber LIKE ?";
            ArrayList<Integer> aircraftIds = new ArrayList<>();
            try (PreparedStatement aircraftIdStatement = connection.prepareStatement(aircraftIdQuery)) {
                aircraftIdStatement.setString(1, selectedAircraftModel + "%");
                try (ResultSet resultSet = aircraftIdStatement.executeQuery()) {
                    while (resultSet.next()) {
                        aircraftIds.add(resultSet.getInt("AircraftID"));
                    }
                }
            }

            // Delete flights associated with these aircraft IDs
            String deleteFlightsQuery = "DELETE FROM Flights WHERE AircraftID = ?";
            try (PreparedStatement deleteFlightsStatement = connection.prepareStatement(deleteFlightsQuery)) {
                for (Integer aircraftId : aircraftIds) {
                    deleteFlightsStatement.setInt(1, aircraftId);
                    deleteFlightsStatement.executeUpdate();
                }
            }

            // Delete the aircraft themselves
            String deleteAircraftQuery = "DELETE FROM Aircrafts WHERE AircraftNumber LIKE ?";
            try (PreparedStatement deleteAircraftStatement = connection.prepareStatement(deleteAircraftQuery)) {
                deleteAircraftStatement.setString(1, selectedAircraftModel + "%");
                deleteAircraftStatement.executeUpdate();
            }

            // Commit the transaction
            connection.commit();

            JOptionPane.showMessageDialog(this, "All aircrafts of model '" + selectedAircraftModel + "' and their associated flights have been removed.");
            aircraftDropdown.removeAllItems();
            loadAircraftAndFlights();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error removing aircrafts: " + ex.getMessage());
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
    }


    private void loadAircraftAndFlights() {
        try (Connection connection = databaseConnector.getConnection()) {
            String query = "SELECT a.AircraftNumber, f.FlightNumber " +
                    "FROM Aircrafts a " +
                    "LEFT JOIN Flights f ON a.AircraftID = f.AircraftID";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String aircraftNumber = resultSet.getString("AircraftNumber");
                    String flightNumber = resultSet.getString("FlightNumber");
                    String aircraftInfo = aircraftNumber + " - " + (flightNumber != null ? flightNumber : "No Flight");
                    aircraftDropdown.addItem(aircraftInfo);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading aircraft and flight information.");
        }
    }
}