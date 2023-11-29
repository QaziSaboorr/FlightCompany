import java.sql.ResultSet;
import java.sql.SQLException;

public class Crew implements Item {
    private String name;

    public Crew(ResultSet resultSet) throws SQLException {
        this.name = resultSet.getString("Name");
    }

    @Override
    public String getText() {
        return name;
    }
}
