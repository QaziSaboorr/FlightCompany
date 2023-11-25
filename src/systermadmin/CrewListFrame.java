package src.systermadmin;
// CrewListFrame.java
import javax.swing.*;

import src.common.DatabaseConnector;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CrewListFrame extends JFrame {
    private JTextArea crewListArea;
    private DatabaseConnector databaseConnector;

    public CrewListFrame(DatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;

        setTitle("Flight Reservation - Crew List");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        // Text area to display the list of crews
        crewListArea = new JTextArea();
        crewListArea.setEditable(false);

        // Scroll pane for the text area
        JScrollPane scrollPane = new JScrollPane(crewListArea);

        // Create the form layout
        setLayout(new BorderLayout());

        add(new JLabel("List of Crews"), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Load and display the list of crews
        loadCrews();
    }

    // Function to load and display the list of crews from the database
    private void loadCrews() {
        try (Connection connection = databaseConnector.getConnection()) {
            String query = "SELECT Name FROM Crews";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                StringBuilder crewList = new StringBuilder();
                while (resultSet.next()) {
                    String crewInfo = resultSet.getString("Name");
                    crewList.append(crewInfo).append("\n");
                }
                crewListArea.setText(crewList.toString());
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading crew list.");
        }
    }
}