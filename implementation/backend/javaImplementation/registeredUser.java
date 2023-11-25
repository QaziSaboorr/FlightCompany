package implementation.backend.javaImplementation;

public class registeredUser extends user {

    private user registeredUser;
    private String password;


    public registeredUser(user regUser, String password) {
        super(regUser.getName(), regUser.getBirthday(), regUser.getAddress(), 
            regUser.getPhone(), regUser.getEAddress(), regUser.getPersonID());
        registeredUser = regUser;
        this.password = password;
    }
    
}