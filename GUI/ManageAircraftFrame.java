// ManageAircraftFrame.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ManageAircraftFrame extends JFrame {
    private JTextField aircraftNumberField;
    private JButton addButton;
    private DatabaseConnector databaseConnector;

    public ManageAircraftFrame(DatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;

        setTitle("Flight Reservation - Manage Aircraft");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 150);
        setLocationRelativeTo(null);

        // Form components
        aircraftNumberField = new JTextField();
        addButton = new JButton("Add Aircraft");

        // Create the form layout
        setLayout(new GridLayout(3, 2));

        add(new JLabel("Aircraft Number:"));
        add(aircraftNumberField);
        add(new JLabel());
        add(addButton);

        // Add action listener for the "Add Aircraft" button
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addAircraft();
            }
        });
    }

    // Function to add a new aircraft to the database
    private void addAircraft() {
        String aircraftNumber = aircraftNumberField.getText();

        if (aircraftNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an aircraft number.");
            return;
        }

        try (Connection connection = databaseConnector.getConnection()) {
            String query = "INSERT INTO Aircrafts (AircraftNumber) VALUES (?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, aircraftNumber);
                preparedStatement.executeUpdate();
                JOptionPane.showMessageDialog(this, "Aircraft added successfully.");
                aircraftNumberField.setText(""); // Clear the input field after adding
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding aircraft.");
        }
    }
}
