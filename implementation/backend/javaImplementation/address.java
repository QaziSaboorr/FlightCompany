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

    // --- GETTERS ---
    public int getNumber() {
        return this.number;
    }

    public String getStreet() {
        return this.street;
    }

    public String getCity() {
        return this.city;
    }

    public String getCountry() {
        return this.country;
    }

    public String getPostalCode() {
        return this.postalCode;
    }

    // --- SETTERS ---
    public void setNumber(int num) {
        number = num;
    }

    public void setStreet(String s) {
        street = s;
    }

    public void setCity(String c) {
        city = c;
    }

    public void setCountry(String c) {
        country = c;
    }

    public void setPostalCode(String pc) {
        postalCode = pc;
    }

    // --- METHODS --- 
    public void print() {
        String output = number + " " + street + " " + city + " " + country + " " + postalCode;
        Sytem.out.println(output);
    }
}
