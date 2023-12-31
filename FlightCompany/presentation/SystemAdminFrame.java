package presentation;
import javax.swing.*;


public class SystemAdminFrame extends JFrame {

    public SystemAdminFrame() {

        setTitle("Flight Reservation - System Admin");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        JButton browseFlightsButton = new JButton("Browse Flights");
        JButton browseCrewsButton = new JButton("Browse Crews");
        JButton browseAircraftsButton = new JButton("Browse Aircrafts");
        JButton addRemoveCrewButton = new JButton("Add/Remove Crew");
        JButton addRemoveAircraftButton = new JButton("Add/Remove Aircraft");
        JButton addRemoveDestinationsButton = new JButton("Add/Remove Destinations");
        JButton modifyFlightsInfoButton = new JButton("Modify Flights Information");
        JButton printUsersListButton = new JButton("Print Users List");

        add(browseFlightsButton);
        add(browseCrewsButton);
        add(browseAircraftsButton);
        add(addRemoveCrewButton);
        add(addRemoveAircraftButton);
        add(addRemoveDestinationsButton);
        add(modifyFlightsInfoButton);
        add(printUsersListButton);

        browseFlightsButton.addActionListener(e -> browseFlights());
        browseCrewsButton.addActionListener(e -> browseCrews());
        browseAircraftsButton.addActionListener(e -> browseAircrafts());
        addRemoveCrewButton.addActionListener(e -> addRemoveCrew());
        addRemoveAircraftButton.addActionListener(e -> addRemoveAircraft());
        addRemoveDestinationsButton.addActionListener(e -> addRemoveDestinations());
        modifyFlightsInfoButton.addActionListener(e -> modifyFlightsInfo());
        printUsersListButton.addActionListener(e -> printUsersList());

        setVisible(true);
    }

    private void browseFlights() {
        // Open the FlightListFrame
        new FlightListFrame().setVisible(true);
    }

    private void browseCrews() {
        // Open the CrewListFrame
        new CrewListFrame().setVisible(true);
    }

    private void browseAircrafts() {
        // Open the AircraftListFrame
        new AircraftListFrame().setVisible(true);
    }

    private void addRemoveCrew() {
        // Open the ManageCrewFrame
        new ManageCrewFrame().setVisible(true);
    }

    private void addRemoveAircraft() {
        // Open the ManageAircraftFrame
        new ManageAircraftFrame().setVisible(true);
    }

    private void addRemoveDestinations() {
        // Open the ManageDestinationFrame
        new ManageDestinationsFrame().setVisible(true);
    }

    private void modifyFlightsInfo() {
        // Open the ManageFlightsFrame
        new ManageFlightsFrame().setVisible(true);
    }

    private void printUsersList() {
        // Open a frame or perform actions to print the list of users
        new UserListFrame().setVisible(true);
    }

 
    }