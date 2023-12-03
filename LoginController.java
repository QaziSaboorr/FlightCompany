import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;


public class LoginController {

    private DatabaseConnector databaseConnector;

    public LoginController() {
        this.databaseConnector = DatabaseConnector.getInstance();
    }

    public void updateCreditCardStatus(String username, boolean hasCompanyCreditCard) {
        String query = "UPDATE Users SET HasCompanyCreditCard = ? WHERE UserName = ?";

        try (Connection connection = databaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setBoolean(1, hasCompanyCreditCard);
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
    
    public void displayUserTicket(String username) {
        // Query to retrieve ticket information based on the user's username
        String query = "SELECT * FROM Tickets WHERE UserName = ?";
    
        try (Connection connection = databaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
    
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // Extract ticket information from the ResultSet
                    int ticketID = resultSet.getInt("TicketID");
                    int userID = resultSet.getInt("UserID");
                    String email = resultSet.getString("Email");
                    String flightID = resultSet.getString("FlightID");
                    int seatID = resultSet.getInt("SeatID");
                    String seatType = resultSet.getString("SeatType");
                    String seatNumber = resultSet.getString("SeatNumber");
                    String destination = resultSet.getString("Destination");
                    boolean isCancelled = resultSet.getBoolean("IsCancelled");
                    boolean insuranceSelected = resultSet.getBoolean("InsuranceSelected");
                    double paymentAmount = resultSet.getDouble("PaymentAmount");
                    boolean emailSent = resultSet.getBoolean("EmailSent");
                    boolean receiptSent = resultSet.getBoolean("ReceiptSent");
    
                    // Display the ticket information
                    StringBuilder ticketInfo = new StringBuilder();
                    ticketInfo.append("Ticket ID: ").append(ticketID).append("\n");
                    ticketInfo.append("User ID: ").append(userID).append("\n");
                    ticketInfo.append("Email: ").append(email).append("\n");
                    ticketInfo.append("Flight ID: ").append(flightID).append("\n");
                    ticketInfo.append("Seat ID: ").append(seatID).append("\n");
                    ticketInfo.append("Seat Type: ").append(seatType).append("\n");
                    ticketInfo.append("Seat Number: ").append(seatNumber).append("\n");
                    ticketInfo.append("Destination: ").append(destination).append("\n");
                    ticketInfo.append("Is Cancelled: ").append(isCancelled).append("\n");
                    ticketInfo.append("Insurance Selected: ").append(insuranceSelected).append("\n");
                    ticketInfo.append("Payment Amount: $").append(paymentAmount).append("\n");
                    ticketInfo.append("Email Sent: ").append(emailSent).append("\n");
                    ticketInfo.append("Receipt Sent: ").append(receiptSent).append("\n");
    
                    // Display the ticket information 
                    JOptionPane.showMessageDialog(null, ticketInfo.toString(), "Ticket Details", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "No ticket found for the user.", "Ticket Details", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean checkCredentials(String username, String password) {
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

    
}
