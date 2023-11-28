import javax.swing.*;
import java.awt.*;
import java.util.List;

public class FlightAttendantFrame extends JFrame implements ListLoader{
    private JComboBox<String> flightComboBox;
    private JButton viewPassengerListButton;

    private ItemLoader itemLoader;

    public FlightAttendantFrame(DatabaseConnector databaseConnector) {
        this.itemLoader = new ItemLoader(databaseConnector);

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
            String selectedFlightNumber = extractFlightNumber(selectedFlightInfo);

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
}
