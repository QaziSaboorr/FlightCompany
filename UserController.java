import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserController {

    private DatabaseConnector databaseConnector;

    public UserController(DatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;
    }
    
    public static void updateCreditCardStatus(DatabaseConnector databaseConnector, String username, boolean hasCompanyCreditCard) {
        String query = "UPDATE Users SET HasCompanyCreditCard = ? WHERE UserName = ?";

        try (Connection connection = databaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setBoolean(1, hasCompanyCreditCard);
            preparedStatement.setString(2, username);

            preparedStatement.executeUpdate();


            JOptionPane.showMessageDialog(null, "Congratulations! You have successfully applied for a credit card.",
                "Credit Card Application", JOptionPane.INFORMATION_MESSAGE);
      
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getUserFlights(DatabaseConnector databaseConnector, String username) {
        List<String> userFlights = new ArrayList<>();
        String query = "SELECT Flight FROM UserFlights WHERE UserName = ?";

        try (Connection connection = databaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String flight = resultSet.getString("Flight");
                    userFlights.add(flight);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userFlights;
    }

    public void cancelSelectedFlight(String selectedFlight, String username) {
        // Implement the cancellation logic here, e.g., delete the selected flight from the database
        String deleteQuery = "DELETE FROM UserFlights WHERE UserName = ? AND Flight = ?";

        try (Connection connection = databaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, selectedFlight);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void selectFlight() {
        // Open the FlightSelectionFrame
        FlightSelectionFrame flightSelectionFrame = new FlightSelectionFrame(UserType.Unregistered, databaseConnector);
        flightSelectionFrame.setVisible(true);

    }

}
