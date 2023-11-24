package implementation.backend.javaImplementation;

public class registeredUser extends user {

    private user registeredPerson;


    public registeredUser(user regUser, int userID) {
        super(regUser.getPerson(), regUser.getUserID());
        registeredPerson = regUser;
    }

    
    
}