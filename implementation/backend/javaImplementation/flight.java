package implementation.backend.javaImplementation;

import java.util.ArrayList;

public class flight {

    protected ArrayList<ticket> tickets;
    protected seatMap map;
    protected ArrayList<crew> crews;
    protected aircraft plane;
    protected destination dest;

    public flight(ArrayList<ticket> tickets, seatMap map, 
        ArrayList<crew> crews, aircraft plane, destination dest) {
            this.tickets = tickets;
            this.map = map;
            this.crews = crews;
            this.plane = plane;
            this.dest = dest;   
    }


}
