package implementation.backend.javaImplementation;

public class person {

    protected name name;
    protected birthday birthday;
    protected address address;
    protected phoneNumber phone;
    protected emailAddress eAddress;

    public person(name name, birthday birthday, address address, phoneNumber phone, emailAddress eaAddress) {
        this.eAddress = eaAddress;
        this.phone = phone;
        this.address = address;
        this.birthday = birthday;
        this.name = name;
    }

    // Getters
    public name getName() {
        return name;
    }

    public birthday getBirthday() {
        return birthday;
    }

    public address getAddress() {
        return address;
    }

    public phoneNumber getPhone() {
        return phone;
    }

    public emailAddress getEAddress() {
        return eAddress;
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
}
