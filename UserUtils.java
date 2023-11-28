



import javax.swing.*;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserUtils {

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

    // Add other common methods as needed
}
