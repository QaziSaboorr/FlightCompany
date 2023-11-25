package implementation.backend.javaImplementation;

public class flightAttendant extends user{

    protected user attendantUser;
    protected String password;

    public flightAttendant(user attendant, String password) {
        super(attendant.getName(), attendant.getBirthday(), attendant.getAddress(), 
            attendant.getPhone(), attendant.getEAddress(), attendant.getPersonID());
        attendantUser = attendant;
        this.password = password;
    }

    public void browsePassengers() {

    }
    
}
