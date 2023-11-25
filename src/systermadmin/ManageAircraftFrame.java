package src.systermadmin;
import javax.swing.*;

import src.common.DatabaseConnector;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
        String aircraftNumber = aircraftNumberField.getText();

        if (aircraftNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Aircraft Number.");
            return;
        }

        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to add this aircraft?",
                "Confirm Addition",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmation == JOptionPane.YES_OPTION) {
            try (Connection connection = databaseConnector.getConnection()) {
                // Insert the aircraft information into the Aircrafts table
                String query = "INSERT INTO Aircrafts (AircraftNumber) VALUES (?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, aircraftNumber);
                    preparedStatement.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Aircraft added successfully.");
                    aircraftNumberField.setText("");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding aircraft.");
            }
        }
    }

    private void removeAircraft() {
        String selectedAircraft = (String) aircraftDropdown.getSelectedItem();

        if (selectedAircraft == null) {
            JOptionPane.showMessageDialog(this, "Please select an aircraft to remove.");
            return;
        }

        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to remove this aircraft?",
                "Confirm Removal",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmation == JOptionPane.YES_OPTION) {
            try (Connection connection = databaseConnector.getConnection()) {
                // Split the selectedAircraft string to get aircraft number
                String[] parts = selectedAircraft.split(" - ");
                String aircraftNumber = parts[0];

                // Update the flight information in the Flights table (set FlightNumber to null)
                String flightQuery = "UPDATE Flights SET FlightNumber = null WHERE AircraftID IN (SELECT AircraftID FROM Aircrafts WHERE AircraftNumber = ?)";
                try (PreparedStatement flightStatement = connection.prepareStatement(flightQuery)) {
                    flightStatement.setString(1, aircraftNumber);
                    flightStatement.executeUpdate();
                }

                // Delete the aircraft based on aircraft number from Aircrafts table
                String aircraftQuery = "DELETE FROM Aircrafts WHERE AircraftNumber = ?";
                try (PreparedStatement aircraftStatement = connection.prepareStatement(aircraftQuery)) {
                    aircraftStatement.setString(1, aircraftNumber);
                    aircraftStatement.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Aircraft removed successfully.");
                    aircraftDropdown.removeAllItems();
                    loadAircraftAndFlights();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error removing aircraft.");
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
