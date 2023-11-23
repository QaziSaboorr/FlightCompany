package implementation.backend.javaImplementation;

public class birthday {
    
    protected int year;
    protected int day;
    protected int month;

    public birthday(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    // --- GETTERS ---
    public int getYear() {
        return this.year;
    }

    public int getDay() {
        return this.day;
    }
    
    public int getMonth() {
        return this.month;
    }

    // --- SETTERS ---
    public void setYear(int y) {
        year = y;
    }

    public void setDay(int d) {
        day = d;
    }

    public void setMonth(int m) {
        month = m;
    }

    // --- METHODS ---
    public void print() {
        String output = day + "/" + month + "/" + year;
        System.out.println(output);
    }
}
