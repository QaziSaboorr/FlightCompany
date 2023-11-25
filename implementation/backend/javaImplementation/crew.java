package implementation.backend.javaImplementation;

public class crew {

    protected String crewName;
    protected int crewID;

    public crew(String crewName, int crewID) {
        this.crewName = crewName;
        this.crewID = crewID;
    }

    // Getters
    public String getCrewName() {
        return this.crewName;
    }

    public int getCrewID() {
        return this.crewID;
    }

    // Setters
    public void setCrewName(String crew) {
        this.crewName = crew;
    }

    public void setCrewID(int id) {
        this.crewID = id;
    }

    // Other methods
    public void print() {
        System.out.println(crewName); 
    }
 
}
