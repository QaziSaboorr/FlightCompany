package presentation;
import datasource.DatabaseConnector;

// ManageCrewFrame.java
import javax.swing.*;

import controllers.ManageController;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ManageCrewFrame extends JFrame {
    private JTextField crewNameField;
    private JComboBox<String> flightComboBox;
    private JButton addButton;
    private JButton removeButton; 
    private DatabaseConnector databaseConnector;
    private JComboBox<String> crewDropdown; 

    private ManageController manageController;

    public ManageCrewFrame() {
        this.databaseConnector = DatabaseConnector.getInstance();

        this.manageController = new ManageController();

        setTitle("Flight Reservation - Manage Crew");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);

        // Form components
        crewNameField = new JTextField();
        flightComboBox = new JComboBox<>();
        addButton = new JButton("Add Crew");
        removeButton = new JButton("Remove Crew");

        // Added crew dropdown
        crewDropdown = new JComboBox<>();

        // Load flight numbers and crews into the combo boxes
        manageController.loadFlightNumbers(flightComboBox);
        manageController.loadCrewsAndFlights(crewDropdown);

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
            int flightID = manageController.getFlightID(connection, flightNumber);

            // Check if there is an existing crew for the selected flight
            String existingCrew = manageController.getExistingCrew(connection, flightID);
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
                crewDropdown.removeAllItems();
                manageController.loadCrewsAndFlights(crewDropdown);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding crew.");
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
                        crewDropdown.removeAllItems();
                        manageController.loadCrewsAndFlights(crewDropdown);
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
}
