package implementation.backend.javaImplementation;

public class aircraft {

    protected String type;
    protected int number;

    public aircraft(String type, int number) {
        this.type = type;
        this.number = number;
    }

    // Getters
    public String getType() {
        return this.type;
    }

    public int getNumber() {
        return this.number;
    }

    // Setters
    public void setType(String t) {
        type = t;
    }

    public void setNumber(int num) {
        number = num;
    }

    // Other Methods
    public void print() {
        String output = type + " " + number;
        System.out.println(output);
    }
    
}
