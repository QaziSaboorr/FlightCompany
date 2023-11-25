package src;
import javax.swing.*;

import src.common.DatabaseConnector;
import src.common.LoginFrame;

public class FlightReservationApp {
    public static void main(String[] args) {
        // Ensure the MySQL JDBC driver is loaded
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading MySQL JDBC Driver!");
            return;
        }

        // Create a DatabaseConnector object to handle database connectivity
        DatabaseConnector databaseConnector = new DatabaseConnector();

        // Pass the database connector to the LoginFrame
        SwingUtilities.invokeLater(() -> new LoginFrame(databaseConnector).setVisible(true));
    }
}
