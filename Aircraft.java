import java.sql.ResultSet;
import java.sql.SQLException;

public class Aircraft implements Item {
    private String aircraftNumber;

    public Aircraft(ResultSet resultSet) throws SQLException {
        this.aircraftNumber = resultSet.getString("AircraftNumber");
    }

    @Override
    public String getText() {
        return aircraftNumber;
    }
}
