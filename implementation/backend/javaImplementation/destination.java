package implementation.backend.javaImplementation;

public class destination {

    protected String city;
    protected String country;
    protected int destinationID;

    public destination(String city, String country, int destinationID) {
        this.city = city;
        this.country = country;
        this.destinationID = destinationID;
    }
    
    // Getters
    public String getCity() {
        return this.city;
    }

    public String getCountry() {
        return this.country;
    }

    public int getDestinationID() {
        return this.destinationID;
    }

    // Setters
    public void setCity(String c) {
        this.city = c;
    }

    public void setCountry(String c) {
        this.country = c;
    }

    public void setDestinationID(int id) {
        this.destinationID = id;
    }
    
}
