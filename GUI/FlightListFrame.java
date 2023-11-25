// FlightListFrame.java
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FlightListFrame extends JFrame {
    private JTextArea flightListArea;
    private DatabaseConnector databaseConnector;

    public FlightListFrame(DatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;

        setTitle("Flight Reservation - Flight List");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        // Text area to display the list of flights
        flightListArea = new JTextArea();
        flightListArea.setEditable(false);

        // Scroll pane for the text area
        JScrollPane scrollPane = new JScrollPane(flightListArea);

        // Create the form layout
        setLayout(new BorderLayout());

        add(new JLabel("List of Flights"), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Load and display the list of flights
        loadFlights();
    }

    // Function to load and display the list of flights from the database
    private void loadFlights() {
        try (Connection connection = databaseConnector.getConnection()) {
            String query = "SELECT FlightNumber, Origin, Destination FROM Flights";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                StringBuilder flightList = new StringBuilder();
                while (resultSet.next()) {
                    String flightInfo = resultSet.getString("FlightNumber") + " - " +
                            resultSet.getString("Origin") + " to " +
                            resultSet.getString("Destination");
                    flightList.append(flightInfo).append("\n");
                }
                flightListArea.setText(flightList.toString());
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading flight list.");
        }
    }
}
