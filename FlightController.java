public class FlightController {
    private DatabaseConnector databaseConnector;

    public FlightController(DatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;
    }

    public void handleFlightSelection(UserType userType) {
        FlightSelectionFrame flightSelectionFrame = new FlightSelectionFrame(userType, databaseConnector);
        flightSelectionFrame.setVisible(true);
    }

    public String extractFlightNumber(String flightInfo) {
        int endIndex = flightInfo.indexOf(" -");
        if (endIndex != -1) {
            return flightInfo.substring(0, endIndex);
        } else {
            return flightInfo;
        }
    }
}
