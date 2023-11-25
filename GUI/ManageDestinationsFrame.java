// ManageDestinationFrame.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ManageDestinationsFrame extends JFrame {
    private JTextField destinationNameField;
    private JButton addButton;
    private DatabaseConnector databaseConnector;

    public ManageDestinationsFrame(DatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;

        setTitle("Flight Reservation - Manage Destination");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 150);
        setLocationRelativeTo(null);

        // Form components
        destinationNameField = new JTextField();
        addButton = new JButton("Add Destination");

        // Create the form layout
        setLayout(new GridLayout(3, 2));

        add(new JLabel("Destination Name:"));
        add(destinationNameField);
        add(new JLabel());
        add(addButton);

        // Add action listener for the "Add Destination" button
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addDestination();
            }
        });
    }

    // Function to add a new destination to the database
    private void addDestination() {
        String destinationName = destinationNameField.getText();

        if (destinationName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a destination name.");
            return;
        }

        try (Connection connection = databaseConnector.getConnection()) {
            String query = "INSERT INTO Destinations (DestinationName) VALUES (?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, destinationName);
                preparedStatement.executeUpdate();
                JOptionPane.showMessageDialog(this, "Destination added successfully.");
                destinationNameField.setText(""); // Clear the input field after adding
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding destination.");
        }
    }
}
