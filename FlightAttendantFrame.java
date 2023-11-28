import javax.swing.*;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
public class FlightAttendantFrame extends JFrame {
    private JComboBox<String> flightComboBox;
    private JButton viewPassengerListButton;
    private DatabaseConnector databaseConnector;

    public FlightAttendantFrame(DatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;

        setTitle("Flight Reservation - Flight Attendant");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 150);
        setLocationRelativeTo(null);

        flightComboBox = new JComboBox<>();
        viewPassengerListButton = new JButton("View Passenger List");

        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        add(new JLabel("Welcome, Flight Attendant!"));
        add(new JLabel("Select Flight:"));
        add(flightComboBox);
        add(viewPassengerListButton);

        loadFlights();

        viewPassengerListButton.addActionListener(e -> {
            String selectedFlightInfo = (String) flightComboBox.getSelectedItem();
            String selectedFlightNumber = extractFlightNumber(selectedFlightInfo);

            // Open the PassengerListFrame for the selected flight
            new PassengerListFrame(selectedFlightNumber, databaseConnector).setVisible(true);
        });

        setVisible(true);
    }

    private void loadFlights() {
        try (Connection connection = databaseConnector.getConnection()) {
            String query = "SELECT FlightNumber, Origin, Destination FROM Flights";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String flightInfo = resultSet.getString("FlightNumber") + " - " +
                            resultSet.getString("Origin") + " to " +
                            resultSet.getString("Destination");
                    flightComboBox.addItem(flightInfo);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private String extractFlightNumber(String flightInfo) {
        int endIndex = flightInfo.indexOf(" -");
        if (endIndex != -1) {
            return flightInfo.substring(0, endIndex);
        } else {
            return flightInfo;
        }
    }


}
