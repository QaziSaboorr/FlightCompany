package presentation;
import javax.swing.*;

import controllers.FlightController;
import domain.Item;
import domain.ItemLoader;
import domain.ListLoader;
import domain.Printer;

import java.util.List;

public class FlightAttendantFrame extends JFrame implements ListLoader, Printer {
    private JComboBox<String> flightComboBox;
    private JButton viewPassengerListButton;

    private ItemLoader itemLoader;
    private FlightController flightController;

    public FlightAttendantFrame() {
        this.itemLoader = new ItemLoader();

        this.flightController = new FlightController();

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
            new PassengerListFrame(selectedFlightNumber).setVisible(true);
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
