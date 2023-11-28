
public class FlightSelectionController {
    private DatabaseConnector databaseConnector;

    public FlightSelectionController(DatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;
    }

    public void handleFlightSelection(UserType userType) {
        FlightSelectionFrame flightSelectionFrame = new FlightSelectionFrame(userType, databaseConnector);
        flightSelectionFrame.setVisible(true);
    }
}
