package implementation.backend.javaImplementation;

public class aircraft {

    protected String type;
    protected int number;

    public aircraft(String type, int number) {
        this.type = type;
        this.number = number;
    }

    // --- GETTERS ---
    public String getType() {
        return this.type;
    }

    public int getNumber() {
        return this.number;
    }

    // --- SETTERS ---
    public void setType(String t) {
        type = t;
    }

    public void setNumber(int num) {
        number = num;
    }

    // --- METHODS ---
    public void print() {
        String output = type + " " + number;
        System.out.println(output);
    }
    
}
