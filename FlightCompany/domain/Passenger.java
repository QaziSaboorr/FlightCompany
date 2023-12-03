package domain;
public class Passenger implements Item{
    private String name;
    private String seatNumber;

    public Passenger(String name, String seatNumber) {
        this.name = name;
        this.seatNumber = seatNumber;
    }

    @Override
    public String getText() {
        return "Name: " + name + ", Seat: " + seatNumber;
    }
}
