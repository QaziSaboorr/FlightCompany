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
    
}
