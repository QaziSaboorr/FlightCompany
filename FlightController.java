public class FlightController {

    public FlightController() {
    }

    public void handleFlightSelection(UserType userType) {
        FlightSelectionFrame flightSelectionFrame = new FlightSelectionFrame(userType);
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
