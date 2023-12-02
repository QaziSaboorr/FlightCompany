import javax.swing.*;
import java.util.List;

public class FlightAttendantFrame extends JFrame implements ListLoader, Printer {
    private JComboBox<String> flightComboBox;
    private JButton viewPassengerListButton;

    private ItemLoader itemLoader;
    private FlightController flightController;

    public FlightAttendantFrame(DatabaseConnector databaseConnector) {
        this.itemLoader = new ItemLoader(databaseConnector);

        this.flightController = new FlightController(databaseConnector);

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

        loadList();

        viewPassengerListButton.addActionListener(e -> {
            String selectedFlightInfo = (String) flightComboBox.getSelectedItem();
            String selectedFlightNumber = flightController.extractFlightNumber(selectedFlightInfo);

            // Open the PassengerListFrame for the selected flight
            new PassengerListFrame(selectedFlightNumber, databaseConnector).setVisible(true);
        });

        setVisible(true);
    }

    @Override
    public void loadList() {
        List<Item> flightList = itemLoader.loadFlights();
        displayItems(flightList);
    }

    @Override
    public void displayItems(List<Item> items) {
        for (Item item : items) {
            flightComboBox.addItem(item.getText());
        }
    }
}