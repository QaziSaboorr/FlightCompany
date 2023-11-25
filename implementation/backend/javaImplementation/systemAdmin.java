package implementation.backend.javaImplementation;


public class systemAdmin extends user {

    private user systemUser;
    private String password;

    public systemAdmin(user system, String password) {
        super(system.getName(), system.getBirthday(), system.getAddress(), 
            system.getPhone(), system.getEAddress(), system.getPersonID());
        systemUser = system;
        this.password = password;
    }
    
}