import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FlightSelectionFrame extends JFrame implements ListLoader {
    private JComboBox<String> flightComboBox;
    private JButton selectFlightButton;
    private JButton showPassengerListButton;  // New button for showing passenger list
    private UserType userType;
    private DatabaseConnector databaseConnector;

    public FlightSelectionFrame(UserType userType, DatabaseConnector databaseConnector) {
        this.userType = userType;
        this.databaseConnector = databaseConnector;

        setTitle("Flight Reservation - Flight Selection");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 150);
        setLocationRelativeTo(null);

        flightComboBox = new JComboBox<>();
        selectFlightButton = new JButton("Select Flight");
        showPassengerListButton = new JButton("Show Passenger List");  // Initialize the button

        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        add(new JLabel("Welcome, " + userType.name() + "!"));
        add(new JLabel("Select Flight:"));
        add(flightComboBox);
        add(selectFlightButton);

        // Add the button only if the user is an airline agent or flight attendant
        if (userType == UserType.AirlineAgent) {
            add(showPassengerListButton);
            showPassengerListButton.addActionListener(e -> openPassengerListFrame());
        }

        loadList();

        selectFlightButton.addActionListener(e -> {
            String selectedFlightInfo = (String) flightComboBox.getSelectedItem();
            String selectedFlightNumber = extractFlightNumber(selectedFlightInfo);

            new SeatSelectionFrame(selectedFlightNumber, userType, databaseConnector).setVisible(true);
            this.dispose();
        });

        // Show passenger list button
        if (userType == UserType.AirlineAgent || userType == UserType.FlightAttendant) {
            add(showPassengerListButton);
            showPassengerListButton.addActionListener(e -> openPassengerListFrame());
        }

        setVisible(true);
    }

    @Override
    public void loadList() {
        try (Connection connection = databaseConnector.getConnection()) {
            String query = "SELECT FlightNumber, Origin, Destination FROM Flights WHERE FlightNumber IS NOT NULL";
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

    private void openPassengerListFrame() {
        String selectedFlightInfo = (String) flightComboBox.getSelectedItem();
        String selectedFlightNumber = extractFlightNumber(selectedFlightInfo);
        new PassengerListFrame(selectedFlightNumber, databaseConnector).setVisible(true);
    }
}
