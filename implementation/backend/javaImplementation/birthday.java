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

    // Getters
    public int getYear() {
        return this.year;
    }

    public int getDay() {
        return this.day;
    }
    
    public int getMonth() {
        return this.month;
    }

    // Setters
    public void setYear(int y) {
        this.year = y;
    }

    public void setDay(int d) {
        this.day = d;
    }

    public void setMonth(int m) {
       this.month = m;
    }

    // Other Methods
    public void print() {
        String output = day + "/" + month + "/" + year;
        System.out.println(output);
    }
}
