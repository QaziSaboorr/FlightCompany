
// AircraftListFrame.java
import javax.swing.*;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AircraftListFrame extends JFrame {
    private JTextArea aircraftListArea;
    private DatabaseConnector databaseConnector;

    private DatabaseController databaseController;

    public AircraftListFrame(DatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;

        databaseController = new DatabaseController(databaseConnector);

        setTitle("Flight Reservation - Aircraft List");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        // Text area to display the list of aircrafts
        aircraftListArea = new JTextArea();
        aircraftListArea.setEditable(false);

        // Scroll pane for the text area
        JScrollPane scrollPane = new JScrollPane(aircraftListArea);

        // Create the form layout
        setLayout(new BorderLayout());

        add(new JLabel("List of Aircrafts"), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Load and display the list of aircrafts
        loadAircrafts();
    }

    // Function to load and display the list of aircrafts from the database
    private void loadAircrafts() {
        try (Connection connection = databaseConnector.getConnection()) {
            String query = "SELECT AircraftNumber FROM Aircrafts";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                StringBuilder aircraftList = new StringBuilder();
                while (resultSet.next()) {
                    String aircraftInfo = resultSet.getString("AircraftNumber");
                    aircraftList.append(aircraftInfo).append("\n");
                }
                aircraftListArea.setText(aircraftList.toString());
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading aircraft list.");
        }
    }
}