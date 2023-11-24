package implementation.backend.javaImplementation;

public class user extends person{

    private person userPerson;
    private int userID;

    public user(person user, int userID) {
        super(user.getName(), user.getBirthday(), 
            user.getAddress(), user.getPhone(), user.getEAddress());
        userPerson = user;
        this.userID = userID;
    }

    // Getters
    public int getUserID() {
        return userID;
    }

    public person getPerson() {
        return userPerson;
    }

    // Setters
    public void setUserID(int id) {
        this.userID = id;
    }

    public void setUserPerson(person person) {
        this.userPerson = person;
    }

    

}

