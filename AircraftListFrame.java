
// AircraftListFrame.java
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AircraftListFrame extends JFrame implements ListLoader, Printer {
    private JTextArea aircraftListArea;
    private ItemLoader itemLoader;

    public AircraftListFrame(DatabaseConnector databaseConnector) {
        this.itemLoader = new ItemLoader(databaseConnector);

        setTitle("Flight Reservation - Aircraft List");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        // Text area to display the list of aircrafts
        aircraftListArea = new JTextArea();
        aircraftListArea.setEditable(false);

        // Scroll pane for the text area
        JScrollPane scrollPane = new JScrollPane(aircraftListArea);

        // Create the form layout
        setLayout(new BorderLayout());

        add(new JLabel("List of Aircrafts"), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Load and display the list of aircrafts
        loadList();
    }

    @Override
    public void loadList() {
        List<Item> aircraftList = itemLoader.loadAircrafts();
        displayItems(aircraftList);
    }

    @Override
    public void displayItems(List<Item> items) {
        StringBuilder itemText = new StringBuilder();
        for (Item item : items) {
            itemText.append(item.getText()).append("\n");
        }
        aircraftListArea.setText(itemText.toString());
    }
}