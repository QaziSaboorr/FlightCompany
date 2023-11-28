import javax.swing.*;


public class UserHomeFrame extends JFrame {

    private UserController userController;

    public UserHomeFrame(DatabaseConnector databaseConnector) {

        userController = new UserController(databaseConnector);

        setTitle("Flight Reservation - User");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        JButton flightSelectionButton = new JButton("Select Flights");

        add(flightSelectionButton);

        flightSelectionButton.addActionListener(e -> userController.selectFlight(UserType.Unregistered));


        setVisible(true);
    }
}
