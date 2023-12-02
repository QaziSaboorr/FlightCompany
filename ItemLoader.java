import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemLoader {
    private DatabaseConnector databaseConnector;

    public ItemLoader(DatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;
    }

    public List<Item> loadAircrafts() {
        return loadData("SELECT AircraftNumber FROM Aircrafts", Aircraft::new);
    }

    public List<Item> loadCrews() {
        return loadData("SELECT Name FROM Crews", Crew::new);
    }

    public List<Item> loadFlights() {
        return loadData("SELECT FlightNumber, Origin, Destination FROM Flights", Flight::new);
    }


    private List<Item> loadData(String query, DataItem factory) {
        List<Item> items = new ArrayList<>();
        try (Connection connection = databaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                items.add(factory.createItem(resultSet));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return items;
    }

    interface DataItem{
        Item createItem(ResultSet resultSet) throws SQLException;
    }
}
