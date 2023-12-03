import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

public class ManageController {
    
    private DatabaseConnector databaseConnector;

    public ManageController() {
        this.databaseConnector = DatabaseConnector.getInstance();
    }

    public void loadAircraftAndFlights(JComboBox<String> dropdown) {
        try (Connection connection = databaseConnector.getConnection()) {
            String query = "SELECT a.AircraftNumber, f.FlightNumber " +
                    "FROM Aircrafts a " +
                    "LEFT JOIN Flights f ON a.AircraftID = f.AircraftID";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String aircraftNumber = resultSet.getString("AircraftNumber");
                    String flightNumber = resultSet.getString("FlightNumber");
                    String aircraftInfo = aircraftNumber + " - " + (flightNumber != null ? flightNumber : "No Flight");
                    dropdown.addItem(aircraftInfo);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, this, "Error loading aircraft and flight information.", 0);
        }
    }

    // Function to load crews and their flights into the dropdown menu
    public void loadCrewsAndFlights(JComboBox<String> dropdown) {
        try (Connection connection = databaseConnector.getConnection()) {
            String query = "SELECT c.CrewID, c.Name, f.FlightNumber " +
                        "FROM Crews c " +
                        "LEFT JOIN Flights f ON c.FlightID = f.FlightID";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String crewInfo = resultSet.getString("Name") + " - " +
                                    (resultSet.getString("FlightNumber") != null ?
                                            resultSet.getString("FlightNumber") : "No Flight");
                    dropdown.addItem(crewInfo);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(dropdown, this, "Error loading crews and flights.", 0);
        }
    }

    // Function to load flight numbers into the combo box
    public void loadFlightNumbers(JComboBox<String> flightComboBox) {
        try (Connection connection = databaseConnector.getConnection()) {
            String query = "SELECT FlightNumber FROM Flights";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String flightNumber = resultSet.getString("FlightNumber");
                    flightComboBox.addItem(flightNumber);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(flightComboBox, this, "Error loading flight numbers.", 0);
        }
    }

    // Function to get the FlightID for a given flight number
    public int getFlightID(Connection connection, String flightNumber) throws SQLException {
        String query = "SELECT FlightID FROM Flights WHERE FlightNumber = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, flightNumber);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("FlightID");
                }
            }
        }
        return -1; // Return -1 if FlightID is not found
    }


    // Function to load destination names into the combo box
    public void loadDestinationNames(JComboBox<String> destinationComboBox) {
        try (Connection connection = databaseConnector.getConnection()) {
            String query = "SELECT DestinationName FROM Destinations";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String destinationName = resultSet.getString("DestinationName");
                    destinationComboBox.addItem(destinationName);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(destinationComboBox, this, "Error loading destination names.", 0);
        }
    }

    // Function to load aircraft numbers into the combo box
    public void loadAircraftNumbers(JComboBox<String> aircraftComboBox) {
        try (Connection connection = databaseConnector.getConnection()) {
            String query = "SELECT AircraftNumber FROM Aircrafts";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String aircraftNumber = resultSet.getString("AircraftNumber");
                    aircraftComboBox.addItem(aircraftNumber);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(aircraftComboBox, this, "Error loading aircraft numbers.", 0);
        }
    }

    public void removeFlight(String flightNumber) {
        try (Connection connection = databaseConnector.getConnection()) {
            // Update the flight information in the Flights table and set the entire row to null
            String query = "UPDATE Flights SET FlightNumber = null, Origin = null, Destination = null, AircraftID = 0 WHERE FlightNumber = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, flightNumber);
                int rowsUpdated = preparedStatement.executeUpdate();
    
                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(null, this, "Flight information removed successfully.", rowsUpdated, null);
                } else {
                    JOptionPane.showMessageDialog(null, this, "Flight not found with the given flight number.", rowsUpdated);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, this, "Error removing flight information.", 0);
        }
    }

    // Function to get the existing crew for a given flight ID
    public String getExistingCrew(Connection connection, int flightID) throws SQLException {
        String query = "SELECT Name FROM Crews WHERE FlightID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, flightID);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("Name");
                }
            }
        }
        return null; // Return null if there is no existing crew
    }   

        // Function to check if a flight with the given flight number already exists
    public boolean flightExists(Connection connection, String flightNumber) throws SQLException {
        String query = "SELECT FlightID FROM Flights WHERE FlightNumber = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, flightNumber);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next(); // Returns true if a matching flight is found
            }
        }
    }

    // Function to get the DestinationID for a given destination name
    public int getDestinationID(Connection connection, String destinationName) throws SQLException {
        String query = "SELECT DestinationID FROM Destinations WHERE DestinationName = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, destinationName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("DestinationID");
                }
            }
        }
        return -1; // Return -1 if DestinationID is not found
    }

    // Function to get the AircraftID for a given aircraft number
    public int getAircraftID(Connection connection, String aircraftNumber) throws SQLException {
        String query = "SELECT AircraftID FROM Aircrafts WHERE AircraftNumber = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, aircraftNumber);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("AircraftID");
                }
            }
        }
        return -1; // Return -1 if AircraftID is not found
    }


    public void addSeatsForFlight(Connection connection, int flightID) throws SQLException {
        // Define the seat data
        String[] seatNumbers = {"A1", "A2", "A3", "A4", "B1", "B2", "B3", "B4", "C1", "C2", "C3", "C4", "D1", "D2", "D3", "D4"};
        String[] seatTypes = {"Business-Class", "Business-Class", "Business-Class", "Business-Class", "Business-Class", "Business-Class", "Business-Class", "Business-Class", "Regular", "Regular", "Regular", "Regular", "Regular", "Regular", "Regular", "Regular"};
        double[] seatPrices = {200.00, 200.00, 200.00, 200.00, 200.00, 200.00, 200.00, 200.00, 100.00, 100.00, 100.00, 100.00, 100.00, 100.00, 100.00, 100.00};

        // Insert seat data into the Seats table for the given flightID
        String query = "INSERT INTO Seats (FlightID, SeatNumber, SeatType, SeatPrice) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            for (int i = 0; i < seatNumbers.length; i++) {
                preparedStatement.setInt(1, flightID);
                preparedStatement.setString(2, seatNumbers[i]);
                preparedStatement.setString(3, seatTypes[i]);
                preparedStatement.setDouble(4, seatPrices[i]);
                preparedStatement.executeUpdate();
            }
        }
    }

    

}
