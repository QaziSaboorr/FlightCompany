import javax.swing.*;


public class UserHomeFrame extends JFrame {
    private DatabaseConnector databaseConnector;

    public UserHomeFrame(DatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;

        setTitle("Flight Reservation - User");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        JButton flightSelectionButton = new JButton("Select Flights");
        JButton cancelFlightButton = new JButton("Cancel Flight");
        JButton accessLoungeButton = new JButton("Access Lounge");

        add(flightSelectionButton);
        add(cancelFlightButton);
        add(accessLoungeButton);

        flightSelectionButton.addActionListener(e -> selectFlight());
        cancelFlightButton.addActionListener(e -> cancelFlight());
        accessLoungeButton.addActionListener(e -> accessLounge());


        setVisible(true);
    }

    // FlightSelectionFrame flightSelectionFrame = new FlightSelectionFrame(userType, databaseConnector);
    // flightSelectionFrame.setVisible(true);

    private void selectFlight() {
        // Open the FlightSelectionFrame
        FlightSelectionFrame flightSelectionFrame = new FlightSelectionFrame(UserType.Unregistered, databaseConnector);
        flightSelectionFrame.setVisible(true);

    }

    private void cancelFlight() {

    }

    private void accessLounge() {

    }

}
