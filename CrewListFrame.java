import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CrewListFrame extends JFrame implements ListLoader {
    private JTextArea itemListArea;
    private ItemLoader itemLoader;

    public CrewListFrame(DatabaseConnector databaseConnector) {
        this.itemLoader = new ItemLoader(databaseConnector);

        setTitle("Flight Reservation - Crew List");
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

        add(new JLabel("List of Crews"), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Load and display the list of items
        loadList();
    }

    @Override
    public void loadList() {
        List<Item> crewList = itemLoader.loadCrews();
        displayItems(crewList);
    }

    public void displayItems(List<Item> items) {
        StringBuilder itemText = new StringBuilder();
        for (Item item : items) {
            itemText.append(item.getText()).append("\n");
        }
        itemListArea.setText(itemText.toString());
    }
}
