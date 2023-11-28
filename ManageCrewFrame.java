
// ManageCrewFrame.java
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



public class ManageCrewFrame extends JFrame implements Loader {
    private JTextField crewNameField;
    private JComboBox<String> flightComboBox;
    private JButton addButton;
    private JButton removeButton; // Added Remove Crew button
    private DatabaseConnector databaseConnector;
    private JComboBox<String> crewDropdown; // Added crew dropdown

    public ManageCrewFrame(DatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;

        setTitle("Flight Reservation - Manage Crew");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);

        // Form components
        crewNameField = new JTextField();
        flightComboBox = new JComboBox<>();
        addButton = new JButton("Add Crew");
        removeButton = new JButton("Remove Crew");

        // Added crew and flight dropdowns
        crewDropdown = new JComboBox<>();


        // Load flight numbers and crews into the combo boxes
        loadList();
        loadCrewsAndFlights();

        // Create the form layout
        setLayout(new GridLayout(5, 2));

        add(new JLabel("Crew Name:"));
        add(crewNameField);
        add(new JLabel("Flight Number:"));
        add(flightComboBox);
        add(new JLabel());
        add(addButton);
        add(new JLabel("Remove Crew:"));
        add(crewDropdown);
        add(new JLabel());
        add(removeButton);

        // Add action listener for the "Add Crew" button
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addCrew();
            }
        });

        // Add action listener for the "Remove Crew" button
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeCrew();
            }
        });

            // Add action listener for the "Remove Crew" button
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeCrew();
            }
         });
    }

    // Function to get the existing crew for a given flight ID
    private String getExistingCrew(Connection connection, int flightID) throws SQLException {
        String query = "SELECT Name FROM Crews WHERE FlightID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, flightID);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("Name");
                }
            }
        }
        return null; // Return null if there is no existing crew
    }   

    // Function to load flight numbers into the combo box
    @Override
    public void loadList() {
        try (Connection connection = databaseConnector.getConnection()) {
            String query = "SELECT FlightNumber FROM Flights";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String flightNumber = resultSet.getString("FlightNumber");
                    flightComboBox.addItem(flightNumber);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading flight numbers.");
        }
    }

    // Function to add a new crew to the database
    private void addCrew() {
        String crewName = crewNameField.getText();
        String flightNumber = (String) flightComboBox.getSelectedItem();

        if (crewName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a crew name.");
            return;
        }

        try (Connection connection = databaseConnector.getConnection()) {
            // Get the FlightID for the selected flight number
            int flightID = getFlightID(connection, flightNumber);

            // Check if there is an existing crew for the selected flight
            String existingCrew = getExistingCrew(connection, flightID);
            if (existingCrew != null) {
                int confirmation = JOptionPane.showConfirmDialog(
                        this,
                        "There is already an existing crew for this flight: " + existingCrew +
                                "\nDo you want to remove the existing crew and add the new one?",
                        "Confirm Removal",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirmation == JOptionPane.YES_OPTION) {
                    // Remove the existing crew
                    removeCrew();
                } else {
                    return; // If the user chooses not to remove the existing crew, do not proceed with adding
                }
            }

            // Insert the crew information into the Crews table
            String query = "INSERT INTO Crews (Name, FlightID) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, crewName);
                preparedStatement.setInt(2, flightID);
                preparedStatement.executeUpdate();
                JOptionPane.showMessageDialog(this, "Crew added successfully.");
                crewNameField.setText(""); // Clear the input field after adding
                // Reload the crews and flights into the dropdown for an updated view
                crewDropdown.removeAllItems();
                loadCrewsAndFlights();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding crew.");
        }
    }


    // Function to load crews and their associated flights into the dropdown menu
    private void loadCrewsAndFlights() {
        try (Connection connection = databaseConnector.getConnection()) {
            String query = "SELECT c.CrewID, c.Name, f.FlightNumber " +
                        "FROM Crews c " +
                        "LEFT JOIN Flights f ON c.FlightID = f.FlightID";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String crewInfo = resultSet.getString("Name") + " - " +
                                    (resultSet.getString("FlightNumber") != null ?
                                            resultSet.getString("FlightNumber") : "No Flight");
                    crewDropdown.addItem(crewInfo);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading crews and flights.");
        }
    }



    // Function to remove a crew from the database
    private void removeCrew() {
        String selectedCrewInfo = (String) crewDropdown.getSelectedItem();
        if (selectedCrewInfo == null || selectedCrewInfo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a crew to remove.");
            return;
        }

        String[] parts = selectedCrewInfo.split(" - ");
        String crewName = parts[0];

        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to remove this crew?",
                "Confirm Removal",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmation == JOptionPane.YES_OPTION) {
            try (Connection connection = databaseConnector.getConnection()) {
                String query = "DELETE FROM Crews WHERE Name = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, crewName);
                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Crew removed successfully.");
                        crewNameField.setText(""); // Clear the input field after removal
                        crewDropdown.removeItem(selectedCrewInfo); // Remove the crew from the dropdown
                        // Reload the crews and flights into the dropdown for an updated view
                        crewDropdown.removeAllItems();
                        loadCrewsAndFlights();
                    } else {
                        JOptionPane.showMessageDialog(this, "Crew not found.");
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error removing crew.");
            }
        }
    }





    // Function to get the FlightID for a given flight number
    private int getFlightID(Connection connection, String flightNumber) throws SQLException {
        String query = "SELECT FlightID FROM Flights WHERE FlightNumber = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, flightNumber);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("FlightID");
                }
            }
        }
        return -1; // Return -1 if FlightID is not found (should not happen in a well-formed database)
    }
}