

import javax.swing.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class FlightSelectionFrame extends JFrame implements ListLoader{
    private JComboBox<String> flightComboBox;
    private JButton selectFlightButton;
    private JButton showPassengerListButton;
    private UserType userType;
    private DatabaseConnector databaseConnector;

    private FlightSelectionController flightSelectionController;

    private ItemLoader itemLoader;

    public FlightSelectionFrame(UserType userType, DatabaseConnector databaseConnector) {
        this.userType = userType;
        this.databaseConnector = databaseConnector;

        flightSelectionController = new FlightSelectionController(databaseConnector);

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

        // loadList();

        // selectFlightButton.addActionListener(e -> {
        //     String selectedFlightInfo = (String) flightComboBox.getSelectedItem();
        //     String selectedFlightNumber = extractFlightNumber(selectedFlightInfo);

        //     new SeatSelectionFrame(selectedFlightNumber, userType, databaseConnector).setVisible(true);
        //     this.dispose();
        // });

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
        List<Item> flightList = itemLoader.loadFlights();
        displayItems(flightList);
    }


    public void displayItems(List<Item> items) {
        for (Item item : items) {
            flightComboBox.addItem(item.getText());
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



