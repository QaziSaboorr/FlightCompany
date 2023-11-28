import java.sql.ResultSet;
import java.sql.SQLException;

public class Destination implements Item {
    private String destinationName;

    public Destination(ResultSet resultSet) throws SQLException {
        this.destinationName = resultSet.getString("DestinationName");
    }

    @Override
    public String getText() {
        return destinationName;
    }
}
