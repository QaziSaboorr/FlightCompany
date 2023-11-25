package implementation.backend.javaImplementation;

public class person {

    protected name name;
    protected birthday birthday;
    protected address address;
    protected phoneNumber phone;
    protected emailAddress eAddress;
    protected int personID;

    public person(name name, birthday birthday, address address, phoneNumber phone, emailAddress eaAddress, int personID) {
        this.eAddress = eaAddress;
        this.phone = phone;
        this.address = address;
        this.birthday = birthday;
        this.name = name;
        this.personID = personID;
    }

    // Getters
    public name getName() {
        return this.name;
    }

    public birthday getBirthday() {
        return this.birthday;
    }

    public address getAddress() {
        return this.address;
    }

    public phoneNumber getPhone() {
        return this.phone;
    }

    public emailAddress getEAddress() {
        return this.eAddress;
    }

    public int getPersonID() {
        return this.personID;
    }

    // Setters
    public void setName(name name) {
        this.name = name;
    }

    public void setBirthday(birthday birthday) {
        this.birthday = birthday;
    }

    public void setAddress(address address) {
        this.address = address;
    }

    public void setPhone(phoneNumber phone) {
        this.phone = phone;
    }

    public void setEAddress(emailAddress eAddress) {
        this.eAddress = eAddress;
    }

    public void setPersonID(int id) {
        this.personID = id;
    }
}
