import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseController {

    private DatabaseConnector databaseConnector;

    public DatabaseController(DatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;
    }

    public void addUserToDatabase(String username, String email) {
        String query = "INSERT INTO Users (UserName, Email, UserType) VALUES (?, ?, ?)";
        try (Connection connection = databaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, UserType.Unregistered.name()); // Set UserType to Unregistered

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean authenticateUser(UserType userType, String username, String password) {
        // Implement user authentication logic here (query the database, etc.)
        // For simplicity, compare with the fake data
        switch (userType) {
            case Registered:
            case AirlineAgent:
            case SystemAdmin:
            case FlightAttendant:
                return checkCredentials(username, password);
            default:
                return false;
        }
    }


    private boolean checkCredentials(String username, String password) {
        String query = "SELECT * FROM Users WHERE UserName = ? AND Password = ?";

        try (Connection connection = databaseConnector.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next(); // Returns true if a matching user is found
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean checkUserHasTicket(String username) {
        String query = "SELECT * FROM Tickets WHERE UserName = ?";
    
        try (Connection connection = databaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
    
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next(); // Returns true if the user has a ticket
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return false;
    }

    public void cancelTicket(String username) {
        // Implement the logic to cancel the user's ticket by removing the corresponding row from the Tickets table
        String query = "DELETE FROM Tickets WHERE UserName = ?";
    
        try (Connection connection = databaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.executeUpdate();
    
            JOptionPane.showMessageDialog(null, this, "Ticket Cancelled!", 0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        // Additional logic for ticket cancellation (e.g., updating UI)
        // You may need to refresh the UI or close the current frame, depending on your application flow.
    }



    public boolean getMembershipStatus(String username) {
        String query = "SELECT IsMember FROM Users WHERE UserName = ?";

        try (Connection connection = databaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBoolean("IsMember");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; // Default value
    }

    public void updateMembershipStatus(String username, boolean isMember) {
        String query = "UPDATE Users SET IsMember = ? WHERE UserName = ?";

        try (Connection connection = databaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setBoolean(1, isMember);
            preparedStatement.setString(2, username);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean getCompanyCreditCardStatus(String username) {
        String query = "SELECT HasCompanyCreditCard FROM Users WHERE UserName = ?";

        try (Connection connection = databaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBoolean("HasCompanyCreditCard");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; // Default value
    }


    public void updateCompanionTicketRedemptionStatus(String username, boolean hasRedeemedCompanionTicket) {
        String query = "UPDATE Users SET HasRedeemedCompanionTicket = ? WHERE UserName = ?";

        try (Connection connection = databaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setBoolean(1, hasRedeemedCompanionTicket);
            preparedStatement.setString(2, username);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean getCompanionTicketRedemptionStatus(String username) {
        String query = "SELECT HasRedeemedCompanionTicket FROM Users WHERE UserName = ?";

        try (Connection connection = databaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBoolean("HasRedeemedCompanionTicket");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; // Default value
    }


    

}
