// ManageCrewFrame.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ManageCrewFrame extends JFrame {
    private JTextField crewNameField;
    private JComboBox<String> flightComboBox;
    private JButton addButton;
    private DatabaseConnector databaseConnector;

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

        // Load flight numbers into the combo box
        loadFlightNumbers();

        // Create the form layout
        setLayout(new GridLayout(4, 2));

        add(new JLabel("Crew Name:"));
        add(crewNameField);
        add(new JLabel("Flight Number:"));
        add(flightComboBox);
        add(new JLabel());
        add(addButton);

        // Add action listener for the "Add Crew" button
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addCrew();
            }
        });
    }

    // Function to load flight numbers into the combo box
    private void loadFlightNumbers() {
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

            // Insert the crew information into the Crews table
            String query = "INSERT INTO Crews (Name, FlightID) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, crewName);
                preparedStatement.setInt(2, flightID);
                preparedStatement.executeUpdate();
                JOptionPane.showMessageDialog(this, "Crew added successfully.");
                crewNameField.setText(""); // Clear the input field after adding
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding crew.");
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
