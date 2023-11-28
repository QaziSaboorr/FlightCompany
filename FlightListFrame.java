import javax.swing.*;
import java.awt.*;
import java.util.List;

public class FlightListFrame extends JFrame implements ListLoader, Printer {
    private JTextArea itemListArea;
    private ItemLoader itemLoader;

    public FlightListFrame(DatabaseConnector databaseConnector) {
        this.itemLoader= new ItemLoader(databaseConnector);

        setTitle("Flight Reservation - Flight List");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        // Text area to display the list of items
        itemListArea = new JTextArea();
        itemListArea.setEditable(false);

        // Scroll pane for the text area
        JScrollPane scrollPane = new JScrollPane(itemListArea);

        // Create the form layout
        setLayout(new BorderLayout());

        add(new JLabel("List of Flights"), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Load and display the list of items
        loadList();
    }

    @Override
    public void loadList() {
        List<Item> flightList = itemLoader.loadFlights();
        displayItems(flightList);
    }

    @Override
    public void displayItems(List<Item> items) {
        StringBuilder itemText = new StringBuilder();
        for (Item item : items) {
            itemText.append(item.getText()).append("\n");
        }
        itemListArea.setText(itemText.toString());
    }
}
