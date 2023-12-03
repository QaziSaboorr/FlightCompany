// FlightListFrame.java
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class FlightListFrame extends JFrame implements ListLoader, Printer {
    private JTextArea flightListArea;
    private ItemLoader itemLoader;

    public FlightListFrame() {
        this.itemLoader = new ItemLoader();

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
        loadList();
    }
    
    // Overrided function to get the Flight List
    @Override
    public void loadList() {
        List<Item> flightList = itemLoader.loadFlights();
        displayItems(flightList);
    }

    // Overrided function to display the Item (Flight)
    @Override
    public void displayItems(List<Item> items) {
        StringBuilder itemText = new StringBuilder();
        for (Item item : items) {
            itemText.append(item.getText()).append("\n");
        }
        flightListArea.setText(itemText.toString());
    }
}
