package implementation.backend.javaImplementation;

public class address {

    protected int number;
    protected String street;
    protected String city;
    protected String country;
    protected String postalCode;
    
    public address(int number, String street, 
        String city, String country, String postalCode) {

            this.number = number;
            this.street = street;
            this.city = city;
            this.country = country;
            this.postalCode = postalCode;
    }
}
