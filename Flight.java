import java.sql.ResultSet;
import java.sql.SQLException;

public class Flight implements Item {
    private String flightNumber;
    private String origin;
    private String destination;

    public Flight(ResultSet resultSet) throws SQLException {
        this.flightNumber = resultSet.getString("FlightNumber");
        this.origin = resultSet.getString("Origin");
        this.destination = resultSet.getString("Destination");
    }

    @Override
    public String getText() {
        return flightNumber + " - " + origin + " to " + destination;
    }
}
