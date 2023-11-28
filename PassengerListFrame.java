
import javax.swing.*;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PassengerListFrame extends JFrame implements Loader{
    private JTextArea passengerListArea;
    private DatabaseConnector databaseConnector;
    private String selectedFlight;

    public PassengerListFrame(String selectedFlight, DatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;
        this.selectedFlight = selectedFlight;

        setTitle("Flight Reservation - Passenger List");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        // Text area to display the list of passengers
        passengerListArea = new JTextArea();
        passengerListArea.setEditable(false);

        // Scroll pane for the text area
        JScrollPane scrollPane = new JScrollPane(passengerListArea);

        // Create the form layout
        setLayout(new BorderLayout());

        add(new JLabel("List of Passengers for " + selectedFlight), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Load and display the list of passengers for the selected flight
        loadList();
    }

    // Function to load and display the list of passengers for the selected flight from the database
    @Override
    public void loadList() {
        try (Connection connection = databaseConnector.getConnection()) {
            String query = "SELECT * FROM Passengers WHERE FlightID = (SELECT FlightID FROM Flights WHERE FlightNumber = ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, selectedFlight);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    StringBuilder passengerList = new StringBuilder();
                    while (resultSet.next()) {
                        String passengerName = resultSet.getString("PassengerName");
                        // Adjust the column name based on your Passengers table structure
                        passengerList.append(passengerName).append("\n");
                    }
                    passengerListArea.setText(passengerList.toString());
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading passenger list.");
        }
    }
    

}