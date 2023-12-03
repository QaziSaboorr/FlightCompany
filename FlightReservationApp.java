
import javax.swing.*;

public class FlightReservationApp {
    public static void main(String[] args) {
        // make sure the MySQL JDBC driver is loaded
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading MySQL JDBC Driver!");
            return;
        }

        // Create a DatabaseConnector singleton instance
        DatabaseConnector databaseConnector = DatabaseConnector.getInstance();

        // Pass the database connector to the LoginFrame
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
