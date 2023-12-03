import java.sql.ResultSet;
import java.sql.SQLException;

public class FlightAttendant implements Item {
    private String flightInfo;

    public FlightAttendant(ResultSet resultSet) throws SQLException {
        this.flightInfo = resultSet.getString("FlightNumber") + " - " +
                resultSet.getString("Origin") + " to " +
                resultSet.getString("Destination");
    }

    @Override
    public String getText() {
        return flightInfo;
    }
}
